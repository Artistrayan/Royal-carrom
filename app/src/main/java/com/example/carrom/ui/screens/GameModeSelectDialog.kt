package com.example.carrom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.carrom.model.AIDifficulty
import com.example.carrom.model.GameMode
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VelvetCard
import com.example.ui.theme.VelvetCardLight
import com.example.ui.theme.VelvetDark

@Composable
fun GameModeSelectDialog(
    initialMode: GameMode,
    onConfirm: (GameMode, AIDifficulty) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMode by remember { mutableStateOf(initialMode) }
    var selectedDifficulty by remember { mutableStateOf(AIDifficulty.MEDIUM) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = VelvetDark),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                    Text(
                        text = "انتخاب مسابقه و قوانین",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Selector Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val modes = listOf(
                        GameMode.DISC_POOL,
                        GameMode.CLASSIC,
                        GameMode.VS_AI,
                        GameMode.FREESTYLE
                    )
                    for (m in modes) {
                        val isSelected = selectedMode == m
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) VelvetCardLight else VelvetCard)
                                .border(
                                    1.dp,
                                    if (isSelected) GoldPrimary else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedMode = m }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = m.titleFa,
                                        color = if (isSelected) GoldPrimary else Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = m.descFa,
                                        color = Color.LightGray,
                                        fontSize = 10.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }

                // AI Difficulty Selector if VS_AI selected
                if (selectedMode == GameMode.VS_AI) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "سطح هوش مصنوعی حریف:",
                        color = GoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (d in AIDifficulty.values()) {
                            val isSel = selectedDifficulty == d
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) GoldPrimary else VelvetCard)
                                    .clickable { selectedDifficulty = d }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = d.labelFa,
                                    color = if (isSel) VelvetDark else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Entry Fee & Prize
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VelvetCard)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "ورودی: ", color = Color.LightGray, fontSize = 12.sp)
                        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Text(text = " ۵۰۰", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "جایزه برد: ", color = Color.LightGray, fontSize = 12.sp)
                        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Text(text = " ۱,۰۰۰", color = EmeraldSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Start Match Button
                Button(
                    onClick = { onConfirm(selectedMode, selectedDifficulty) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dialog_start_game_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = VelvetDark)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = VelvetDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "شروع مسابقه", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
