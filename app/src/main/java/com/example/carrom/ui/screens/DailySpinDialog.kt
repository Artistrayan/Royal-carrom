package com.example.carrom.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.carrom.audio.CarromSoundEngine
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VelvetCard
import com.example.ui.theme.VelvetDark
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SpinReward(val coins: Int, val gems: Int, val label: String, val color: Color)

val SPIN_REWARDS = listOf(
    SpinReward(500, 0, "500", GoldPrimary),
    SpinReward(1000, 10, "1K + 10💎", Color(0xFF00E5FF)),
    SpinReward(2000, 0, "2,000", Color(0xFFFF4081)),
    SpinReward(5000, 25, "5K + 25💎", EmeraldSuccess),
    SpinReward(800, 0, "800", GoldLight),
    SpinReward(3000, 15, "3K + 15💎", Color(0xFF7C4DFF)),
    SpinReward(10000, 50, "10,000 👑", Color(0xFFFFB300)),
    SpinReward(1500, 5, "1,500", Color(0xFF1DE9B6))
)

@Composable
fun DailySpinDialog(
    canSpin: Boolean,
    onClaimReward: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var isSpinning by remember { mutableStateOf(false) }
    var wonReward by remember { mutableStateOf<SpinReward?>(null) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (!isSpinning) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = VelvetDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSpinning,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                    Text(
                        text = "گردونه شانس روزانه",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Wheel & Pointer
                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Wheel Canvas
                    Canvas(
                        modifier = Modifier
                            .size(230.dp)
                            .rotate(rotation.value)
                    ) {
                        val arcAngle = 360f / SPIN_REWARDS.size
                        for (i in SPIN_REWARDS.indices) {
                            val reward = SPIN_REWARDS[i]
                            val startAngle = i * arcAngle
                            drawArc(
                                color = reward.color,
                                startAngle = startAngle,
                                sweepAngle = arcAngle,
                                useCenter = true,
                                size = Size(size.width, size.height)
                            )
                            // Outer border stroke
                            drawArc(
                                color = VelvetDark,
                                startAngle = startAngle,
                                sweepAngle = arcAngle,
                                useCenter = true,
                                style = Stroke(width = 3f),
                                size = Size(size.width, size.height)
                            )
                        }

                        // Center hub
                        drawCircle(
                            color = VelvetDark,
                            radius = 26.dp.toPx()
                        )
                        drawCircle(
                            color = GoldPrimary,
                            radius = 18.dp.toPx(),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    // Top Pointer Needle
                    Canvas(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(26.dp)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, size.height)
                            lineTo(0f, 0f)
                            lineTo(size.width, 0f)
                            close()
                        }
                        drawPath(path, color = Color(0xFFFF1744))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (wonReward != null) {
                    Text(
                        text = "جایزه شما: ${wonReward?.label} سکه/الماس!",
                        color = EmeraldSuccess,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val r = wonReward ?: return@Button
                            onClaimReward(r.coins, r.gems)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = VelvetDark)
                    ) {
                        Text(text = "دریافت جایزه", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            if (!isSpinning && canSpin) {
                                isSpinning = true
                                CarromSoundEngine.playStrikerHit(0.9f)
                                scope.launch {
                                    val randomTargetIndex = Random.nextInt(SPIN_REWARDS.size)
                                    val arcAngle = 360f / SPIN_REWARDS.size
                                    // Multiple full rotations plus landing offset
                                    val totalRotations = 360f * 5f
                                    val targetAngle = totalRotations + (360f - (randomTargetIndex * arcAngle + arcAngle / 2f))

                                    rotation.animateTo(
                                        targetValue = targetAngle,
                                        animationSpec = tween(
                                            durationMillis = 3500,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                    isSpinning = false
                                    wonReward = SPIN_REWARDS[randomTargetIndex]
                                    CarromSoundEngine.playWinFanfare()
                                }
                            }
                        },
                        enabled = canSpin && !isSpinning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("spin_wheel_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = VelvetDark,
                            disabledContainerColor = VelvetCard
                        )
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = VelvetDark)
                        Text(
                            text = if (isSpinning) "در حال چرخش..." else if (canSpin) "چرخش رایگان روزانه" else "امروز استفاده شده است",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
