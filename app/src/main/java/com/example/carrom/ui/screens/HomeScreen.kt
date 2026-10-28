package com.example.carrom.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SmartToy
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Data model for each 3D Cubic Option in the circular carousel
 */
data class HomeCubicOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val badgeColor: Color,
    val accentColor: Color,
    val imageRes: Int? = null,
    val icon: ImageVector? = null,
    val feeText: String? = null,
    val prizeText: String? = null,
    val actionButtonText: String,
    val testTag: String,
    val onClick: () -> Unit
)

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
    val coroutineScope = rememberCoroutineScope()

    // Definition of all options presented as 3D Square Cubic Cards in the circular carousel
    val options = remember(profile) {
        listOf(
            HomeCubicOption(
                id = "disc_pool",
                title = "دیسک پول مسابقاتی",
                subtitle = "۱ به ۱ سرعتی • بدون ملکه",
                badgeText = "🔥 پرطرفدارترین",
                badgeColor = Color(0xFF00E5FF),
                accentColor = Color(0xFF00E5FF),
                imageRes = R.drawable.img_mode_disc_pool,
                feeText = "ورودی: ۵۰۰ سکه",
                prizeText = "جایزه: ۱,۰۰۰ سکه",
                actionButtonText = "ورود به مسابقه",
                testTag = "home_arena_disc_pool",
                onClick = { onSelectMode(GameMode.DISC_POOL) }
            ),
            HomeCubicOption(
                id = "classic",
                title = "جام سلطنتی کاروم",
                subtitle = "ملکه سرخ و کاور • قوانین کامل",
                badgeText = "👑 تورنمنت جهانی",
                badgeColor = Color(0xFFFFD700),
                accentColor = GoldPrimary,
                imageRes = R.drawable.img_mode_classic,
                feeText = "ورودی: ۱,۰۰۰ سکه",
                prizeText = "جایزه: ۲,۰۰۰ سکه",
                actionButtonText = "شروع تورنمنت",
                testTag = "home_arena_classic",
                onClick = { onSelectMode(GameMode.CLASSIC) }
            ),
            HomeCubicOption(
                id = "vs_ai",
                title = "نبرد با هوش مصنوعی",
                subtitle = "آفلاین • ۳ سطح مهارت هوشمند",
                badgeText = "🤖 هوش مصنوعی",
                badgeColor = Color(0xFF7C4DFF),
                accentColor = Color(0xFFB388FF),
                icon = Icons.Default.SmartToy,
                feeText = "بدون نیاز به اینترنت",
                prizeText = "افزایش مهارت و کاپ",
                actionButtonText = "مبارزه با ربات",
                testTag = "home_arena_vs_ai",
                onClick = { onSelectMode(GameMode.VS_AI) }
            ),
            HomeCubicOption(
                id = "pass_and_play",
                title = "بازی دونفره محلی",
                subtitle = "رقابت دوستانه روی یک گوشی",
                badgeText = "👥 دونفره محلی",
                badgeColor = Color(0xFFFF9100),
                accentColor = Color(0xFFFFAB40),
                icon = Icons.Default.People,
                feeText = "آفلاین و دورهمی",
                prizeText = "بدون کسر سکه",
                actionButtonText = "شروع دونفره",
                testTag = "home_arena_pass_and_play",
                onClick = { onSelectMode(GameMode.PASS_AND_PLAY) }
            ),
            HomeCubicOption(
                id = "practice",
                title = "شلیک‌های نمایشی و تمرین",
                subtitle = "آموزش ضربات زاویه‌دار و پاکتینگ",
                badgeText = "🎯 تمرین و چالش",
                badgeColor = Color(0xFF00E676),
                accentColor = EmeraldSuccess,
                icon = Icons.Default.TrackChanges,
                feeText = "سناریوهای حرفه‌ای",
                prizeText = "آزادسازی مهارت‌ها",
                actionButtonText = "ورود به تمرین",
                testTag = "home_arena_practice",
                onClick = { onOpenPractice() }
            ),
            HomeCubicOption(
                id = "spin",
                title = "گردونه شانس روزانه",
                subtitle = "سکه و الماس رایگان هر ۲۴ ساعت",
                badgeText = "🎁 جایزه روزانه",
                badgeColor = Color(0xFFFF4081),
                accentColor = Color(0xFFFF80AB),
                imageRes = R.drawable.img_chest_gold,
                feeText = "چرخش ۱۰۰٪ رایگان",
                prizeText = "تا ۱۰,۰۰۰ سکه",
                actionButtonText = "چرخش گردونه",
                testTag = "home_spin_card",
                onClick = { onOpenSpin() }
            ),
            HomeCubicOption(
                id = "tutorial",
                title = "آموزش و قوانین بین‌المللی",
                subtitle = "راهنمای گام به گام و استراتژی",
                badgeText = "📖 آکادمی کاروم",
                badgeColor = Color(0xFF29B6F6),
                accentColor = Color(0xFF81D4FA),
                icon = Icons.Default.HelpOutline,
                feeText = "قوانین رسمی WCF",
                prizeText = "آموزش رایگان",
                actionButtonText = "مشاهده آموزش",
                testTag = "home_tutorial_card",
                onClick = { onOpenTutorial() }
            ),
            HomeCubicOption(
                id = "support",
                title = "پشتیبانی و ارتباط با ما",
                subtitle = "پاسخگویی سریع، تیکت و راهنمایی",
                badgeText = "🎧 پشتیبانی ۲۴/۷",
                badgeColor = Color(0xFFAB47BC),
                accentColor = Color(0xFFCE93D8),
                icon = Icons.Default.SupportAgent,
                feeText = "پاسخگویی آنلاین",
                prizeText = "پشتیبانی بازیکنان",
                actionButtonText = "ارسال پیام",
                testTag = "home_support_card",
                onClick = { onOpenSupport() }
            )
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { options.size })

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitAngle"
    )
    val buttonPulse by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D0817),
                        Color(0xFF160E24),
                        Color(0xFF0E0918)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Header: VIP Profile & Currency
            GameTopHeader(
                profile = profile,
                onOpenProfile = onOpenProfile
            )

            // 2. Center Section: 3D Circular Carousel of Square Cubic Cards
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Background 3D Circular Pedestal & Orbiting Rings
                CircularPedestalCanvas(
                    orbitAngle = orbitAngle,
                    modifier = Modifier.fillMaxSize()
                )

                // The 3D Circular Horizontal Pager of Square Cubic Cards
                val density = LocalDensity.current.density
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    pageSpacing = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) { page ->
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                        .coerceIn(-2.5f, 2.5f)

                    // 3D Circular Rotation and Parabolic Orbit Transformation
                    val scale = (1f - (abs(pageOffset) * 0.15f)).coerceIn(0.76f, 1f)
                    val alpha = (1f - (abs(pageOffset) * 0.35f)).coerceIn(0.55f, 1f)
                    val rotY = pageOffset * 28f // 3D Y-axis circular perspective
                    val transY = abs(pageOffset) * 16.dp.value * density // Orbital circular depth curve

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                rotationY = rotY
                                translationY = transY
                                this.alpha = alpha
                                cameraDistance = 14f * density
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        CubicOptionCard(
                            option = options[page],
                            isSelected = page == pagerState.currentPage,
                            pulseScale = if (page == pagerState.currentPage) buttonPulse else 1f
                        )
                    }
                }

                // Left Quick Navigation Button
                IconButton(
                    onClick = {
                        if (pagerState.currentPage > 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(options.size - 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 6.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B112B).copy(alpha = 0.85f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "قبلی",
                        tint = GoldLight,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Right Quick Navigation Button
                IconButton(
                    onClick = {
                        if (pagerState.currentPage < options.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 6.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B112B).copy(alpha = 0.85f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "بعدی",
                        tint = GoldLight,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Circular Carousel Dots Indicator (Bottom of Center Section)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    options.indices.forEach { index ->
                        val isSelected = index == pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (isSelected) 22.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isSelected) GoldPrimary else Color.White.copy(alpha = 0.25f)
                                )
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }
            }

            // 3. Bottom Glassmorphic Navigation Dock
            GameBottomDock(
                onOpenHome = { /* Current screen */ },
                onOpenPractice = onOpenPractice,
                onOpenShop = onOpenProfile,
                onOpenTutorial = onOpenTutorial,
                onOpenSupport = onOpenSupport,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}

/**
 * 3D Square Cubic Card Composable ("کارت مکعب مربع")
 */
@Composable
private fun CubicOptionCard(
    option: HomeCubicOption,
    isSelected: Boolean,
    pulseScale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .widthIn(max = 290.dp)
            .aspectRatio(1f) // True Square Cube Aspect Ratio
            .shadow(
                elevation = if (isSelected) 16.dp else 6.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = option.accentColor.copy(alpha = 0.6f)
            )
            .border(
                width = if (isSelected) 2.2.dp else 1.2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        if (isSelected) option.accentColor else GoldDark.copy(alpha = 0.4f),
                        Color(0xFF382352),
                        if (isSelected) GoldLight else Color(0xFF1E142B)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .clickable { option.onClick() }
            .testTag(option.testTag),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C122A))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Cubic 3D Lighting & Texture Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                option.accentColor.copy(alpha = if (isSelected) 0.22f else 0.1f),
                                Color(0xFF1E132D),
                                Color(0xFF120A1D)
                            ),
                            center = Offset(300f, 150f),
                            radius = 450f
                        )
                    )
            )

            // Top Isometric Bevel Highlight (Cube Top Face simulation)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                (if (isSelected) option.accentColor else GoldPrimary).copy(alpha = 0.7f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Card Inner Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Top Row: Badge & Mode Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Floating Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(option.badgeColor.copy(alpha = 0.22f))
                            .border(1.dp, option.badgeColor.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = option.badgeText,
                            color = option.badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // 3D Cubic Emblem Icon
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF28193D))
                            .border(1.dp, option.accentColor.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = option.icon ?: Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = option.accentColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // 2. Middle Visual: Hero Image or Holographic 3D Icon
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF241538))
                        .border(
                            1.5.dp,
                            Brush.sweepGradient(
                                listOf(option.accentColor, GoldPrimary, option.accentColor)
                            ),
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (option.imageRes != null) {
                        Image(
                            painter = painterResource(id = option.imageRes),
                            contentDescription = option.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (option.icon != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        listOf(option.accentColor.copy(alpha = 0.35f), Color.Transparent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = option.title,
                                tint = option.accentColor,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }
                }

                // 3. Title & Subtitle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = option.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = option.subtitle,
                        color = GoldLight.copy(alpha = 0.85f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }

                // 4. Action Button with 3D pulse
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(if (isSelected) pulseScale else 1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    option.accentColor,
                                    Color(0xFFFFD54F)
                                )
                            )
                        )
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = VelvetDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = option.actionButtonText,
                            color = VelvetDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3D Circular Pedestal & Orbital Rings drawn on Canvas
 */
@Composable
private fun CircularPedestalCanvas(
    orbitAngle: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f + 50f
        val radiusX = size.width * 0.44f
        val radiusY = size.height * 0.16f

        // Outer Elliptical Orbital Glow Ring
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    GoldPrimary.copy(alpha = 0.25f),
                    Color(0xFF7C4DFF).copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = radiusX
            ),
            topLeft = Offset(centerX - radiusX, centerY - radiusY),
            size = Size(radiusX * 2, radiusY * 2)
        )

        // Metallic Orbital Track
        drawOval(
            color = GoldPrimary.copy(alpha = 0.35f),
            topLeft = Offset(centerX - radiusX, centerY - radiusY),
            size = Size(radiusX * 2, radiusY * 2),
            style = Stroke(width = 2.dp.toPx())
        )

        // Inner Dashed Orbital Ring
        drawOval(
            color = Color(0xFF00E5FF).copy(alpha = 0.25f),
            topLeft = Offset(centerX - radiusX * 0.75f, centerY - radiusY * 0.75f),
            size = Size(radiusX * 1.5f, radiusY * 1.5f),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

/**
 * Top Header: Profile Info, Trophies, Coins, Gems, Sound Toggle
 */
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
                    listOf(Color(0xFF1E132E), Color(0xFF140C20))
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
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(2.dp, Brush.sweepGradient(listOf(GoldPrimary, Color(0xFFFF9100), GoldPrimary)), CircleShape)
                        .background(VelvetCardLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Avatar",
                        tint = GoldPrimary,
                        modifier = Modifier.size(26.dp)
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
                        fontSize = 8.sp,
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
                        fontSize = 13.sp,
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

        // Currency Capsules (Coins & Gems) & Audio Toggle
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Coins Capsule
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0A0711))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 7.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Coins",
                        tint = GoldPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${profile.coins}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Gems Capsule
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0A0711))
                    .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 7.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "Gems",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${profile.gems}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Sound Toggle
            IconButton(
                onClick = {
                    CarromSoundEngine.isSoundEnabled = !CarromSoundEngine.isSoundEnabled
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Sound",
                    tint = if (CarromSoundEngine.isSoundEnabled) GoldPrimary else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Bottom Glassmorphic Navigation Dock
 */
@Composable
private fun GameBottomDock(
    onOpenHome: () -> Unit,
    onOpenPractice: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenTutorial: () -> Unit,
    onOpenSupport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF160E22), Color(0xFF0D0816))
                )
            )
            .border(width = 1.dp, color = Color(0xFF33204E))
            .padding(vertical = 6.dp, horizontal = 12.dp),
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
    icon: ImageVector,
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
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) GoldPrimary.copy(alpha = 0.2f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) GoldPrimary else Color.Gray,
                modifier = Modifier.size(20.dp)
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
