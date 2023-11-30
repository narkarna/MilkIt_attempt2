package com.example.milkit.presentation.orders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.milkit.presentation.profile.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class OrderViewModel:ViewModel() {

    val currentUser = Firebase.auth.currentUser

    var userData = mutableStateOf<User?>(null)

    val firebaseFireStore : FirebaseFirestore = FirebaseFirestore.getInstance()

    val orders = mutableStateListOf<OrderModel>()

    var isLoading by mutableStateOf(false)

    init {
        getProfileDetails()
    }


    private fun getProfileDetails() {
        isLoading = true
        val query = firebaseFireStore.collection("profile").whereEqualTo("userId", currentUser?.uid ?: "")
        query.get().addOnCompleteListener {

            if(it.isSuccessful && it.result.documents.isNotEmpty()) {
                userData.value =  it.result.documents[0].toObject<User>()
                getOrders()
            }
        }
    }


    private fun getOrders() {
        viewModelScope.launch {
            firebaseFireStore.collection("orders")
                .whereEqualTo("userId", userData.value?.userId)
                .get()
                .addOnSuccessListener { res ->
                    isLoading = false
                    orders.clear()
                    for(doc in res) {
                        orders.add(doc.toObject())
                    }
                }
                .addOnFailureListener{
                    isLoading = false
                }
        }
    }
}