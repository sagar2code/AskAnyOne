package com.example.askanyone

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@Composable
fun AddQuestionScreen(
    navController: NavHostController,
    appViewModel: AppViewModel
) {
    ShowToast(appViewModel.addQuestionError) { appViewModel.addQuestionError = null }

    val context = LocalContext.current
    val forestGreen = Color(0xFF064E3B)
    val amber = Color(0xFFF59E0B)

    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(forestGreen)
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = amber),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = amber,
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

        // Body
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            placeholder = { Text("Body") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = amber,
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

        // Image preview (shows after picking)
        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Add Image Button
        OutlinedButton(
            onClick = { imagePicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = amber)
        ) {
            Text(
                if (imageUri == null) "Add Image (Optional)" else "Change Image",
                color = amber
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Post Button
        Button(
            onClick = {
                appViewModel.addQuestion(title, body, imageUri, context) {
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
                "Post Question",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}