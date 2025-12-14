package com.example.eatstedinow.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatstedinow.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // SETUP GOOGLE SIGN IN
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                isLoading = true
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        isLoading = false
                        if (authTask.isSuccessful) {
                            Toast.makeText(context, "Login Google Berhasil", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        } else {
                            Toast.makeText(context, "Gagal: ${authTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                isLoading = false
                Toast.makeText(context, "Google Error: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

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

            Text("Selamat Datang\nKembali!", fontSize = 26.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp)
            Text("Masuk ke akun Anda", fontSize = 14.sp, color = GrayText, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))

            AuthTextField(value = email, onValueChange = { email = it }, label = "Email", hint = "johndoe@gmail.com", keyboardType = KeyboardType.Email)
            AuthTextField(value = password, onValueChange = { password = it }, label = "Kata Sandi", hint = "min. 8 karakter", isPassword = true)

            Text("Lupa kata sandi?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OrangePrimary, modifier = Modifier.align(Alignment.End))

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(text = "Login", isLoading = isLoading, onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Isi semua data", Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                onLoginSuccess()
                            } else {
                                Toast.makeText(context, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            })

            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                Text("atau", color = GrayText, fontSize = 12.sp)
            }

            GoogleButton(isLoading = isLoading, onClick = {
                isLoading = true
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            })

            Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.Center) {
                Text("Belum punya akun? ", color = Color.Black)
                Text("Register", color = OrangePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onRegisterClick() })
            }
        }
    }
}