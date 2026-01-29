package com.example.askanyone


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.askanyone.models.Question
@Composable
fun MyDoubtsScreen(
    navController: NavHostController,
    appViewModel: AppViewModel
) {
    val list = appViewModel.myQuestions

    LaunchedEffect(Unit) {
        appViewModel.fetchMyQuestions()
    }

    val forestGreen = Color(0xFF064E3B)
    val amber = Color(0xFFF59E0B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(forestGreen)
            .padding(16.dp)
    ) {

        // Top row: Only Back button (removed "My Doubts" text)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                border = BorderStroke(2.dp, amber),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", color = amber)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(list) { q ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { navController.navigate("question_detail/${q.id}") },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        Text(
                            text = "User: ${q.user_id}",
                            color = forestGreen,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = q.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = q.body,
                            maxLines = 2,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

