package com.example.askanyone

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun QuestionDetailScreen(
    navController: NavHostController,
    appViewModel: AppViewModel,
    questionId: Int
) {
    ShowToast(appViewModel.detailError) { appViewModel.detailError = null }
    ShowToast(appViewModel.deleteAnswerError) { appViewModel.deleteAnswerError = null }
    ShowToast(appViewModel.deleteQuestionError) { appViewModel.deleteQuestionError = null }

    val question = appViewModel.selectedQuestion
    val answers = appViewModel.answers

    val forestGreen = Color(0xFF064E3B)
    val amber = Color(0xFFF59E0B)

    LaunchedEffect(questionId) {
        appViewModel.loadQuestionDetail(questionId)
    }

    // ONE single LazyColumn for everything
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(forestGreen)
            .padding(16.dp)
    ) {

        // Back button
        item {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                border = BorderStroke(2.dp, amber),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Back", color = amber)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Question card
        if (question != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

                        Text(
                            text = "User: ${question.username}",
                            color = forestGreen,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = question.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = question.body,
                            color = Color.DarkGray
                        )

                        // Image display
                        if (question.image_url != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            AsyncImage(
                                model = question.image_url,
                                contentDescription = "Question Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(), // ← auto height, no cropping
                                contentScale = ContentScale.FillWidth // ← full image shown
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { navController.navigate("add_answer/$questionId") },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = amber)
                            ) {
                                Text("Add Answer", color = Color.Black)
                            }

                            OutlinedButton(
                                onClick = {
                                    appViewModel.deleteQuestion(questionId) {
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    }
                                },
                                border = BorderStroke(2.dp, amber),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Delete", color = amber)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Answers header ← now inside item{}
        item {
            Text(
                text = "Answers",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Answers list ← directly in the outer LazyColumn, no nesting!
        items(answers) { a ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Text(
                        text = "User: ${a.username}",
                        color = forestGreen,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = a.body,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            appViewModel.deleteAnswer(
                                answerId = a.id,
                                questionId = questionId
                            ) {}
                        },
                        border = BorderStroke(2.dp, amber),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Delete", color = amber)
                    }
                }
            }
        }
    }
}