package com.example.carrom.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.carrom.audio.CarromSoundEngine
import com.example.carrom.model.GameMode
import com.example.carrom.model.PlayerProfile
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VelvetCard
import com.example.ui.theme.VelvetCardLight
import com.example.ui.theme.VelvetDark
import com.example.ui.theme.VelvetSurface

@Composable
fun HomeScreen(
    profile: PlayerProfile,
    onSelectMode: (GameMode) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenTutorial: () -> Unit,
    onOpenPractice: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenSpin: () -> Unit,
    onOpenModeSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    var chestClaimed by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F0B18),
                        Color(0xFF191228),
                        Color(0xFF100C1A)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Header: VIP Profile & Currency Capsules
            GameTopHeader(
                profile = profile,
                onOpenProfile = onOpenProfile
            )

            // 2. Scrollable Action Arena
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Season Pass / Royal League Banner
                item {
                    SeasonPassBanner(onOpenProfile = onOpenProfile)
                }

                // Visual Arena Card 1: DISC POOL (AAA Action Visual)
                item {
                    ArenaModeBanner(
                        title = "دیسک پول مسابقاتی",
                        subtitle = "DISC POOL • ۱ به ۱ سرعتی",
                        imageRes = R.drawable.img_mode_disc_pool,
                        badgeText = "🔥 پرطرفدارترین",
                        badgeColor = Color(0xFF00E5FF),
                        entryFee = 500,
                        prizePool = 1000,
                        pulseScale = pulseScale,
                        onClick = { onSelectMode(GameMode.DISC_POOL) },
                        testTag = "home_arena_disc_pool"
                    )
                }

                // Visual Arena Card 2: CLASSIC CARROM (Royal Queen & Cover)
                item {
                    ArenaModeBanner(
                        title = "جام سلطنتی کاروم",
                        subtitle = "CLASSIC • ملکه سرخ و کاور",
                        imageRes = R.drawable.img_mode_classic,
                        badgeText = "👑 تورنمنت جهانی",
                        badgeColor = Color(0xFFFFD700),
                        entryFee = 1000,
                        prizePool = 2000,
                        pulseScale = 1.0f,
                        onClick = { onSelectMode(GameMode.CLASSIC) },
                        testTag = "home_arena_classic"
                    )
                }

                // 2-Column Grid: VS AI & PASS & PLAY
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // VS AI
                        SecondaryModeCard(
                            title = "نبرد با هوش مصنوعی",
                            subTitle = "۳ سطح مهارت",
                            tag = "AI BOT",
                            icon = Icons.Default.SmartToy,
                            accentColor = Color(0xFF00E5FF),
                            modifier = Modifier.weight(1f),
                            onClick = { onSelectMode(GameMode.VS_AI) },
                            testTag = "home_arena_vs_ai"
                        )

                        // PASS & PLAY
                        SecondaryModeCard(
                            title = "دونفره محلی",
                            subTitle = "یک گوشی بدون نت",
                            tag = "PASS & PLAY",
                            icon = Icons.Default.People,
                            accentColor = Color(0xFFFF9100),
                            modifier = Modifier.weight(1f),
                            onClick = { onSelectMode(GameMode.PASS_AND_PLAY) },
                            testTag = "home_arena_pass_and_play"
                        )
                    }
                }

                // Mystery Reward Chest & Lucky Spin Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Mystery Gold Chest
                        ChestRewardCard(
                            isClaimed = chestClaimed,
                            onClaim = {
                                if (!chestClaimed) {
                                    chestClaimed = true
                                    CarromSoundEngine.playWinFanfare()
                                }
                            },
                            modifier = Modifier.weight(1.1f)
                        )

                        // Daily Spin Wheel
                        DailySpinCard(
                            pulseScale = pulseScale,
                            onClick = onOpenSpin,
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // 3. Bottom Glassmorphic Navigation Dock
            GameBottomDock(
                onOpenHome = { /* Already here */ },
                onOpenPractice = onOpenPractice,
                onOpenShop = onOpenProfile,
                onOpenTutorial = onOpenTutorial,
                onOpenSupport = onOpenSupport
            )
        }
    }
}

