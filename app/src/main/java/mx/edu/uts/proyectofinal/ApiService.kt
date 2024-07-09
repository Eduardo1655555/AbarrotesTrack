package mx.edu.uts.proyectofinal

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("products")
    fun getProducts(): Call<List<Product>>

    @POST("products")
    fun addProduct(@Body product: Product): Call<Product>

    @PUT("products/{id}")
    fun updateProduct(@Path("id") id: Int, @Body product: Product): Call<Product>

    @DELETE("products/{id}")
    fun deleteProduct(@Path("id") id: Int): Call<Void>
}
