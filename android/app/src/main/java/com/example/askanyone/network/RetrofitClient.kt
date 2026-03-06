package com.example.askanyone.network


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient { //object means one single object for the whole app
   // private const val BASE_URL = "http://10.0.2.2:5001/"
    private const val BASE_URL ="http://192.168.1.42:5001"
    // the PCs localhost is this
    // the PCs localhost is this

    private val loggingInterceptor = HttpLoggingInterceptor().apply { // used for debugging
        level = HttpLoggingInterceptor.Level.BODY                       // prints in logcat
    }

    private val client = OkHttpClient.Builder() //uses OkHttps for the HTTP work
        .addInterceptor(loggingInterceptor)//adds logging
        .build()

    val api: ApiServiceInterface = Retrofit.Builder()//building retrofit
        .baseUrl(BASE_URL)//setting base URL
        .client(client)//using OkHttps client
        .addConverterFactory(GsonConverterFactory.create())//enables gson conversion
        .build()
        .create(ApiServiceInterface::class.java)//Retrofit creates a REAL ob
// ject that implements your interface.

}