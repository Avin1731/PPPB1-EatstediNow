package com.example.eatstedinow.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatstedinow.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val subtitle: String? = null,
    val description: String,
    val imageRes: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Pesan Makanan Kantin",
            subtitle = "Lebih Cepat",
            description = "Nikmati kemudahan memesan makan dan minum dari EATSTEDI langsung dari ponselmu.",
            imageRes = R.drawable.dummy
        ),
        OnboardingPage(
            title = "Pilih Menu Favoritmu",
            description = "Temukan berbagai pilihan makanan dan minuman dari EATSTEDI dengan harga terjangkau.",
            imageRes = R.drawable.dummy
        ),
        OnboardingPage(
            title = "Mulai Sekarang!",
            description = "Silakan masuk atau buat akun baru untuk melanjutkan pesanan.",
            imageRes = R.drawable.dummy
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OrangePrimary)
    ) {
        // --- MODIFIKASI DISINI: userScrollEnabled = false ---
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false // Matikan swipe manual
        ) { position ->
            val page = pages[position]

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // BAGIAN ATAS
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = page.imageRes),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(150.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "EATSTEDI Now",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // BAGIAN BAWAH
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color.White)
                        .padding(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = page.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )

                        if (page.subtitle != null) {
                            Text(
                                text = page.subtitle,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary,
                                modifier = Modifier.padding(top = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Text(
                            text = page.description,
                            fontSize = 12.sp,
                            color = GrayText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Indikator Dots
                        if (position < 2) {
                            Row(
                                modifier = Modifier.padding(bottom = 24.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(pages.size) { iteration ->
                                    val color = if (pagerState.currentPage == iteration) OrangePrimary else Color(0xFFDDDDDD)
                                    val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .height(8.dp)
                                            .width(width)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // LOGIKA TOMBOL (Navigasi Programmatic)
                        when (position) {
                            0 -> {
                                PrimaryButton(text = "Lanjutkan", onClick = {
                                    scope.launch { pagerState.animateScrollToPage(1) }
                                })
                            }
                            1 -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                                        modifier = Modifier.weight(1f).height(56.dp),
                                        shape = RoundedCornerShape(32.dp),
                                        border = BorderStroke(1.dp, Color(0xFFDDDDDD))
                                    ) {
                                        Text("Kembali", color = GrayText)
                                    }
                                    Button(
                                        onClick = { scope.launch { pagerState.animateScrollToPage(2) } },
                                        modifier = Modifier.weight(1f).height(56.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                        shape = RoundedCornerShape(32.dp)
                                    ) {
                                        Text("Lanjutkan", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            2 -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = onRegisterClick,
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                        shape = RoundedCornerShape(32.dp)
                                    ) {
                                        Text("Register", fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = onLoginClick,
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(32.dp),
                                        border = BorderStroke(1.dp, Color(0xFFDDDDDD))
                                    ) {
                                        Text("Log in", color = GrayText, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}