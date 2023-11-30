package com.example.milkit.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.milkit.R
import com.example.milkit.util.Screen
import kotlinx.coroutines.delay


@Composable

fun SplashScreen(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(3000L)
        navController.navigate(Screen.LoginScreen.route){
            popUpTo(Screen.SplashScreen.route) {
                inclusive = true
            }
        }
    }

    Box(modifier = Modifier
        .background(color = colorResource(id = R.color.splash_bg))
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Image(
            modifier = Modifier
                .height(200.dp)
                .width(200.dp),
            painter = painterResource(id = R.drawable.milkit), contentDescription ="Logo" )
    }
}