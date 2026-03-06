package com.example.askanyone.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.askanyone.AppViewModel
import com.example.askanyone.ShowToast
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    navController: NavHostController,
    appViewModel: AppViewModel
) {
    ShowToast(appViewModel.loginError) { appViewModel.loginError = null }

    val forestGreen = Color(0xFF064E3B)
    val amber = Color(0xFFF59E0B)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("524926722649-6l7nioc1mpnps49uj9iovaroetg0lp66.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult( //launcher to start another activity and receive its result
        /*
        A contract class that defines:
        What type of activity is started ,What type of result is returned , here it means sending intents*/
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken != null) {
                    appViewModel.googleLogin(idToken)
                }

            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }
    // Dimmed background + popup box
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(forestGreen) // dim effect
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(0.92f),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Login",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = forestGreen
                )

                Spacer(modifier = Modifier.height(20.dp))



                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    maxLines = 1,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = amber,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = forestGreen,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )


                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {Text("Password")},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),

                    visualTransformation = PasswordVisualTransformation(),
                    shape=RoundedCornerShape(12.dp) ,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = amber,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = forestGreen,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        appViewModel.login(email, password, onSuccess = {
                            navController.navigate("home") {
                                popUpTo("start") { inclusive = true }
                            }
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = amber)
                ) {
                    Text(
                        "Login",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }


                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {

                        googleSignInClient.signOut().addOnCompleteListener {
                            launcher.launch(googleSignInClient.signInIntent)
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue with Google")
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = { navController.navigate("register") }) {
                    Text(
                        "Already have an account? Register",
                        color = forestGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}