package com.example.milkit.data.repo

import androidx.lifecycle.MutableLiveData
import com.example.milkit.data.room.CartDao
import com.example.milkit.presentation.cart.CartModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CartRepository(private val cartDao: CartDao) {

    val allCartItems = MutableLiveData<List<CartModel>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    fun getAllCartsItems() {
        coroutineScope.launch(Dispatchers.IO) {
            allCartItems.postValue(cartDao.getAllCartItems())
        }
    }

}