package com.example.milkit.presentation.cart

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "cart")
data class CartModel(
    @PrimaryKey(autoGenerate = false)
    var productId: String,
    var name:String? = null,
    var description:String? = null,
    var quantityList:List<String>? = emptyList(),
    var priceList:List<String>? = emptyList(),
    var imageUrl:String? = null,
    var selectedQuantity:String? = null,
    var frequency:String? = null,
    var totalCost:String? = null,
    var isCheckedOut:Boolean = false,
    var morningOrEvening:String? = null
)



class StringListConverter {
    @TypeConverter
    fun fromString(stringListString: String): List<String> {
        return stringListString.split(",").map { it }
    }

    @TypeConverter
    fun toString(stringList: List<String>): String {
        return stringList.joinToString(separator = ",")
    }
}