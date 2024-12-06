package com.example.alexandru_radu_ca3

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://6752c7c4f3754fcea7b99109.mockapi.io/api/v1/" // Base URL must end with '/'

    val roomApiService: RoomApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Base URL only, no endpoint here
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RoomApiService::class.java)
    }
}