package com.example.milkit.presentation.checkout

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.milkit.R
import com.example.milkit.presentation.cart.CartModel
import com.example.milkit.presentation.orders.OrderItemModel
import com.example.milkit.presentation.orders.OrderModel
import com.example.milkit.presentation.profile.User
import com.example.milkit.util.Screen
import com.google.firestore.v1.StructuredQuery.Order
import java.util.Calendar
import java.util.Date
import java.util.UUID


@Composable
fun CheckoutScreen(navController: NavController) {

    val viewModel: CheckoutViewModel = viewModel()
    if (viewModel.isLoading.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = colorResource(id = R.color.main_theme)
            )
        }
    } else {
        val cartList = viewModel.cartItems.observeAsState()

        val context = LocalContext.current


        val a = cartList.value?.map { it.totalCost } ?: emptyList()
        val totalCost = a.sumOf { it?.toInt() ?: 0 }

        var selectedDate:String? = null


        val userData = viewModel.userData.value

        Column(
            Modifier
                .background(color = colorResource(id = R.color.main_theme))
                .fillMaxSize()
        ) {

            if ((cartList.value?.size ?: 0) >= 1 && cartList.value?.any {
                    it.isCheckedOut
                } == true)  {

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(count = cartList.value!!.size) { index ->

                        val product = cartList.value!![index]
                        if(product.isCheckedOut) {
                            Items(product = product)
                        }

                    }
                    if (userData != null) {
                        item {
                            AddressCard(user = userData, onDateSelected = {
                                selectedDate = it
                        })
                        }
                    }
                    item {
                        TotalCostCard(totalCost.toString(), onDoneClick = {
                            if(selectedDate.isNullOrEmpty()) {
                                Toast.makeText(context,"Select Start Date",Toast.LENGTH_LONG).show()
                            } else {
                                val orderItems = mutableListOf<OrderItemModel>()
                                cartList.value?.forEach { ite ->
                                    if(ite.isCheckedOut) {
                                        orderItems.add(
                                            OrderItemModel(
                                                orderId = ite.productId,
                                                type = (ite.name
                                                ?:" ") + ",  ${ite.selectedQuantity}",
                                                typeDesc = "${ite.frequency}, ${
                                                    if (ite.morningOrEvening == "Both") {
                                                        " Morning & Evening"
                                                    } else {
                                                        " " + ite.morningOrEvening
                                                    }
                                                }",imageUrl = ite.imageUrl,
                                                itemPrice = ite.totalCost)
                                        )
                                    }

                                }

                                if(orderItems.isEmpty()) {
                                    Toast.makeText(context,"No items is selected for checkout",Toast.LENGTH_LONG).show()


                                } else {
                                    val orderModel = OrderModel(
                                        orderId = UUID.randomUUID().toString(),
                                        totalPrice = totalCost.toString(),
                                        startDate = selectedDate,
                                        address = userData?.address,
                                        phoneNumber = userData?.phoneNumber,
                                        userId = userData?.userId,
                                        orders = orderItems
                                    )
                                    viewModel.addOrderToDb(orderModel,context)
                                    navController.navigate(Screen.HomeScreen.route){
                                     popUpTo(Screen.CheckoutScreen.route) {
                                         inclusive = true
                                     }
                                    }
                                }

                            }
                        },navController)
                    }
                }
            }
        }
    }
}


@Composable
fun TotalCostCard(cost: String, onDoneClick: ()->Unit,navController: NavController) {


    Card(
        modifier = Modifier.padding(8.dp),
        backgroundColor = colorResource(id = R.color.main_theme)
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total Cash : ", fontSize = 16.sp)
                Text(text = "£ $cost", fontSize = 16.sp)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(onClick = {
                    onDoneClick.invoke()
                }, modifier = Modifier.weight(1f)) {
                    Text(
                        color = Color.White,
                        text = "Done ", fontSize = 16.sp
                    )
                }

                Button(onClick = {

                                 navController.popBackStack()

                }, modifier = Modifier.weight(1f)) {
                    Text(
                        color = Color.White,
                        text = "Cancel ", fontSize = 16.sp
                    )
                }
            }

        }


    }
}

@Composable
fun AddressCard(user: User,onDateSelected:(date:String)->Unit) {

    val context = LocalContext.current

    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()


    val mDate = remember { mutableStateOf("") }

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYe: Int, mMon: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMon + 1}/$mYe"
            onDateSelected.invoke(mDate.value)
        }, mYear, mMonth, mDay
    )


    Card(
        modifier = Modifier.padding(8.dp),
        backgroundColor = colorResource(id = R.color.main_theme)
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = user.name ?: "", fontSize = 20.sp)
                Icon(
                    painter = painterResource(id = R.drawable.profile_icon),
                    tint = colorResource(id = R.color.main_theme),
                    contentDescription = "ProfileIcon"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .weight(2f)
                        .padding(end = 70.dp),
                    text = user.address ?: "", fontSize = 16.sp
                )
                Icon(
                    painter = painterResource(id = R.drawable.location),
                    tint = colorResource(id = R.color.main_theme),
                    contentDescription = "ProfileIcon"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Contact : ", fontSize = 16.sp)
                Text(text = "+91  ${(user.phoneNumber ?: "")}", fontSize = 16.sp)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Choose Start Date :", fontSize = 16.sp
                )

                Row {
                    Text(
                        text = mDate.value + " ", fontSize = 16.sp
                    )
                    Icon(
                        modifier = Modifier.clickable {
                            mDatePickerDialog.show()
                        },
                        painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                        tint = colorResource(id = R.color.main_theme),
                        contentDescription = "ProfileIcon"
                    )

                }


            }
        }

    }
}


@Composable
fun Items(product: CartModel) {
    Card(modifier = Modifier.padding(8.dp), elevation = 8.dp) {
        Column {
            Row {
                Image(
                    painter = rememberAsyncImagePainter(model = product.imageUrl),
                    contentDescription = "Something",
                    modifier = Modifier
                        .padding(8.dp)
                        .height(60.dp)
                        .width(60.dp)
                        .clip(CircleShape)
                )

                Column {
                    Text(
                        text = (product.name
                            ?:" ") + ",  ${product.selectedQuantity}",
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        fontSize = 16.sp
                    )


                    Text(
                        text = "${product.frequency}, ${
                            if (product.morningOrEvening == "Both") {
                                " Morning & Evening"
                            } else {
                                " " + product.morningOrEvening
                            }
                        }",
                    )

                }
            }
            Text(
                text = "Total Price : ${product.totalCost}",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 16.dp)
            )
        }
    }
}
