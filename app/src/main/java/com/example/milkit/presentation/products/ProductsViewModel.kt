package com.example.milkit.presentation.products

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.milkit.data.repo.CartRepository
import com.example.milkit.data.room.MyRoomDatabase
import com.example.milkit.presentation.cart.CartModel
import com.example.milkit.presentation.home.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductsViewModel:ViewModel() {

    val firebaseFireStore : FirebaseFirestore = FirebaseFirestore.getInstance()

    val allProducts = mutableStateListOf<Product>()
    var isLoading by mutableStateOf(false)

    val db = MyRoomDatabase.getInstance()
    val cartDao = db.cartDao()


    init {
        getProducts()
    }

    fun getProducts() {
        isLoading = true
        viewModelScope.launch {
            firebaseFireStore.collection("products").get()
                .addOnSuccessListener { res ->
                    isLoading = false
                    allProducts.clear()
                    for(doc in res) {
                        allProducts.add(doc.toObject())
                    }
                }
                .addOnFailureListener{
                    isLoading = false
                }
        }
    }

    fun addToCart(product: Product) {

        val cartModel = CartModel(
            productId = product.id!!,
            name = product.name,
            description = product.description,
            quantityList = product.quantity,
            priceList = product.price,
            imageUrl = product.imageUrl,
        )

        CoroutineScope(Dispatchers.IO).launch {
            cartDao.addNewItem(cartModel)
        }
    }
}