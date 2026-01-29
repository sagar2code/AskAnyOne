package com.example.askanyone.models

data class Answer(
    val id: Int,
    val body: String,
    val user_id: Int,
    val question_id: Int
)
