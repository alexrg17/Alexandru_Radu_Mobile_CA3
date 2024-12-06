package com.example.alexandru_radu_ca3

import retrofit2.Call
import retrofit2.http.GET

interface RoomApiService {

    // Coroutine-based method
    @GET("rooms") // Endpoint appended to the base URL
    suspend fun getRooms(): List<Room>

    // Callback-based method
    @GET("rooms")
    fun getRoomsCall(): Call<List<Room>>
}