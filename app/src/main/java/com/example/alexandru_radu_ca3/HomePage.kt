package com.example.alexandru_radu_ca3

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Data class for Room
data class Room(
    val id: String,
    val temperature: String,
    val humidity: String,
    val resident: String,
    val age: Int,
    val preferredTemp: Int,
    val preferredHumidity: Int
)

@Composable
fun HomePage(navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var rooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val apiClient = ApiClient.roomApiService

    // Fetch data from the API
    LaunchedEffect(Unit) {
        val call = apiClient.getRoomsCall()
        call.enqueue(object : Callback<List<Room>> {
            override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
                if (response.isSuccessful) {
                    rooms = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error: ${response.code()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Room>>, t: Throwable) {
                errorMessage = "Failed to fetch data: ${t.message}"
                isLoading = false
            }
        })
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        val isTablet = maxWidth > 600.dp

        Column {
            // Header
            AnimatedHeader(isTablet = isTablet)

            // Content
            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                errorMessage != null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage ?: "An error occurred",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Red)
                    )
                }
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(if (isTablet) 24.dp else 12.dp)
                ) {
                    itemsIndexed(rooms) { _, room ->
                        RoomCard(room = room, navController = navController, isTablet = isTablet)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedHeader(isTablet: Boolean) {
    var colorToggle by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (colorToggle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(durationMillis = 2000)
    )

    LaunchedEffect(Unit) {
        while (true) {
            colorToggle = !colorToggle
            kotlinx.coroutines.delay(3000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Home Temperature Monitor",
            style = if (isTablet) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
    }
}

@Composable
fun RoomCard(room: Room, navController: NavController, isTablet: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                navController.navigate("details/${room.id}")
            },
        shape = RoundedCornerShape(if (isTablet) 24.dp else 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(if (isTablet) 24.dp else 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (isTablet) 12.dp else 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = getRoomImageResource(room.resident)),
                    contentDescription = "${room.resident} Image",
                    modifier = Modifier
                        .size(if (isTablet) 96.dp else 64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(if (isTablet) 24.dp else 16.dp))

                Column {
                    Text(
                        text = room.resident,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(if (isTablet) 8.dp else 4.dp))
                    Text(
                        text = "Temp: ${room.temperature}°C | Humidity: ${room.humidity}%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                    Spacer(modifier = Modifier.height(if (isTablet) 8.dp else 4.dp))
                    Text(
                        text = "Age: ${room.age} years",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}