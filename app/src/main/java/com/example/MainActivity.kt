package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.carrom.ui.screens.AuthScreen
import com.example.carrom.ui.screens.DailySpinDialog
import com.example.carrom.ui.screens.GameModeSelectDialog
import com.example.carrom.ui.screens.GameScreen
import com.example.carrom.ui.screens.HomeScreen
import com.example.carrom.ui.screens.PracticeScreen
import com.example.carrom.ui.screens.ProfileScreen
import com.example.carrom.ui.screens.SplashScreen
import com.example.carrom.ui.screens.SupportScreen
import com.example.carrom.ui.screens.TutorialScreen
import com.example.carrom.viewmodel.AppScreen
import com.example.carrom.viewmodel.CarromGameViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CarromGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val state by viewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CarromAppContent(
                        state = state,
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CarromAppContent(
    state: com.example.carrom.viewmodel.CarromUiState,
    viewModel: CarromGameViewModel,
    modifier: Modifier = Modifier
) {
    // Android System Back Navigation Handling
    BackHandler(enabled = state.currentScreen != AppScreen.HOME && state.currentScreen != AppScreen.SPLASH) {
        viewModel.navigateTo(AppScreen.HOME)
    }

    when (state.currentScreen) {
        AppScreen.SPLASH -> {
            SplashScreen(modifier = modifier)
        }

        AppScreen.AUTH -> {
            AuthScreen(
                currentName = state.profile.name,
                currentAvatarIdx = state.profile.avatarIndex,
                onSaveProfile = { name, avatar ->
                    viewModel.updateProfileName(name, avatar)
                },
                modifier = modifier
            )
        }

        AppScreen.HOME -> {
            HomeScreen(
                profile = state.profile,
                onSelectMode = { mode ->
                    viewModel.startNewGame(mode)
                },
                onOpenProfile = { viewModel.navigateTo(AppScreen.PROFILE) },
                onOpenTutorial = { viewModel.navigateTo(AppScreen.TUTORIAL) },
                onOpenPractice = { viewModel.navigateTo(AppScreen.PRACTICE) },
                onOpenSupport = { viewModel.navigateTo(AppScreen.SUPPORT) },
                onOpenSpin = { viewModel.openSpinDialog() },
                onOpenModeSelector = { viewModel.openGameModeDialog() },
                modifier = modifier
            )

            // Dialogs
            if (state.showGameModeDialog) {
                GameModeSelectDialog(
                    initialMode = state.gameMode,
                    onConfirm = { mode, difficulty ->
                        viewModel.startNewGame(mode, difficulty)
                    },
                    onDismiss = { viewModel.closeGameModeDialog() }
                )
            }

            if (state.showSpinDialog) {
                DailySpinDialog(
                    canSpin = state.canSpinToday,
                    onClaimReward = { coins, gems ->
                        viewModel.claimDailySpin(coins, gems)
                    },
                    onDismiss = { viewModel.closeSpinDialog() }
                )
            }
        }

        AppScreen.PROFILE -> {
            ProfileScreen(
                profile = state.profile,
                strikerSkins = state.strikerSkins,
                boardSkins = state.boardSkins,
                activeStrikerId = state.activeStriker.id,
                activeBoardId = state.activeBoard.id,
                onEquipStriker = { viewModel.equipStriker(it) },
                onBuyStriker = { viewModel.buyStrikerSkin(it) },
                onEquipBoard = { viewModel.equipBoard(it) },
                onBuyBoard = { viewModel.buyBoardSkin(it) },
                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = modifier
            )
        }

        AppScreen.GAME -> {
            GameScreen(
                profile = state.profile,
                gameMode = state.gameMode,
                pieces = state.pieces,
                matchRuleState = state.matchRuleState,
                matchStatus = state.matchStatus,
                strikerBaselineX = state.strikerBaselineX,
                aimAngleRad = state.aimAngleRad,
                shotPower = state.shotPower,
                isAiming = state.isAiming,
                aimPrediction = state.aimPrediction,
                turnSecondsLeft = state.turnSecondsLeft,
                activeStriker = state.activeStriker,
                activeBoard = state.activeBoard,
                showGameOverDialog = state.showGameOverDialog,
                impactEffects = state.impactEffects,
                onStrikerSliderChange = { viewModel.onStrikerSliderChange(it) },
                onAimAndPowerChange = { angle, power -> viewModel.setAimAndPower(angle, power) },
                onReleaseStrike = { viewModel.releaseStrike() },
                onRestartGame = { viewModel.startNewGame(state.gameMode, state.aiDifficulty) },
                onExitGame = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = modifier
            )
        }

        AppScreen.TUTORIAL -> {
            TutorialScreen(
                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = modifier
            )
        }

        AppScreen.PRACTICE -> {
            PracticeScreen(
                scenarios = state.practiceLevels,
                onSelectScenario = { index ->
                    viewModel.startPracticeScenario(index)
                },
                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = modifier
            )
        }

        AppScreen.SUPPORT -> {
            SupportScreen(
                onBack = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = modifier
            )
        }
    }
}
