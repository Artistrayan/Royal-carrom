package com.example.carrom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VelvetCard
import com.example.ui.theme.VelvetCardLight
import com.example.ui.theme.VelvetDark
import com.example.ui.theme.VelvetSurface

@Composable
fun SupportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var feedbackText by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(VelvetDark, VelvetSurface, VelvetDark)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("support_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "پشتیبانی و ارتباط با توسعه‌دهنده",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VelvetCard)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.HeadsetMic, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "تیم توسعه بازی سلطنتی کاروم ۳بعدی",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ایمیل پشتیبانی: support@royalcarrom3d.com",
                                color = GoldLight,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "پرسش‌های متداول بازیکنان:",
                    color = GoldLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Right
                )
            }

            item {
                FaqItem(
                    q = "آیا این بازی به اینترنت نیاز دارد؟",
                    a = "خیر، بازی به صورت ۱۰۰٪ آفلاین و با موتور فیزیک بومی اندروید اجرا می‌شود و تمام حالت‌های دو نفره و نبرد با هوش مصنوعی بدون نیاز به اینترنت در دسترس هستند."
                )
            }

            item {
                FaqItem(
                    q = "چگونه سکه و الماس بیشتری کسب کنم؟",
                    a = "از طریق چرخش روزانه گردونه شانس، پیروزی در مسابقات سریع و حل مراحل تمرینی تریک‌شات می‌توانید سکه‌های زیادی جمع‌آوری کنید."
                )
            }

            item {
                FaqItem(
                    q = "چرا ملکه بعد از پاکت شدن به وسط برگشت؟",
                    a = "طبق قوانین رسمی کاروم، بلافاصله در همان ضربه یا ضربه بعدی باید یک مهره از رنگ خودتان را به عنوان کاور پاکت کنید. در غیر این صورت ملکه به مرکز بازمی‌گردد."
                )
            }

            // Feedback form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = VelvetCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "ارسال پیشنهاد و نظر شما:",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isSubmitted) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmeraldSuccess.copy(alpha = 0.2f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "پیام شما با موفقیت ثبت شد! سپاس از همراهی شما.",
                                    color = EmeraldSuccess,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = feedbackText,
                                onValueChange = { feedbackText = it },
                                placeholder = { Text("پیشنهاد یا بازخورد خود را در مورد فیزیک یا گرافیک بنویسید...", color = Color.Gray, fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .testTag("support_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = Color.DarkGray,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    if (feedbackText.isNotBlank()) {
                                        isSubmitted = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("support_submit_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = VelvetDark)
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = VelvetDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "ثبت و ارسال پیام", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun FaqItem(q: String, a: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = VelvetCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.QuestionAnswer, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = q,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = a,
                color = Color(0xFFD1C8E0),
                fontSize = 11.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Right
            )
        }
    }
}
