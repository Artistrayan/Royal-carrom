package com.example.carrom.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.carrom.model.BoardSkin
import com.example.carrom.model.PlayerProfile
import com.example.carrom.model.StrikerSkin
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VelvetCard
import com.example.ui.theme.VelvetCardLight
import com.example.ui.theme.VelvetDark
import com.example.ui.theme.VelvetSurface

@Composable
fun ProfileScreen(
    profile: PlayerProfile,
    strikerSkins: List<StrikerSkin>,
    boardSkins: List<BoardSkin>,
    activeStrikerId: String,
    activeBoardId: String,
    onEquipStriker: (StrikerSkin) -> Unit,
    onBuyStriker: (StrikerSkin) -> Unit,
    onEquipBoard: (BoardSkin) -> Unit,
    onBuyBoard: (BoardSkin) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val winRate = if (profile.totalMatches > 0) {
        ((profile.wins.toFloat() / profile.totalMatches) * 100).toInt()
    } else 0

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
            // App Bar
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("profile_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "پروفایل و کلکسیون سلطنتی",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Coin Counter
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(VelvetCard)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
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
                    }
                }
            }

            // Profile Card & Career Stats
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VelvetCard)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, GoldPrimary, CircleShape)
                                .background(VelvetCardLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Profile",
                                tint = GoldPrimary,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Text(
                            text = profile.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Text(
                            text = "سطح ${profile.level} • ${profile.trophies} کاپ قهرمانی جهان",
                            color = GoldLight,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid 2x2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatBox(label = "کل مسابقات", value = "${profile.totalMatches}")
                            StatBox(label = "پیروزی‌ها", value = "${profile.wins}")
                            StatBox(label = "درصد برد", value = "$winRate%")
                            StatBox(label = "کاور ملکه", value = "${profile.queenCovers}")
                        }
                    }
                }
            }

            // Tabs: Strikers vs Boards
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = VelvetCard,
                    contentColor = GoldPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GoldPrimary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "استرایکرها (${strikerSkins.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "میزهای مسابقات (${boardSkins.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab 0: Strikers
            if (selectedTab == 0) {
                items(strikerSkins) { skin ->
                    val isEquipped = skin.id == activeStrikerId
                    StrikerSkinCard(
                        skin = skin,
                        isEquipped = isEquipped,
                        userCoins = profile.coins,
                        onEquip = { onEquipStriker(skin) },
                        onBuy = { onBuyStriker(skin) }
                    )
                }
            } else {
                // Tab 1: Board Skins
                items(boardSkins) { skin ->
                    val isEquipped = skin.id == activeBoardId
                    BoardSkinCard(
                        skin = skin,
                        isEquipped = isEquipped,
                        userCoins = profile.coins,
                        onEquip = { onEquipBoard(skin) },
                        onBuy = { onBuyBoard(skin) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = GoldPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun StrikerSkinCard(
    skin: StrikerSkin,
    isEquipped: Boolean,
    userCoins: Int,
    onEquip: () -> Unit,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) VelvetCardLight else VelvetCard
        ),
        border = if (isEquipped) androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Striker Circle Preview
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(skin.primaryColor), Color(skin.accentColor))
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skin.nameFa,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = skin.nameEn,
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "قدرت: ", color = GoldLight, fontSize = 10.sp)
                    repeat(skin.powerRating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Action Button
            if (isEquipped) {
                Button(
                    onClick = {},
                    enabled = false,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = EmeraldSuccess.copy(alpha = 0.2f),
                        disabledContentColor = EmeraldSuccess
                    )
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "انتخاب شده", fontSize = 11.sp)
                }
            } else if (skin.isUnlocked) {
                Button(
                    onClick = onEquip,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = VelvetDark)
                ) {
                    Text(text = "تجهیز", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onBuy,
                    enabled = userCoins >= skin.price,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldDark, contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${skin.price} سکه", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun BoardSkinCard(
    skin: BoardSkin,
    isEquipped: Boolean,
    userCoins: Int,
    onEquip: () -> Unit,
    onBuy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) VelvetCardLight else VelvetCard
        ),
        border = if (isEquipped) androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Board square preview
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(skin.borderWoodColor))
                    .padding(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(skin.surfaceColor))
                        .border(1.dp, Color(skin.linesColor), RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skin.nameFa,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = skin.nameEn,
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }

            // Action Button
            if (isEquipped) {
                Button(
                    onClick = {},
                    enabled = false,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = EmeraldSuccess.copy(alpha = 0.2f),
                        disabledContentColor = EmeraldSuccess
                    )
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "میز فعال", fontSize = 11.sp)
                }
            } else if (skin.isUnlocked) {
                Button(
                    onClick = onEquip,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = VelvetDark)
                ) {
                    Text(text = "انتخاب میز", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onBuy,
                    enabled = userCoins >= skin.price,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldDark, contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${skin.price} سکه", fontSize = 11.sp)
                }
            }
        }
    }
}
