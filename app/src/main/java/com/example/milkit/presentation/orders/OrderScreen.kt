package com.example.milkit.presentation.orders

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.milkit.R
import com.example.milkit.presentation.cart.CartModel
import com.example.milkit.util.Screen

@Composable

fun OrderScreen(navController: NavController) {

    val viewModel: OrderViewModel = viewModel()

    if (viewModel.isLoading) {
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
        val orders = viewModel.orders

        if (orders.isNotEmpty()) {

            Column(
                Modifier
                    .background(color = colorResource(id = R.color.main_theme))
                    .fillMaxSize()
                    .padding(8.dp)
            ) {

                LazyColumn (modifier = Modifier.weight(1f)){
                    items(count = orders.size) { index ->
                        val item = orders[index]
                        if (item.orders != null && item.orders!!.isNotEmpty()) {
                            Card(
                                elevation = 8.dp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(colorResource(id = R.color.white))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Items(item.orders!!)

                                    Row {
                                        Text(
                                            text = "Address : ${item.address}",
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(text = "", modifier = Modifier.weight(1f))
                                    }


                                    Row {
                                        Text(
                                            text = "Phone Number : ${item.phoneNumber}",
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(text = "", modifier = Modifier.weight(1f))
                                    }


                                    Row {
                                        Text(
                                            text = "Start Date : ${item.startDate}",
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(text = "", modifier = Modifier.weight(1f))
                                    }

                                    Row {
                                        Text(
                                            text = "Total Price : ${item.totalPrice}",
                                            modifier = Modifier.weight(2f)
                                        )
                                        Text(text = "", modifier = Modifier.weight(1f))
                                    }
                                }

                            }

                        }

                    }

                }

                Button(
                    onClick = {
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(Screen.OrderScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(text = "Back to Home", color = colorResource(id = R.color.white))
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No orders Found"
                )
            }
        }
    }
}

@Composable
fun Items(itemsList: List<OrderItemModel>) {
    itemsList.forEach { it ->
        Item(it)
    }
}


@Composable
@Preview
fun Item(
    product: OrderItemModel = OrderItemModel(
        type = "Cow Milk, 250ML", typeDesc = "One Time, Morning",
        imageUrl = "Null", itemPrice = "Rs. 20"
    )
) {
    Card(modifier = Modifier.padding(8.dp), elevation = 8.dp) {
        Column {
            Row {
                Image(
                    painter = rememberAsyncImagePainter(model = product.imageUrl),
                    contentDescription = "Something",
                    modifier = Modifier
                        .padding(8.dp)
                        .height(50.dp)
                        .width(50.dp)
                )

                Column {
                    Text(
                        text = product.type ?: " ",
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        fontSize = 16.sp
                    )


                    Text(
                        text = "${product.typeDesc}"
                    )

                }
            }
            Text(
                text = "Total Price : £ ${product.itemPrice}",
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 16.dp)
            )
        }
    }
}

