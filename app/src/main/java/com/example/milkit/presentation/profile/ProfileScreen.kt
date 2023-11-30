package com.example.milkit.presentation.profile

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.milkit.R
import com.example.milkit.util.Screen
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.ktx.model.markerOptions
import java.util.Locale


@Composable
fun ProfileScreen(navController: NavController) {


    val viewModel: ProfileViewModel = viewModel()
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

        Column {
            val userData = viewModel.userData.value
            ProfileView(
                userData?.name,
                userData?.phoneNumber,
                userData?.address,
                userData?.profileUrl,
                navController
            )


        }


    }



    if (viewModel.makingApiCall.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = colorResource(id = R.color.main_theme)
            )
        }
    }


}

@Composable
fun ProfileView(
    userName: String?,
    phone: String?,
    add: String?,
    uri: String?,
    navController: NavController
) {


    var shouldShowMap by remember {
        mutableStateOf(false)
    }

    var selectedLatLng by rememberSaveable {
        mutableStateOf(LatLng(0.0, 0.0))
    }

    var convertedAddress by remember {
        mutableStateOf<String?>(add?:"")
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLatLng, 10f)
    }

    var isPhotoChanges = remember {
        mutableStateOf(false)
    }

    val viewMode: ProfileViewModel = viewModel()
    var photoUri: Uri? by remember {
        mutableStateOf(
            if (uri != null) Uri.parse(uri) else null
        )
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uriData ->
            photoUri = uriData
            isPhotoChanges.value = true
        }

    var profileName: String by remember {
        mutableStateOf(userName ?: "")
    }

    var address by remember {
        mutableStateOf(add ?: "")
    }

    var phoneNumber by remember {
        mutableStateOf(phone ?: "")
    }


    val context = LocalContext.current


    var isNameError by remember {
        mutableStateOf(false)
    }
    var isAddressError by remember {
        mutableStateOf(false)
    }

    var isPhoneNumberError by remember {
        mutableStateOf(false)
    }

    val scrollState = rememberScrollState()

    val locationPermissionsAlreadyGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val permissionsGranted = permissions.values.reduce { acc, isPermissionGranted ->
                acc && isPermissionGranted
            }

            if(permissionsGranted) {
                shouldShowMap = true
            }

            if (!permissionsGranted) {
                //Logic when the permissions were not granted by the user
            }
        })

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    LaunchedEffect(key1 = Unit, block = {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                selectedLatLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
            }
    })




    LaunchedEffect(selectedLatLng) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(selectedLatLng, 10f)
    }


    if (shouldShowMap) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = {
                selectedLatLng = it

                try {
                    val geo = Geocoder(
                        context.applicationContext,
                        Locale.getDefault()
                    )
                    val addresses: List<Address>? =
                        geo.getFromLocation(selectedLatLng.latitude, selectedLatLng.longitude, 1)
                    convertedAddress = if (addresses!!.isEmpty()) {
                        "Waiting for Location"
                    } else {
                        (addresses[0].featureName?: "") + ", " + (addresses[0].locality?: "") + ", " + (addresses[0].adminArea?:"") + ", " + addresses[0].countryName
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                shouldShowMap = false
            },
            onMapLoaded = {

            } ,

            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)

        ) {
            Marker(
                state = MarkerState(
                    position = selectedLatLng
                )
            )
        }

    } else {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.splash_bg))
                .padding(16.dp),

            ) {

            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Image(
                        modifier = Modifier
                            .height(150.dp)
                            .width(150.dp)
                            .background(colorResource(id = R.color.splash_bg), CircleShape)
                            .clip(CircleShape)
                            .clickable {
                                launcher.launch(
                                    PickVisualMediaRequest(
                                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                        painter = if (photoUri != null) rememberAsyncImagePainter(photoUri) else painterResource(
                            id = R.drawable.profile_icon
                        ),
                        contentDescription = "Profile Image"
                    )

                    Column(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    ) {

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = profileName ?: "",
                            onValueChange = {
                                isNameError = false
                                profileName = it
                            },
                            isError = isNameError,
                            singleLine = true,
                            label = {
                                Text(
                                    "Display Name",
                                    color = colorResource(id = R.color.white)
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

                        if (isNameError) {
                            Text(text = "Name cannot be empty", color = Color.Red)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    ) {


                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = phoneNumber,
                            onValueChange = {
                                isPhoneNumberError = false
                                phoneNumber = it
                            },
                            isError = isPhoneNumberError,
                            singleLine = true,
                            label = {
                                Text(
                                    "Phone Number",
                                    color = colorResource(id = R.color.white)
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colorResource(id = R.color.white),
                                unfocusedBorderColor = colorResource(id = R.color.white),
                                textColor = colorResource(id = R.color.white),
                                errorBorderColor = Color.Red,
                                cursorColor = Color.White
                            ),
                        )

                        if (isPhoneNumberError) {
                            Text(text = "Enter Valid Phone Number", color = Color.Red)
                        }
                    }



                    Column(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    ) {


                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clickable {

                                },
                            readOnly = true,
                            value = convertedAddress ?: "",
                            onValueChange = {
//                            isAddressError = false
//                            address = it
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.location),
                                    contentDescription = "Location",

                                    Modifier.clickable {
                                        if(locationPermissionsAlreadyGranted) {
                                            shouldShowMap  = true
                                        } else {
                                            locationPermissionLauncher.launch(locationPermissions)
                                        }

                                    },
                                    tint = colorResource(id = R.color.white)
                                )
                            },
                            isError = isAddressError,
                            singleLine = true,
                            label = {
                                Text(
                                    "Select Address from Map",
                                    color = colorResource(id = R.color.white)
                                )
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colorResource(id = R.color.white),
                                unfocusedBorderColor = colorResource(id = R.color.white),
                                textColor = colorResource(id = R.color.white),
                                errorBorderColor = Color.Red
                            ),
                        )

                        if (isAddressError) {
                            Text(text = "Enter Address", color = Color.Red)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            if (profileName.isNotEmpty() && phoneNumber.isNotEmpty() && convertedAddress?.isNotEmpty()==true) {
                                viewMode.updateProfile(
                                    photoUri,
                                    context,
                                    profileName,
                                    convertedAddress!!,
                                    phoneNumber,
                                    isPhotoChanges.value
                                )
                            } else {
                                Toast.makeText(context, "Enter All Details", Toast.LENGTH_LONG)
                                    .show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),

                        ) {
                        Text(
                            text = "Save",
                            color = colorResource(id = R.color.main_theme)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = {
                            if (userName.isNullOrEmpty() || add.isNullOrEmpty() || phone.isNullOrEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Complete Profile to See Your orders",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                navController.navigate(Screen.OrderScreen.route)
                            }

                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),

                        ) {
                        Text(
                            text = "Orders",
                            color = colorResource(id = R.color.main_theme)
                        )
                    }



                    OutlinedButton(
                        onClick = {
                            viewMode.logout()
                            navController.navigate(Screen.LoginScreen.route) {
                                popUpTo(Screen.ProfileScreen.route) {
                                    inclusive = true
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    ) {
                        Text(
                            text = "Logout",
                            color = colorResource(id = R.color.white)
                        )
                    }
                }


            }
        }
    }
}



