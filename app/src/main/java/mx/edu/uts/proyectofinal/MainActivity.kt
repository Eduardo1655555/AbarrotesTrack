package mx.edu.uts.proyectofinal

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), ProductAdapter.OnItemClickListener {

    private lateinit var buttonAddProduct: Button
    private lateinit var buttonRefresh: ImageButton
    private lateinit var buttonLogout: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var productList = mutableListOf<Product>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)

        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        buttonAddProduct = findViewById(R.id.buttonAddProduct)
        buttonRefresh = findViewById(R.id.buttonRefresh)
        buttonLogout = findViewById(R.id.buttonLogout)
        recyclerView = findViewById(R.id.recyclerView)

        adapter = ProductAdapter(productList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonAddProduct.setOnClickListener {
            val intent = Intent(this, ProductFormActivity::class.java)
            startActivity(intent)
        }

        buttonRefresh.setOnClickListener {
            getProducts()
        }

        buttonLogout.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        getProducts()
    }

    private fun getProducts() {
        ApiClient.apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    productList.clear()
                    response.body()?.let { productList.addAll(it) }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                // Handle failure
                Log.d("ERROR", "Response: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private fun deleteProduct(productId: Int) {
        ApiClient.apiService.deleteProduct(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val index = productList.indexOfFirst { it.id == productId }
                    if (index != -1) {
                        productList.removeAt(index)
                        adapter.notifyItemRemoved(index)
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure
                t.printStackTrace()
            }
        })
    }

    override fun onDeleteClick(product: Product) {
        product.id?.let {
            deleteProduct(it)
        } ?: run {
            Toast.makeText(this, "Error: El producto no tiene un ID válido", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEditClick(product: Product) {
        val intent = Intent(this, ProductFormActivity::class.java)
        intent.putExtra("productId", product.id)
        intent.putExtra("productName", product.name)
        intent.putExtra("productDescription", product.description)
        intent.putExtra("productPrice", product.price)
        startActivity(intent)
    }
}
