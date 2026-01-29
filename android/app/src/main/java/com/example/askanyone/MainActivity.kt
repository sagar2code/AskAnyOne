package com.example.askanyone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController

import com.example.askanyone.ui.theme.AskAnyOneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val naviController = rememberNavController()

            val appViewModel: AppViewModel = viewModel() // = viewModel means assigning an object there


            LaunchedEffect(Unit) {// run this code when the composable is on the screen for the first time
                appViewModel.loadToken()
            }

            MaterialTheme {
                AppNavigation(naviController, appViewModel)
            }
        }
    }
}

