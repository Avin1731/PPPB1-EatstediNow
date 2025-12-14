package com.example.eatstedinow.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = OrangePrimary,
                modifier = Modifier.size(24.dp).clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Register", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Buat akun dan pilih menu favoritmu!", fontSize = 14.sp, color = GrayText, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))

            AuthTextField(value = name, onValueChange = { name = it }, label = "Nama", hint = "John Doe")
            AuthTextField(value = email, onValueChange = { email = it }, label = "Email", hint = "johndoe@gmail.com", keyboardType = KeyboardType.Email)
            AuthTextField(value = password, onValueChange = { password = it }, label = "Kata Sandi", hint = "min. 8 karakter", isPassword = true)

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(text = "Register", isLoading = isLoading, onClick = {
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Lengkapi data", Toast.LENGTH_SHORT).show()
                } else if (password.length < 8) {
                    Toast.makeText(context, "Password min 8 karakter", Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Update Nama Display
                                val user = auth.currentUser
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()

                                user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { taskUpdate ->
                                        isLoading = false
                                        if (taskUpdate.isSuccessful) {
                                            Toast.makeText(context, "Register Berhasil!", Toast.LENGTH_SHORT).show()
                                            onRegisterSuccess()
                                        }
                                    }
                            } else {
                                isLoading = false
                                Toast.makeText(context, "Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            })
        }
    }
}