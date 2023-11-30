package com.example.milkit.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.milkit.presentation.cart.CartModel


@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNewItem(cartModel: CartModel)

    @Query("SELECT * FROM cart WHERE productId = :productId")
    fun findCartItemById(productId: String): CartModel

    @Query("SELECT * FROM cart")
    suspend fun getAllCartItems(): List<CartModel>

    @Update
    suspend fun updateCartItem(cartModel: CartModel)

    @Query("DELETE FROM cart WHERE productId = :id")
    suspend fun deleteCartItem(id: String)
}