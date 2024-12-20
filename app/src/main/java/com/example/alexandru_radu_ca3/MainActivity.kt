package com.example.alexandru_radu_ca3

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alexandru_radu_ca3.ui.theme.Alexandru_Radu_CA3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Alexandru_Radu_CA3Theme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(navController)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        // Login Page
        composable("login") { LoginPage(navController) }

        // Home Page
        composable("home") { HomePage(navController) }

        // Room Details Page
        composable("details/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            if (roomId != null) {
                RoomDetailsPage(navController = navController, roomId = roomId)
            } else {
                // Handle null roomId gracefully, e.g., show an error message or navigate back
                Text("Error: Room ID not provided!")
            }
        }
    }
}

@Composable
fun LoginPage(navController: NavHostController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Home Temperature Monitor",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        TextField(
            value = email,
            onValueChange = {
                email = it
                Log.d("LoginPage", "Email updated: $email")
            },
            label = { Text("Email address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = {
                password = it
                Log.d("LoginPage", "Password updated")
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Log.d("LoginPage", "Login attempt with Email: $email")
                if (email == "admin@example.com" && password == "admin123") {
                    Log.d("LoginPage", "Login successful for Email: $email")
                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    Log.d("LoginPage", "Invalid login attempt for Email: $email")
                    Toast.makeText(context, "Invalid Credentials!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                Log.d("LoginPage", "Signup clicked")
            }) {
                Text("Don't have an account?")
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = {
                Log.d("LoginPage", "Admin login clicked")
            }) {
                Text("Admin Login")
            }
        }
    }
}

@Composable
fun RoomDetailsPage(roomId: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Room Details for Room ID: $roomId",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    Alexandru_Radu_CA3Theme {
        LoginPage(rememberNavController())
    }
}