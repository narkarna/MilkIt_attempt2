package com.example.milkit.util

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object CartScreen: Screen("cart_screen")
    object CheckoutScreen: Screen("checkout_screen")
    object ProfileScreen: Screen("profile_screen")
    object ProductsScreen: Screen("product_screen")
    object SplashScreen:Screen("splash_screen")
    object LoginScreen:Screen("login_screen")
    object OrderScreen:Screen("order_screen")
}