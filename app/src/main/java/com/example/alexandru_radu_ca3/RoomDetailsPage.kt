package com.example.alexandru_radu_ca3

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RoomDetailsPage(navController: NavHostController, roomId: String) {
    var room by remember { mutableStateOf<Room?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val apiClient = ApiClient.roomApiService

    // Fetch room details based on the ID
    LaunchedEffect(roomId) {
        val call = apiClient.getRoomsCall()
        call.enqueue(object : Callback<List<Room>> {
            override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
                if (response.isSuccessful) {
                    room = response.body()?.find { it.id == roomId }
                    isLoading = false
                } else {
                    errorMessage = "Error: ${response.code()}"
                    Log.e("RoomDetailsPage", "Failed with code: ${response.code()}")
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<List<Room>>, t: Throwable) {
                errorMessage = "Failed to fetch data: ${t.message}"
                Log.e("RoomDetailsPage", "Error: ${t.message}")
                isLoading = false
            }
        })
    }

    // UI Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "An error occurred",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            room != null -> RoomDetailsContent(navController, room!!)
        }
    }
}

@Composable
fun RoomDetailsContent(navController: NavController, room: Room) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Room Details",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Room Image
        Image(
            painter = painterResource(id = getRoomImageResource(room.resident)),
            contentDescription = "${room.resident} Image",
            modifier = Modifier
                .size(128.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Room Information
        Text(
            text = room.resident,
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), thickness = 1.dp)

        // Details Section
        Spacer(modifier = Modifier.height(16.dp))
        InfoRow(label = "Temperature", value = "${room.temperature}°C")
        InfoRow(label = "Humidity", value = "${room.humidity}%")
        InfoRow(label = "Age", value = "${room.age} years")
        InfoRow(label = "Preferred Temperature", value = "${room.preferredTemp}°C")
        InfoRow(label = "Preferred Humidity", value = "${room.preferredHumidity}%")
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )
    }
}

fun getRoomImageResource(resident: String): Int {
    return when (resident) {
        "Eva Paucek" -> R.drawable.jerry_bedroom
        "Anthony Bergstrom" -> R.drawable.alice_bedroom
        "Alton Moen" -> R.drawable.paul_bedroom
        "Mrs. Gerard Herman" -> R.drawable.emma_bedroom
        else -> R.drawable.placeholder_image
    }
}