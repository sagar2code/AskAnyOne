package com.example.askanyone
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.askanyone.models.Question
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun HomeScreen(
    navController: NavHostController,
    appViewModel: AppViewModel
) {
    LaunchedEffect(Unit) {
        appViewModel.fetchQuestions()
    }

    ShowToast(appViewModel.getQuestionError) { appViewModel.getQuestionError = null }
    ShowToast(appViewModel.addQuestionError) { appViewModel.addQuestionError = null }
    ShowToast(appViewModel.deleteQuestionError) { appViewModel.deleteQuestionError = null }

    val questions = appViewModel.questions
    var showMenu by remember { mutableStateOf(false) }

    val forestGreen = Color(0xFF064E3B)
    val amber = Color(0xFFF59E0B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(forestGreen)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // Top bar row (Only Others button now)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showMenu = true },
                    colors = ButtonDefaults.buttonColors(containerColor = amber),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Others", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(questions) { q ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),//spacing between the cards
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        onClick = {
                            navController.navigate("question_detail/${q.id}")
                        }
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {

                            Text(
                                text = "User: ${q.user_id}",
                                color = forestGreen,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Title + Delete in same row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = q.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = { appViewModel.deleteQuestion(q.id) { } },
                                    border = BorderStroke(2.dp, amber),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Delete", color = amber, fontSize = 13.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(1.dp))

                            Text(
                                text = q.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray,
                                maxLines = 2
                            )
                        }
                    }

                }
            }
        }

        AnimatedVisibility(
            visible = showMenu,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { showMenu = false }
            )
        }

        AnimatedVisibility(
            visible = showMenu,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }
            ) + fadeIn(),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }
            ) + fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(260.dp),
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                colors = CardDefaults.cardColors(containerColor = forestGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Others",
                            style = MaterialTheme.typography.titleLarge,
                            color = amber
                        )

                        TextButton(onClick = { showMenu = false }) {
                            Text("Close ✕", color = amber)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(15.dp))

                    Button(
                        onClick = {
                            showMenu = false
                            navController.navigate("add_question")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = amber)
                    ) {
                        Text("+ Add Question", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            showMenu = false
                            navController.navigate("my_doubts")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = amber)
                    ) {
                        Text("My Doubts", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            showMenu = false
                            appViewModel.logout {
                                navController.navigate("start") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = amber)
                    ) {
                        Text("Logout", color = Color.Black)
                    }
                }
            }
        }
    }
}



