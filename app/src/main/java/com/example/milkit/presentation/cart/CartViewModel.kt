package com.example.milkit.presentation.cart

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.room.TypeConverter
import com.example.milkit.data.repo.CartRepository
import com.example.milkit.data.room.CartDao
import com.example.milkit.data.room.MyRoomDatabase
import com.example.milkit.presentation.profile.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel:ViewModel() {


    var db:MyRoomDatabase =  MyRoomDatabase.getInstance()
    var cartDao:CartDao =db.cartDao()
    var cartRepo:CartRepository = CartRepository(cartDao)

    val currentUser = Firebase.auth.currentUser

    val cartItems: LiveData<List<CartModel>> = cartRepo.allCartItems

    var firebaseFirestore = FirebaseFirestore.getInstance()
    var firebaseStorage = FirebaseStorage.getInstance()
    var userData = mutableStateOf<User?>(null)




    init {
        getAllCartItems()
        getProfileDetails()
    }


    private fun getProfileDetails() {
        val query = firebaseFirestore.collection("profile").whereEqualTo("userId", currentUser?.uid ?: "")
        query.get().addOnCompleteListener {
            if(it.isSuccessful && it.result.documents.isNotEmpty()) {
                userData.value =  it.result.documents[0].toObject<User>()
            }
        }
    }

    fun isUserDetailsAvailable() :Boolean
    {
        return !(userData.value?.name.isNullOrEmpty() || userData.value?.address.isNullOrEmpty() ||
                userData.value?.phoneNumber.isNullOrEmpty())
    }
    private fun getAllCartItems() {
        cartRepo.getAllCartsItems()
    }

    fun editCartItem(cartModel: CartModel) {
        CoroutineScope(Dispatchers.IO).launch {
            cartDao.updateCartItem(cartModel)
        }
    }
}

