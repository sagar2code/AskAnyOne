package com.example.askanyone.network

import com.example.askanyone.models.Answer
import com.example.askanyone.models.CompleteGoogleRequest
import com.example.askanyone.models.CreateAnswerRequest
import com.example.askanyone.models.CreateQuestionRequest
import com.example.askanyone.models.GoogleRequest
import com.example.askanyone.models.GoogleResponse
import com.example.askanyone.models.LoginRequest
import com.example.askanyone.models.LoginResponse
import com.example.askanyone.models.Question
import com.example.askanyone.models.RegisterRequest
import com.example.askanyone.models.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiServiceInterface {

    // @Body means convert it into json and then send it via json body
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest):  Response<LoginResponse>  //Converts Kotlin to JSON using GSON
            //only LoginResponse gives only the body , Response<LoginResponse> gives full https response with body as LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("questions")
    suspend fun getAllQuestions() : Response<List<Question>>

    /*@POST("questions")
    suspend fun addQuestion(
        @Body request: CreateQuestionRequest,
        @Header("Authorization") token: String
    ): Response<Question>*/

    @Multipart
    @POST("questions")
    suspend fun addQuestion(
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part image: MultipartBody.Part?,
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

    @POST("auth/google")
    suspend fun googleLogin(
        @Body request: GoogleRequest
    ): Response<GoogleResponse>

    @POST("auth/google/complete")
    suspend fun completeGoogleRegistration(
        @Body request: CompleteGoogleRequest
    ): Response<LoginResponse>


}