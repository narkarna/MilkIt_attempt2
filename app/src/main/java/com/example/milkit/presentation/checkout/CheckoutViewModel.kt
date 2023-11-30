package com.example.milkit.presentation.checkout

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.milkit.data.repo.CartRepository
import com.example.milkit.data.room.CartDao
import com.example.milkit.data.room.MyRoomDatabase
import com.example.milkit.presentation.cart.CartModel
import com.example.milkit.presentation.orders.OrderModel
import com.example.milkit.presentation.profile.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckoutViewModel:ViewModel() {


    val currentUser = Firebase.auth.currentUser
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var firebaseStorage = FirebaseStorage.getInstance()
    var userData = mutableStateOf<User?>(null)

    var db: MyRoomDatabase =  MyRoomDatabase.getInstance()
    var cartDao: CartDao =db.cartDao()
    var cartRepo: CartRepository = CartRepository(cartDao)

    var isLoading = mutableStateOf(true)

    val cartItems: LiveData<List<CartModel>> = cartRepo.allCartItems

    private fun getProfileDetails() {
        val query = firebaseFirestore.collection("profile").whereEqualTo("userId", currentUser?.uid ?: "")
        query.get().addOnCompleteListener {
            if(it.isSuccessful && it.result.documents.isNotEmpty()) {
                userData.value =  it.result.documents[0].toObject<User>()
                isLoading.value = false
            }
        }
    }

    fun addOrderToDb(orderModel: OrderModel,context:Context) {
        firebaseFirestore.collection("orders").add(orderModel).addOnCompleteListener {
            if(it.isSuccessful ) {
                orderModel.orders?.forEach { order ->
                    deleteFromCart(order.orderId!!)
                }
                Toast.makeText(context,"Order Placed Successfully",Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(context,"Failed to place order, Try Again Later",Toast.LENGTH_LONG).show()
            }
        }
    }

    init {
        getAllCartItems()
        getProfileDetails()
    }


    private fun getAllCartItems() {
        cartRepo.getAllCartsItems()
    }

    fun deleteFromCart(orderId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            cartDao.deleteCartItem(orderId)
        }
    }
}