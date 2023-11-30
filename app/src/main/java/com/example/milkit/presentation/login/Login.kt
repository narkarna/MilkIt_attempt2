package com.example.milkit.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.milkit.R
import com.example.milkit.presentation.profile.ProfileViewModel
import com.example.milkit.util.Screen

@Composable

fun LoginScreen(navController:NavController) {

    val viewModel: ProfileViewModel = viewModel()

    val isLoginScreen = remember {
        mutableStateOf(true)
    }

    if(viewModel.isLoggedIn.value) {
        navController.navigate(Screen.HomeScreen.route)
    } else {
        Login(isLoginScreen.value,navController) { isLoginPage ->
            isLoginScreen.value = isLoginPage
        }
    }
}



@Composable
fun Login(isLoginScreen:Boolean = true,navController: NavController,onFlowChange : (isLoginPage:Boolean)->Unit) {

    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()

    var passwordVisibility: Boolean by remember { mutableStateOf(false) }



    var emailId by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    if(viewModel.loginSuccess.value == true) {

        navController.navigate(Screen.HomeScreen.route) {
            popUpTo(Screen.LoginScreen.route) {
                inclusive = true
            }
        }
    }

    var showPassword by remember {
        mutableStateOf(false)
    }

    var isEmailError by remember {
        mutableStateOf(false)
    }

    var isPasswordError by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier
        .background(color = colorResource(id = R.color.splash_bg))
        .fillMaxSize(),
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Image(
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp),
                painter = painterResource(id = R.drawable.milkit), contentDescription ="Logo" )

            Column {




                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = emailId,
                    onValueChange = { newText ->
                        isEmailError = false
                        emailId = newText
                    },
                    singleLine =  true,
                    label = { Text(text = "Email ID",color = Color.White) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.AccountBox,
                            contentDescription = "Lock Icon",
                            tint = Color.White
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.white),
                        unfocusedBorderColor = colorResource(id = R.color.white),
                        textColor = colorResource(id = R.color.white),
                        errorBorderColor = Color.Red,
                        cursorColor = Color.White

                    ),
                )


                if(isEmailError) {
                    Text(text = "Enter Valid Email id", color = Color.Red)
                }
            }


            Column(modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()) {


                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = { newText ->
                        isPasswordError = false
                        password = newText
                    },
                    singleLine =  true,
                    label = { Text(text = "Password", color = Color.White) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Lock Icon",
                            tint = Color.White
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.white),
                        unfocusedBorderColor = colorResource(id = R.color.white),
                        textColor = colorResource(id = R.color.white),
                        errorBorderColor = Color.Red,
                        cursorColor = Color.White

                    ),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (showPassword) "Show Password" else "Hide Password",
                                        tint = Color.White
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
                )


                if(isPasswordError) {
                    Text(text = "Password size should be greater than 5", color = Color.Red)
                }
            }


            OutlinedButton(onClick = {


                if(isValidEmail(emailId) && password.length >= 6) {
                    if(isLoginScreen) {
                        viewModel.login(emailId,password,context)
                    } else {
                        viewModel.register(emailId,password,context)
                    }
                } else if(!isValidEmail(emailId)) {
                    isEmailError = true
                } else {
                    isPasswordError = true
                }


            },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(16.dp),


                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white),),

                ) {
                Text(text = if(isLoginScreen) "Login" else "Register",
                    color = colorResource(id = R.color.main_theme)
                )
            }

            Row (modifier = Modifier.padding(top = 16.dp)){
                Text(text = if(isLoginScreen) "Doesn't have account?" else "Already have account?",
                    color = colorResource(id = R.color.white)
                )

                Text(
                    modifier = Modifier
                        .clickable {
                            emailId = ""
                            password = ""
                            onFlowChange.invoke(!isLoginScreen)
                        },
                    text = if(isLoginScreen) "  Sign Up" else "  Login",
                    color = colorResource(id = R.color.white)
                )
            }




        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    return email.matches(emailRegex)
}