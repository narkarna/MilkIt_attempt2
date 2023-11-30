package com.example.milkit.presentation.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel:ViewModel() {

    val firebaseAuth = Firebase.auth



    val loginSuccess = mutableStateOf<Boolean?>(null)

    fun login(email:String,password:String,context:Context) {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful) {
                loginSuccess.value = true
            } else {
                loginSuccess.value = false
                Toast.makeText(context,it.exception?.message?: "Error occured",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun register(email:String,password:String,context:Context) {
        firebaseAuth.createUserWithEmailAndPassword (email,password).addOnCompleteListener {
            if(it.isSuccessful) {
                loginSuccess.value = true
            } else {
                loginSuccess.value = false
                Toast.makeText(context,it.exception?.message?: "Error occured",Toast.LENGTH_LONG).show()
            }
        }
    }



}