@Composable
private fun GameTopHeader(
    profile: PlayerProfile,
    onOpenProfile: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF221735), Color(0xFF160F24))
                )
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player Avatar & Level Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onOpenProfile() }
                .testTag("home_header_profile")
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(2.dp, Brush.sweepGradient(listOf(GoldPrimary, Color(0xFFFF9100), GoldPrimary)), CircleShape)
                        .background(VelvetCardLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Avatar",
                        tint = GoldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                // Level Pill Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF00E5FF))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "L${profile.level}",
                        color = VelvetDark,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(GoldPrimary)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "VIP",
                            color = VelvetDark,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = GoldLight, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${profile.trophies} کاپ",
                        color = GoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Currency Capsules (Coins & Gems)
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Coins Capsule
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0A0711))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile.coins}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = VelvetDark, modifier = Modifier.size(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Gems Capsule
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0A0711))
                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "Gems",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${profile.gems}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Sound Toggle
            IconButton(
                onClick = {
                    CarromSoundEngine.isSoundEnabled = !CarromSoundEngine.isSoundEnabled
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Sound",
                    tint = if (CarromSoundEngine.isSoundEnabled) GoldPrimary else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SeasonPassBanner(onOpenProfile: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onOpenProfile() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1947)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Brush.horizontalGradient(listOf(GoldPrimary, Color(0xFFFF4081))))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = VelvetDark, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "فصل اول: پادشاهی طلایی (CARROM PASS)",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "جوایز استرایکر افسانه‌ای در سطح ۱۰",
                        color = Color(0xFFE1BEE7),
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.horizontalGradient(listOf(GoldPrimary, Color(0xFFFF9100))))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "مشاهده",
                    color = VelvetDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun ArenaModeBanner(
    title: String,
    subtitle: String,
    imageRes: Int,
    badgeText: String,
    badgeColor: Color,
    entryFee: Int,
    prizePool: Int,
    pulseScale: Float,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(12.dp, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VelvetCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, badgeColor.copy(alpha = 0.8f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Visual Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Vignette Gradient Overlays
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.85f),
                                Color.Black.copy(alpha = 0.45f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = VelvetDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Entry & Prize Capsule
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(1.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "جایزه: ", color = Color.LightGray, fontSize = 10.sp)
                            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(13.dp))
                            Text(text = " $prizePool", color = EmeraldSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Title & Action Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = subtitle,
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Big Action Button
                    Button(
                        onClick = onClick,
                        modifier = Modifier
                            .scale(pulseScale)
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = VelvetDark
                        )
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = VelvetDark, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "ورود به مسابقه", fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun SecondaryModeCard(
    title: String,
    subTitle: String,
    tag: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = VelvetCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(22.dp))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = tag, color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column {
                Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = subTitle, color = Color.LightGray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun ChestRewardCard(
    isClaimed: Boolean,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable { onClaim() }
            .testTag("home_chest_reward"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E142E)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (!isClaimed) GoldPrimary else Color.DarkGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3D Chest Visual
            Image(
                painter = painterResource(id = R.drawable.img_chest_gold),
                contentDescription = "Chest",
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "صندوقچه سلطنتی",
                        color = GoldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (!isClaimed) "+۱۰۰۰ سکه رایگان!" else "باز شده",
                        color = if (!isClaimed) EmeraldSuccess else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isClaimed) EmeraldSuccess else Color.DarkGray)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (!isClaimed) "باز کردن" else "دریافت شد",
                        color = if (!isClaimed) VelvetDark else Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun DailySpinCard(
    pulseScale: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() }
            .testTag("home_spin_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1230)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF4081))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(Color(0xFFFF4081).copy(alpha = 0.2f))
                    .border(2.dp, Color(0xFFFF4081), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = "Spin",
                    tint = Color(0xFFFF4081),
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "گردونه شانس",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "تا ۱۰,۰۰۰ سکه",
                    color = GoldLight,
                    fontSize = 10.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFF4081))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "چرخش رایگان",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun GameBottomDock(
    onOpenHome: () -> Unit,
    onOpenPractice: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenTutorial: () -> Unit,
    onOpenSupport: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF160E22), Color(0xFF0D0816))
                )
            )
            .border(width = 1.dp, color = Color(0xFF33204E))
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DockItem(icon = Icons.Default.Home, label = "لابی", isSelected = true, onClick = onOpenHome, testTag = "dock_home")
        DockItem(icon = Icons.Default.TrackChanges, label = "تمرین", isSelected = false, onClick = onOpenPractice, testTag = "dock_practice")
        DockItem(icon = Icons.Default.ShoppingBag, label = "فروشگاه", isSelected = false, onClick = onOpenShop, testTag = "dock_shop")
        DockItem(icon = Icons.Default.HelpOutline, label = "قوانین", isSelected = false, onClick = onOpenTutorial, testTag = "dock_tutorial")
        DockItem(icon = Icons.Default.SupportAgent, label = "پشتیبانی", isSelected = false, onClick = onOpenSupport, testTag = "dock_support")
    }
}

@Composable
private fun DockItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(testTag)
            .padding(horizontal = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (isSelected) GoldPrimary.copy(alpha = 0.2f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) GoldPrimary else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            color = if (isSelected) GoldPrimary else Color.Gray,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
