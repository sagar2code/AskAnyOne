package com.example.askanyone


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.AppLaunchChecker
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.askanyone.auth.Startscreen
import com.example.askanyone.auth.LoginScreen
import com.example.askanyone.auth.RegisterScreen
@Composable
fun AppNavigation(
    navController: NavHostController,
    appViewModel: AppViewModel
) {

    val token = appViewModel.token

    LaunchedEffect(token) {
        if (token != null) {
            navController.navigate("home") {
                popUpTo("start") { inclusive = true }
            }
        } else {
            navController.navigate("start") {
                popUpTo("home") { inclusive = true }
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = "start"
    ) {

        composable("start") {
            Startscreen(navController, appViewModel)
        }

        composable("login") {
            LoginScreen(navController, appViewModel)
        }

        composable("register") {
            RegisterScreen(navController, appViewModel)
        }

        composable("home") {
            HomeScreen(navController, appViewModel)
        }

        composable("add_question"){
            AddQuestionScreen(navController, appViewModel)
        }

        composable("question_detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")!!.toInt()
                QuestionDetailScreen(navController, appViewModel, id)
        }

        composable("add_answer/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")!!.toInt()

                AddAnswerScreen(navController, appViewModel, id)

        }

        composable("my_doubts") {
            MyDoubtsScreen(navController, appViewModel)
        }

    }
}
