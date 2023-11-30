package com.example.milkit.presentation.profile

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class ProfileViewModel : ViewModel() {


    val firebaseAuth = Firebase.auth
    var currentUser= mutableStateOf<FirebaseUser?>(null)
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var firebaseStorage = FirebaseStorage.getInstance()

    val storageRef = firebaseStorage.reference

    val profilePics = storageRef.child("profilePictures")

    var isLoggedIn = mutableStateOf(true)

    var userData = mutableStateOf<User?>(null)

    var isLoading = mutableStateOf(true)

    var makingApiCall = mutableStateOf(false)

    init {
        currentUser.value = firebaseAuth.currentUser
        isLoggedIn.value = currentUser.value != null
        getProfileDetails()
    }


    private val _markerAddressDetail = MutableStateFlow<ResponseState<Address>>(ResponseState.Idle)//ResponseState is a wrapper class
    val markerAddressDetail = _markerAddressDetail.asStateFlow()



    fun getMarkerAddressDetails(lat: Double, long: Double, context: Context) {
        _markerAddressDetail.value = ResponseState.Loading//We will show loading first
        try {
            //Not a good practice to pass context in vm, instead inject this Geocoder
            val geocoder = Geocoder(context, Locale.getDefault())
            if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(//Pass LatLng and get address
                    lat,
                    long,
                    1,//no. of addresses you want
                ) { p0 ->
                    _markerAddressDetail.value = ResponseState.Success(p0[0])
                }
            } else {
                val addresses = geocoder.getFromLocation(//This method is deprecated for >32
                    lat,
                    long,
                    1,
                )
                _markerAddressDetail.value =
                    if(!addresses.isNullOrEmpty()){//The address can be null or empty
                        ResponseState.Success(addresses[0])
                    }else{
                        ResponseState.Error(Exception("Address is null"))
                    }
            }
        } catch (e: Exception) {
            _markerAddressDetail.value = ResponseState.Error(e)
        }
    }


    fun getProfileDetails() {

        val query = firebaseFirestore.collection("profile").whereEqualTo("userId", currentUser.value?.uid ?: "")
        query.get().addOnCompleteListener {

            if(it.isSuccessful && it.result.documents.isNotEmpty()) {
                userData.value =  it.result.documents[0].toObject<User>()
                isLoading.value = false
            } else {
                isLoading.value = false
            }
        }
    }

    fun updateProfile(uri:Uri?,context: Context,name:String,address:String,phoneNumber:String,isPhotoChanged:Boolean) {
        makingApiCall.value = true

        if(isPhotoChanged) {

            profilePics.child(currentUser.value?.uid?:"Doc").putFile(uri!!).addOnCompleteListener {photoTask ->
                if(photoTask.isSuccessful) {
                    profilePics.child(currentUser.value?.uid?:"Doc").downloadUrl.addOnSuccessListener { downloadUri ->
                        val user  = User(currentUser.value?.uid?:"doc",name,downloadUri.toString(),address,phoneNumber)
                        firebaseFirestore.collection("profile").document(currentUser.value?.uid?:"doc").set(user).addOnCompleteListener { profileTask ->
                            if(profileTask.isSuccessful) {
                                makingApiCall.value = false
                                Toast.makeText(context,"Profile Updated successfully",Toast.LENGTH_LONG).show()
                            } else {
                                makingApiCall.value = false
                                Toast.makeText(context,"Profile Update Failed" + profileTask.exception?.message,Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    makingApiCall.value = false
                    Toast.makeText(context,"Profile Update Failed" + photoTask.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
        } else {
            val user  = User(currentUser.value?.uid?:"doc",name,uri.toString(),address,phoneNumber)
            firebaseFirestore.collection("profile").document(currentUser.value?.uid?:"doc").set(user).addOnCompleteListener { profileTask ->
                if(profileTask.isSuccessful) {
                    makingApiCall.value = false
                    Toast.makeText(context,"Profile Updated successfully",Toast.LENGTH_LONG).show()
                } else {
                    makingApiCall.value = false
                    Toast.makeText(context,"Profile Update Failed" + profileTask.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    fun logout() {
        firebaseAuth.signOut()
    }

}

sealed class ResponseState<out T> {
    object Idle : ResponseState<Nothing>()
    object Loading : ResponseState<Nothing>()
    data class Error(val error: Throwable) : ResponseState<Nothing>()
    data class Success<R>(val data: R) : ResponseState<R>()
}