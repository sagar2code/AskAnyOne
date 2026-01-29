package com.example.askanyone.models

data class Question(
    val id : Int,
    val title: String,
    val body: String,
    val image_url: String? = null,
    val user_id: Int
)
