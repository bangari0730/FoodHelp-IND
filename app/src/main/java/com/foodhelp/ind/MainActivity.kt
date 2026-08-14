package com.foodhelp.ind

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foodhelp.ind.ui.theme.EmeraldGreen
import com.foodhelp.ind.ui.theme.FoodHelpINDTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodHelpINDTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen { navController.navigate("entry") } }
        composable("entry") { EntryScreen { isLogin -> navController.navigate("auth/$isLogin") } }
        composable("auth/{isLogin}") { backStack ->
            val isLogin = backStack.arguments?.getString("isLogin")?.toBoolean() ?: true
            AuthScreen(isLogin = isLogin) { navController.navigate("role_select") }
        }
        composable("role_select") {
            RoleSelectScreen(
                onDonorSelected = { navController.navigate("donor") },
                onSeekerSelected = { navController.navigate("seeker") }
            )
        }
        composable("donor") { DonorScreen() }
        composable("seeker") { SeekerScreen() }
    }
}

// ------------------- SCREEN 1: WELCOME SCREEN -------------------
@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("FoodHelp IND", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
            Spacer(modifier = Modifier.width(8.dp))
            Text("🇮🇳", fontSize = 28.sp)
        }
        
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.1f))
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("🍲 Community Food Relief", color = EmeraldGreen, fontWeight = FontWeight.SemiBold)
            }
        }

        Text(
            "\"No one has ever become poor by giving.\"",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
        ) {
            Text("Get Started", fontSize = 18.sp)
        }
    }
}

// ------------------- SCREEN 2: ENTRY SCREEN -------------------
@Composable
fun EntryScreen(onChoice: (Boolean) -> Unit) {
    var selectedCategory by remember { mutableStateOf("Individual") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registering as:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            FilterChip(
                selected = selectedCategory == "Individual",
                onClick = { selectedCategory = "Individual" },
                label = { Text("Individual") }
            )
            FilterChip(
                selected = selectedCategory == "Organization",
                onClick = { selectedCategory = "Organization" },
                label = { Text("Organization") }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { onChoice(true) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
        ) { Text("Login") }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { onChoice(false) },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text("Sign Up", color = EmeraldGreen) }
    }
}

// ------------------- SCREEN 3: AUTHENTICATION SCREEN -------------------
@Composable
fun AuthScreen(isLogin: Boolean, onSuccess: () -> Unit) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (isLogin) "Login" else "Sign Up", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        
        if (!isLogin) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Strict Email Validation on Sign Up
                if (!isLogin && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(context, "Invalid Email Format!", Toast.LENGTH_SHORT).show()
                } else {
                    onSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
        ) {
            Text(if (isLogin) "Submit Login" else "Complete Sign Up")
        }
    }
}

// ------------------- SCREEN 4: ROLE SELECTION SCREEN -------------------
@Composable
fun RoleSelectScreen(onDonorSelected: () -> Unit, onSeekerSelected: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("How would you like to proceed?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDonorSelected,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
        ) { Text("I'm a Donor", fontSize = 18.sp) }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSeekerSelected,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
        ) { Text("I'm in Need", fontSize = 18.sp) }
    }
}

// ------------------- SCREEN 5: DONOR SCREEN -------------------
@Composable
fun DonorScreen() {
    var dishName by remember { mutableStateOf("") }
    var portions by remember { mutableStateOf("") }
    var radius by remember { mutableFloatStateOf(5f) }
    val defaultLocation = LatLng(20.5937, 78.9629) // India
    val cameraState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(defaultLocation, 10f) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top 1/4 Google Map View
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.25f)) {
            GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraState) {
                Marker(state = MarkerState(position = defaultLocation), title = "My Location")
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(value = dishName, onValueChange = { dishName = it }, label = { Text("Dish Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = portions, onValueChange = { portions = it }, label = { Text("Portions Available") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            Text("Visibility Radius: ${radius.toInt()} km")
            Slider(value = radius, onValueChange = { radius = it }, valueRange = 1f..50f)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)) {
                Text("Post Donation")
            }
        }
    }
}

// ------------------- SCREEN 6: SEEKER SCREEN -------------------
@Composable
fun SeekerScreen() {
    var radius by remember { mutableFloatStateOf(10f) }
    val defaultLocation = LatLng(20.5937, 78.9629)
    val cameraState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(defaultLocation, 10f) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.35f)) {
            GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraState) {
                Marker(state = MarkerState(position = defaultLocation), title = "User")
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)) {
                    Text("Find My Location")
                }
                Text("Radius: ${radius.toInt()} km", fontSize = 16.sp)
            }
            Slider(value = radius, onValueChange = { radius = it }, valueRange = 1f..50f)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Available Food Nearby:", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            LazyColumn {
                items(3) { index ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Rice & Curry Pack #${index + 1}", fontWeight = FontWeight.Bold)
                            Text("Portions: ${(index + 1) * 5} | Distance: ${(index + 1) * 1.5} km")
                        }
                    }
                }
            }
        }
    }
}
