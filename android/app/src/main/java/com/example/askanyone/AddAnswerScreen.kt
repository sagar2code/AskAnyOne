package com.example.askanyone

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun AddAnswerScreen(
    navController: NavHostController,
    appViewModel: AppViewModel,
    questionId: Int
) {
    ShowToast(appViewModel.addAnswerError) { appViewModel.addAnswerError = null }

    var answerText by remember { mutableStateOf("") }

    val question = appViewModel.selectedQuestion

    val forestGreen = Color(0xFF064E3B)
    val amber = Color(0xFFF59E0B)

    // load question reference if not loaded
    LaunchedEffect(questionId) {
        appViewModel.loadQuestionDetail(questionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(forestGreen)
            .padding(16.dp)
    ) {

        OutlinedButton(
            onClick = { navController.popBackStack() },
            border = BorderStroke(2.dp, amber),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back", color = amber)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // show the question for reference
        if (question != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Text(
                        text = "User: ${question.user_id}",
                        color = forestGreen,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = question.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = question.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        //answer input
        OutlinedTextField(
            value = answerText,
            onValueChange = { answerText = it },
            placeholder = { Text("Your Answer") },//shows text when field is empty
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,//text field 4 lines tall
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = amber,//border colour when the field is selected(focused)
                unfocusedBorderColor = Color.LightGray,
                cursorColor = forestGreen,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                appViewModel.addAnswer(answerText, questionId) {
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = amber)
        ) {
            Text(
                "Post Answer",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

