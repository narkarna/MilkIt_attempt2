package com.example.milkit.presentation.orders

data class OrderModel(
    var orderId:String? = null,
    var totalPrice :String? = null,
    var startDate:String? = null,
    var address:String? = null,
    var phoneNumber:String?  = null,
    var orders:List<OrderItemModel>? = null,
    var userId:String? = null

)

data class OrderItemModel(
    var orderId: String? = null,
    var type:String? = null,
    var typeDesc:String? = null,
    var imageUrl:String? = null,
    var itemPrice:String? = null
)
