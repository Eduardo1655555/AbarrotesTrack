package mx.edu.uts.proyectofinal


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductFormActivity : AppCompatActivity() {

    private lateinit var editTextId: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_form)

        editTextId = findViewById(R.id.editTextId)
        editTextName = findViewById(R.id.editTextName)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextPrice = findViewById(R.id.editTextPrice)
        buttonSave = findViewById(R.id.buttonSave)

        val productId = intent.getIntExtra("productId", -1)
        if (productId != -1) {
            editTextId.setText(productId.toString())
            editTextName.setText(intent.getStringExtra("productName"))
            editTextDescription.setText(intent.getStringExtra("productDescription"))
            editTextPrice.setText(intent.getDoubleExtra("productPrice", 0.0).toString())
        } else {
            editTextId.visibility = View.GONE
        }

        buttonSave.setOnClickListener {
            val name = editTextName.text.toString()
            val description = editTextDescription.text.toString()
            val price = editTextPrice.text.toString().toDouble()
            if (productId == -1) {
                addProduct(name, description, price)
            } else {
                updateProduct(productId, name, description, price)
            }
        }
    }

    private fun addProduct(name: String, description: String, price: Double) {
        val product = Product(name = name, description = description, price = price)
        ApiClient.apiService.addProduct(product).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductFormActivity, "Producto añadido", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun updateProduct(id: Int, name: String, description: String, price: Double) {
        val product = Product(id = id, name = name, description = description, price = price)
        ApiClient.apiService.updateProduct(id, product).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductFormActivity, "Product updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
