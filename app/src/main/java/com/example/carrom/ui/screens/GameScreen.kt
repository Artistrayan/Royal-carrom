package com.example.carrom.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.window.Dialog
import com.example.carrom.model.BoardSkin
import com.example.carrom.model.CarromPiece
import com.example.carrom.model.GameMode
import com.example.carrom.model.MatchRuleState
import com.example.carrom.model.MatchStatus
import com.example.carrom.model.PieceType
import com.example.carrom.model.PlayerProfile
import com.example.carrom.model.StrikerSkin
import com.example.carrom.model.TurnPlayer
import com.example.carrom.physics.AimPrediction
import com.example.carrom.physics.CarromPhysics
import com.example.carrom.physics.ImpactEffect
import com.example.carrom.ui.components.CarromBoardCanvas
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PieceBlack
import com.example.ui.theme.PieceQueen
import com.example.ui.theme.PieceWhite
import com.example.ui.theme.VelvetCard
import com.example.ui.theme.VelvetCardLight
import com.example.ui.theme.VelvetDark
import com.example.ui.theme.VelvetSurface

@Composable
fun GameScreen(
    profile: PlayerProfile,
    gameMode: GameMode,
    pieces: List<CarromPiece>,
    matchRuleState: MatchRuleState,
    matchStatus: MatchStatus,
    strikerBaselineX: Float,
    aimAngleRad: Float,
    shotPower: Float,
    isAiming: Boolean,
    aimPrediction: AimPrediction?,
    turnSecondsLeft: Int,
    activeStriker: StrikerSkin,
    activeBoard: BoardSkin,
    showGameOverDialog: Boolean,
    impactEffects: List<ImpactEffect> = emptyList(),
    onStrikerSliderChange: (Float) -> Unit,
    onAimAndPowerChange: (Float, Float) -> Unit,
    onReleaseStrike: () -> Unit,
    onRestartGame: () -> Unit,
    onExitGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPauseDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(VelvetDark, VelvetSurface, VelvetDark)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: App Bar & Players Status
            Column(modifier = Modifier.fillMaxWidth()) {
                GameTopBar(
                    gameMode = gameMode,
                    turnSecondsLeft = turnSecondsLeft,
                    onPauseClick = { showPauseDialog = true }
                )

                Spacer(modifier = Modifier.height(4.dp))

                PlayersScoreHeader(
                    profile = profile,
                    gameMode = gameMode,
                    matchRuleState = matchRuleState,
                    pieces = pieces
                )

                // Match Status Toast / Banner
                AnimatedVisibility(
                    visible = matchRuleState.statusMessage.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VelvetCardLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = matchRuleState.statusMessage,
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Center Section: 3D Carrom Board
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                CarromBoardCanvas(
                    pieces = pieces,
                    aimPrediction = aimPrediction,
                    aimAngleRad = aimAngleRad,
                    aimPower = shotPower,
                    isAiming = isAiming,
                    boardSkin = activeBoard,
                    strikerSkin = activeStriker,
                    impactEffects = impactEffects,
                    onStrikerPositionChange = onStrikerSliderChange,
                    onAimAndPowerChange = onAimAndPowerChange,
                    onReleaseStrike = onReleaseStrike
                )
            }

            // Bottom Section: Striker Rail (pure bar, no text, no arrows)
            StrikerRailBar(
                strikerX = strikerBaselineX,
                isAiming = isAiming,
                onPositionChange = onStrikerSliderChange
            )
        }

        // Pause Modal
        if (showPauseDialog) {
            PauseDialog(
                onResume = { showPauseDialog = false },
                onRestart = {
                    showPauseDialog = false
                    onRestartGame()
                },
                onExit = {
                    showPauseDialog = false
                    onExitGame()
                }
            )
        }

        // Game Over / Victory Modal
        if (showGameOverDialog) {
            GameOverDialog(
                matchRuleState = matchRuleState,
                profile = profile,
                onRestart = onRestartGame,
                onExit = onExitGame
            )
        }
    }
}

