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
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VelvetCard
import com.example.ui.theme.VelvetDark
import com.example.ui.theme.VelvetSurface

@Composable
fun TutorialScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                        modifier = Modifier.testTag("tutorial_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "آموزش و قوانین رسمی بازی کاروم",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                TutorialCard(
                    icon = Icons.Default.Info,
                    title = "۱. هدف اصلی و تخته مسابقه",
                    content = "کاروم یک بازی مهارتی هندی-بین‌المللی روی تخته مربعی چوبی صیقلی است. هدف شما پاکت کردن مهره‌های اختصاص‌یافته خود به درون یکی از ۴ پاکت گوشه تخته است.",
                    accentColor = GoldPrimary
                )
            }

            item {
                TutorialCard(
                    icon = Icons.Default.Adjust,
                    title = "۲. ارزش مهره‌ها (Carrom Men)",
                    content = "• مهره‌های سفید (White): ۱۰ تا ۲۰ امتیاز (یا مهره اختصاصی بازیکن ۱)\n• مهره‌های مشکی (Black): ۵ تا ۱۰ امتیاز (یا مهره بازیکن ۲)\n• ملکه سرخ (Queen): ۲۵ تا ۵۰ امتیاز (با ارزش‌ترین مهره زمین)",
                    accentColor = Color(0xFFFF4081)
                )
            }

            item {
                TutorialCard(
                    icon = Icons.Default.Star,
                    title = "۳. قانون ویژه ملکه و کاور (Cover)",
                    content = "مهره ملکه سرخ تا زمانی متعلق به شما نمی‌شود که پس از پاکت کردن آن، یک مهره دیگر از خودتان را در همان نوبت یا بلافاصله بعد از آن پاکت کنید (اصطلاحاً کاور کردن). اگر کاور انجام نشود، ملکه به مرکز زمین بازگردانده می‌شود!",
                    accentColor = Color(0xFFFFD54F)
                )
            }

            item {
                TutorialCard(
                    icon = Icons.Default.ErrorOutline,
                    title = "۴. خطاها و جریمه‌ها (Fouls)",
                    content = "اگر استرایکر شما مستقیماً یا با بازتاب وارد پاکت شود، خطا محسوب می‌شود. یکی از مهره‌های پاکت‌شده قبلی شما به عنوان جریمه به مرکز زمین برمی‌گردد و نوبت به حریف واگذار می‌گردد.",
                    accentColor = Color(0xFFFF1744)
                )
            }

            item {
                TutorialCard(
                    icon = Icons.Default.TipsAndUpdates,
                    title = "۵. تکنیک شوت‌های دیواره (Cushion Rebound)",
                    content = "در صورتی که مسیر مستقیمی به مهره یا پاکت وجود نداشته باشد، با ضربه زدن به دیواره چوبی (باند) می‌توانید زاویه شلیک را بازتاب دهید و مهره‌های پشت مانع را پاکت کنید.",
                    accentColor = Color(0xFF00E5FF)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TutorialCard(
    icon: ImageVector,
    title: String,
    content: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = VelvetCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content,
                color = Color(0xFFD1C8E0),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Right
            )
        }
    }
}
