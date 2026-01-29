package com.example.askanyone.network

import com.example.askanyone.models.Answer
import com.example.askanyone.models.CreateAnswerRequest
import com.example.askanyone.models.CreateQuestionRequest
import com.example.askanyone.models.LoginRequest
import com.example.askanyone.models.LoginResponse
import com.example.askanyone.models.Question
import com.example.askanyone.models.RegisterRequest
import com.example.askanyone.models.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiServiceInterface {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest):  Response<LoginResponse>  //Converts Kotlin to JSON using GSON
            //only LoginResponse gives only the body , Response<LoginResponse> gives full https response with body as LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("questions")
    suspend fun getAllQuestions() : Response<List<Question>>

    @POST("questions")
    suspend fun addQuestion(
        @Body request: CreateQuestionRequest,
        @Header("Authorization") token: String
    ): Response<Question>

    @GET("questions/{id}")
    suspend fun getQuestionById(@Path("id") id: Int): Response<Question>

    @GET("questions/{id}/answers")
    suspend fun getAnswersByQuestion(@Path("id") id: Int): Response<List<Answer>>

    @POST("questions/{id}/answers")
    suspend fun addAnswer(@Path("id") id : Int,
                          @Body request: CreateAnswerRequest,
                          @Header("Authorization") token: String) : Response<Answer>

    @DELETE("questions/{id}")
    suspend fun deleteQuestion(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @DELETE("questions/{id}/answers")
    suspend fun deleteAnswer(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("questions/my")
    suspend fun getMyQuestions(
        @Header("Authorization") token: String
    ): Response<List<Question>>


}