@Composable
private fun GameTopBar(
    gameMode: GameMode,
    turnSecondsLeft: Int,
    onPauseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VelvetCard)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPauseClick,
            modifier = Modifier
                .size(32.dp)
                .testTag("game_pause_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Menu",
                tint = Color.White
            )
        }

        Text(
            text = gameMode.titleFa,
            color = GoldPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        // Turn Timer
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${turnSecondsLeft}s",
                color = if (turnSecondsLeft <= 5) Color(0xFFFF1744) else GoldLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PlayersScoreHeader(
    profile: PlayerProfile,
    gameMode: GameMode,
    matchRuleState: MatchRuleState,
    pieces: List<CarromPiece>
) {
    val remainingWhite = pieces.count { !it.isPocketed && it.type == PieceType.WHITE }
    val remainingBlack = pieces.count { !it.isPocketed && it.type == PieceType.BLACK }
    val remainingQueen = pieces.count { !it.isPocketed && it.type == PieceType.QUEEN }

    val isP1Turn = matchRuleState.currentTurn == TurnPlayer.PLAYER_1

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Player 1 Card (You)
        Card(
            modifier = Modifier
                .weight(1f)
                .border(
                    width = if (isP1Turn) 2.dp else 1.dp,
                    color = if (isP1Turn) GoldPrimary else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isP1Turn) VelvetCardLight else VelvetCard
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = profile.name,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(PieceWhite)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (gameMode == GameMode.FREESTYLE) "${matchRuleState.player1Score} امتیاز" else "$remainingWhite باقی‌مانده",
                            color = GoldLight,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Player 2 / AI Card
        val isP2Turn = matchRuleState.currentTurn == TurnPlayer.PLAYER_2
        val p2Name = if (gameMode == GameMode.PASS_AND_PLAY) "بازیکن دوم" else "هوش مصنوعی (${matchRuleState.aiDifficulty.labelFa})"

        Card(
            modifier = Modifier
                .weight(1f)
                .border(
                    width = if (isP2Turn) 2.dp else 1.dp,
                    color = if (isP2Turn) Color(0xFF00E5FF) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isP2Turn) VelvetCardLight else VelvetCard
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = p2Name,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(PieceBlack)
                                .border(1.dp, Color.Gray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (gameMode == GameMode.FREESTYLE) "${matchRuleState.player2Score} امتیاز" else "$remainingBlack باقی‌مانده",
                            color = Color.LightGray,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = VelvetDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "توقف بازی",
                    color = GoldPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = VelvetDark)
                ) {
                    Text(text = "ادامه بازی", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VelvetCardLight, contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "شروع مجدد مسابقه")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F), contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "خروج به صفحه اصلی")
                }
            }
        }
    }
}

@Composable
private fun GameOverDialog(
    matchRuleState: MatchRuleState,
    profile: PlayerProfile,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    val isWinner = matchRuleState.winner == TurnPlayer.PLAYER_1

    Dialog(onDismissRequest = onExit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = VelvetDark),
            border = androidx.compose.foundation.BorderStroke(2.dp, if (isWinner) GoldPrimary else Color(0xFFD32F2F))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(if (isWinner) GoldPrimary.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = if (isWinner) GoldPrimary else Color.Red,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isWinner) "پیروزی باشکوه! تبریک!" else "پایان مسابقه!",
                    color = if (isWinner) GoldPrimary else Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = matchRuleState.statusMessage,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (isWinner) {
                    Text(
                        text = "+۱,۵۰۰ سکه جایزه قهرمانی دریافت شد!",
                        color = EmeraldSuccess,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("game_over_retry_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = VelvetDark)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = VelvetDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "مسابقه جدید", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("game_over_home_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VelvetCardLight, contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "بازگشت به صفحه اصلی", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun StrikerRailBar(
    strikerX: Float,
    isAiming: Boolean,
    onPositionChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val minX = CarromPhysics.BASELINE_MIN_X
    val maxX = CarromPhysics.BASELINE_MAX_X
    val fraction = ((strikerX - minX) / (maxX - minX)).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Slider(
            value = fraction,
            onValueChange = { newFraction ->
                val newX = minX + newFraction * (maxX - minX)
                onPositionChange(newX)
            },
            enabled = isAiming,
            colors = SliderDefaults.colors(
                thumbColor = GoldPrimary,
                activeTrackColor = GoldPrimary,
                inactiveTrackColor = VelvetCardLight,
                disabledThumbColor = Color.DarkGray,
                disabledActiveTrackColor = Color.DarkGray,
                disabledInactiveTrackColor = VelvetDark
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("striker_rail_slider")
        )
    }
}

