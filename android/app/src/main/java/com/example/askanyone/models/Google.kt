package com.example.askanyone.models

data class GoogleRequest(val idToken: String)

data class GoogleResponse(
    val token: String?,
    val needsUsername: Boolean?
)

data class CompleteGoogleRequest(
    val idToken: String,
    val username: String
)