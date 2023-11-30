package com.example.milkit.presentation.products

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.milkit.R
import com.example.milkit.presentation.home.Product
import com.example.milkit.util.Screen

@Composable
fun ProductsScreen(navController: NavController) {

    val viewModel: ProductsViewModel = viewModel()

    val allProducts: List<Product> = remember {
        viewModel.allProducts
    }

    val backHandlingEnabled by remember { mutableStateOf(true) }

    BackHandler(backHandlingEnabled) {
        navController.popBackStack()
    }

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

        Column(modifier = Modifier.fillMaxWidth()) {


            LazyColumn(modifier = Modifier.weight(1f)) {
                items(count = allProducts.size) { item ->
                    Item(product = allProducts[item], onAddToCart = {
                        viewModel.addToCart(allProducts[item])
                    })
                }
            }

            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.white)),
                onClick = {
                    navController.navigate(Screen.CartScreen.route)
                }) {
                Text( text = "Go to Cart", color = colorResource(id = R.color.main_theme))
            }


        }
    }
}

@Composable

fun Item(
    product: Product,
    onAddToCart: () -> Unit

) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            Modifier
                .background(color = colorResource(id = R.color.main_theme))
                .padding(8.dp)
        ) {
            Row {
                Image(
                    painter = rememberAsyncImagePainter(model = product.imageUrl),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(100.dp)
                        .width(100.dp)
                )

                Column(Modifier.padding(8.dp)) {
                    Text(
                        text = product.name ?: "",
                        color = colorResource(id = R.color.white),
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        fontSize = 16.sp
                    )

                    Text(
                        text = product.description ?: "",
                        color = colorResource(id = R.color.white),
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        fontSize = 12.sp
                    )
                }
            }
            Divider(thickness = 1.dp, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))


            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.clickable {
                    expanded = true
                }) {
                    Text(
                        text = "Quantity: ${product.quantity[selectedIndex]}",
                        color = colorResource(id = R.color.white)
                    )
                    Icon(
                        painter = if (expanded) painterResource(id = R.drawable.dropdown_up) else painterResource(
                            id = R.drawable.dropdown
                        ),
                        contentDescription = "Dropdown",
                        tint = colorResource(id = R.color.white)
                    )
                }

                DropdownMenu(expanded = expanded,
                    onDismissRequest = { expanded = false }) {
                    // Create menu items
                    for (index in 0 until product.quantity.size) {
                        DropdownMenuItem(
                            onClick = {
                                selectedIndex = index
                                expanded = false
                            }
                        ) {
                            Text(product.quantity[index])
                        }
                    }

                }
                Text(
                    text = "Price : £ ${product.price[selectedIndex]}",
                    color = colorResource(id = R.color.white)
                )
            }


            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp),
                onClick = {
                    onAddToCart.invoke()
                    Toast.makeText(context,"Item Added to Cart",Toast.LENGTH_LONG).show()
                }) {
                Text(text = "Add to Cart")
            }


        }
    }
}
