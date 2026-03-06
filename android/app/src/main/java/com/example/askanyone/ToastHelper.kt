package com.example.askanyone


import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun ShowToast(message: String? ,onShown:()->Unit ) {
    val context = LocalContext.current

     LaunchedEffect(message) {
       if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            onShown()
      }
    }
}
