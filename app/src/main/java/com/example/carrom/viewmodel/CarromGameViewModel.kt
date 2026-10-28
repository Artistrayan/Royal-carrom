package com.example.carrom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carrom.ai.CarromAIController
import com.example.carrom.audio.CarromSoundEngine
import com.example.carrom.model.AIDifficulty
import com.example.carrom.model.BoardSkin
import com.example.carrom.model.CarromPiece
import com.example.carrom.model.GameMode
import com.example.carrom.model.MatchRuleState
import com.example.carrom.model.MatchStatus
import com.example.carrom.model.PieceType
import com.example.carrom.model.PlayerProfile
import com.example.carrom.model.SkinsCatalog
import com.example.carrom.model.StrikerSkin
import com.example.carrom.model.TrickShotBank
import com.example.carrom.model.TrickShotScenario
import com.example.carrom.model.TurnPlayer
import com.example.carrom.physics.AimPrediction
import com.example.carrom.physics.CarromPhysics
import com.example.carrom.physics.ImpactEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class AppScreen {
    SPLASH,
    AUTH,
    HOME,
    PROFILE,
    GAME,
    TUTORIAL,
    PRACTICE,
    SUPPORT
}

data class CarromUiState(
    val currentScreen: AppScreen = AppScreen.SPLASH,
    val profile: PlayerProfile = PlayerProfile(),
    val strikerSkins: List<StrikerSkin> = SkinsCatalog.STRIKER_SKINS,
    val boardSkins: List<BoardSkin> = SkinsCatalog.BOARD_SKINS,
    val activeStriker: StrikerSkin = SkinsCatalog.STRIKER_SKINS.first(),
    val activeBoard: BoardSkin = SkinsCatalog.BOARD_SKINS.first(),

    // Game in progress
    val gameMode: GameMode = GameMode.DISC_POOL,
    val aiDifficulty: AIDifficulty = AIDifficulty.MEDIUM,
    val matchStatus: MatchStatus = MatchStatus.AIMING,
    val matchRuleState: MatchRuleState = MatchRuleState(),
    val pieces: List<CarromPiece> = emptyList(),
    val impactEffects: List<ImpactEffect> = emptyList(),
    val animationTick: Long = 0L,

    // Aim & Striker
    val strikerBaselineX: Float = 500f,
    val aimAngleRad: Float = -(PI / 2.0).toFloat(), // Pointing up toward opponent
    val shotPower: Float = 0.75f,
    val isAiming: Boolean = true,
    val aimPrediction: AimPrediction? = null,

    // Turn timer
    val turnSecondsLeft: Int = 15,

    // Trick shot practice
    val practiceLevels: List<TrickShotScenario> = TrickShotBank.getLevels(),
    val currentPracticeIndex: Int = 0,

    // Daily spin
    val canSpinToday: Boolean = true,
    val showSpinDialog: Boolean = false,
    val showGameModeDialog: Boolean = false,
    val showGameOverDialog: Boolean = false,
    val bannerNotice: String = ""
)

class CarromGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CarromUiState())
    val uiState: StateFlow<CarromUiState> = _uiState.asStateFlow()

    private var physicsLoopJob: Job? = null
    private var timerJob: Job? = null
    private var aiThinkingJob: Job? = null

    init {
        // Auto-navigate from splash after animation
        viewModelScope.launch {
            delay(2200)
            if (_uiState.value.currentScreen == AppScreen.SPLASH) {
                _uiState.update { it.copy(currentScreen = AppScreen.HOME) }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun updateProfileName(newName: String, avatarIdx: Int) {
        _uiState.update {
            it.copy(
                profile = it.profile.copy(
                    name = newName.ifBlank { "قهرمان کاروم" },
                    avatarIndex = avatarIdx
                ),
                currentScreen = AppScreen.HOME
            )
        }
    }

    fun openGameModeDialog() {
        _uiState.update { it.copy(showGameModeDialog = true) }
    }

    fun closeGameModeDialog() {
        _uiState.update { it.copy(showGameModeDialog = false) }
    }

    fun openSpinDialog() {
        _uiState.update { it.copy(showSpinDialog = true) }
    }

    fun closeSpinDialog() {
        _uiState.update { it.copy(showSpinDialog = false) }
    }

    fun claimDailySpin(rewardCoins: Int, rewardGems: Int) {
        CarromSoundEngine.playWinFanfare()
        _uiState.update {
            it.copy(
                profile = it.profile.copy(
                    coins = it.profile.coins + rewardCoins,
                    gems = it.profile.gems + rewardGems
                ),
                canSpinToday = false,
                showSpinDialog = false,
                bannerNotice = "تبریک! $rewardCoins سکه و $rewardGems الماس دریافت شد!"
            )
        }
    }

    fun equipStriker(skin: StrikerSkin) {
        _uiState.update {
            it.copy(
                activeStriker = skin,
                profile = it.profile.copy(equippedStrikerId = skin.id)
            )
        }
    }

    fun equipBoard(skin: BoardSkin) {
        _uiState.update {
            it.copy(
                activeBoard = skin,
                profile = it.profile.copy(equippedBoardSkinId = skin.id)
            )
        }
    }

    fun buyStrikerSkin(skin: StrikerSkin) {
        if (_uiState.value.profile.coins >= skin.price) {
            val updatedSkins = _uiState.value.strikerSkins.map {
                if (it.id == skin.id) it.copy(isUnlocked = true) else it
            }
            _uiState.update {
                it.copy(
                    strikerSkins = updatedSkins,
                    profile = it.profile.copy(coins = it.profile.coins - skin.price)
                )
            }
            equipStriker(skin.copy(isUnlocked = true))
            CarromSoundEngine.playWinFanfare()
        }
    }

    fun buyBoardSkin(skin: BoardSkin) {
        if (_uiState.value.profile.coins >= skin.price) {
            val updatedSkins = _uiState.value.boardSkins.map {
                if (it.id == skin.id) it.copy(isUnlocked = true) else it
            }
            _uiState.update {
                it.copy(
                    boardSkins = updatedSkins,
                    profile = it.profile.copy(coins = it.profile.coins - skin.price)
                )
            }
            equipBoard(skin.copy(isUnlocked = true))
            CarromSoundEngine.playWinFanfare()
        }
    }

    // --- GAME INITIALIZATION & LIFECYCLE ---

    fun startNewGame(mode: GameMode, difficulty: AIDifficulty = AIDifficulty.MEDIUM) {
        val initialPieces = CarromPhysics.createStandardCarromLayout()
        val striker = initialPieces.first { it.type == PieceType.STRIKER }

        val initialAngle = -(PI / 2.0).toFloat()
        val prediction = CarromPhysics.calculateAimGuide(striker, initialAngle, initialPieces, 0.75f)

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.GAME,
                gameMode = mode,
                aiDifficulty = difficulty,
                showGameModeDialog = false,
                showGameOverDialog = false,
                pieces = initialPieces,
                strikerBaselineX = 500f,
                aimAngleRad = initialAngle,
                shotPower = 0.75f,
                isAiming = true,
                aimPrediction = prediction,
                turnSecondsLeft = 15,
                matchStatus = MatchStatus.AIMING,
                matchRuleState = MatchRuleState(
                    mode = mode,
                    aiDifficulty = difficulty,
                    currentTurn = TurnPlayer.PLAYER_1,
                    player1Assigned = if (mode == GameMode.DISC_POOL || mode == GameMode.CLASSIC || mode == GameMode.VS_AI) PieceType.WHITE else null,
                    player2Assigned = if (mode == GameMode.DISC_POOL || mode == GameMode.CLASSIC || mode == GameMode.VS_AI) PieceType.BLACK else null,
                    statusMessage = "نوبت شماست! استرایکر را تنظیم و شلیک کنید"
                )
            )
        }

        startTurnTimer()
    }

    fun startPracticeScenario(index: Int) {
        val scenario = _uiState.value.practiceLevels.getOrElse(index) { _uiState.value.practiceLevels.first() }
        val piecesList = scenario.pieces.toMutableList()
        val striker = CarromPiece(
            id = 999,
            type = PieceType.STRIKER,
            x = scenario.strikerStartX,
            y = scenario.strikerStartY,
            radius = 33f,
            mass = 2.6f
        )
        piecesList.add(striker)

        val angle = -(PI / 2.0).toFloat()
        val prediction = CarromPhysics.calculateAimGuide(striker, angle, piecesList, 0.75f)

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.GAME,
                gameMode = GameMode.TRICK_SHOTS,
                currentPracticeIndex = index,
                pieces = piecesList,
                strikerBaselineX = scenario.strikerStartX,
                aimAngleRad = angle,
                shotPower = 0.75f,
                isAiming = true,
                aimPrediction = prediction,
                matchStatus = MatchStatus.AIMING,
                showGameOverDialog = false,
                matchRuleState = MatchRuleState(
                    mode = GameMode.TRICK_SHOTS,
                    currentTurn = TurnPlayer.PLAYER_1,
                    statusMessage = scenario.descriptionFa
                )
            )
        }
    }

    // --- AIM & STRIKER CONTROLS ---

    fun onStrikerSliderChange(newX: Float) {
        val clampedX = newX.coerceIn(CarromPhysics.BASELINE_MIN_X, CarromPhysics.BASELINE_MAX_X)
        val updatedPieces = _uiState.value.pieces.map {
            if (it.type == PieceType.STRIKER) {
                it.deepCopy().apply {
                    x = clampedX
                    prevX = clampedX
                }
            } else it.deepCopy()
        }

        val striker = updatedPieces.first { it.type == PieceType.STRIKER }
        val prediction = CarromPhysics.calculateAimGuide(
            striker,
            _uiState.value.aimAngleRad,
            updatedPieces,
            _uiState.value.shotPower
        )

        _uiState.update {
            it.copy(
                strikerBaselineX = clampedX,
                pieces = updatedPieces,
                aimPrediction = prediction
            )
        }
    }

    fun onDragAimDelta(dx: Float, dy: Float) {
        if (!_uiState.value.isAiming) return

        // Sensitive angle steering
        val angleDelta = (dx * 0.005f)
        val newAngle = _uiState.value.aimAngleRad + angleDelta

        val striker = _uiState.value.pieces.firstOrNull { it.type == PieceType.STRIKER } ?: return
        val prediction = CarromPhysics.calculateAimGuide(
            striker,
            newAngle,
            _uiState.value.pieces,
            _uiState.value.shotPower
        )

        _uiState.update {
            it.copy(
                aimAngleRad = newAngle,
                aimPrediction = prediction
            )
        }
    }

    fun adjustAngleStep(deltaRad: Float) {
        if (!_uiState.value.isAiming) return
        val newAngle = _uiState.value.aimAngleRad + deltaRad
        val striker = _uiState.value.pieces.firstOrNull { it.type == PieceType.STRIKER } ?: return
        val prediction = CarromPhysics.calculateAimGuide(
            striker,
            newAngle,
            _uiState.value.pieces,
            _uiState.value.shotPower
        )
        _uiState.update {
            it.copy(
                aimAngleRad = newAngle,
                aimPrediction = prediction
            )
        }
    }

    fun setShotPower(power: Float) {
        val clamped = power.coerceIn(0.15f, 1.0f)
        val striker = _uiState.value.pieces.firstOrNull { it.type == PieceType.STRIKER } ?: return
        val prediction = CarromPhysics.calculateAimGuide(
            striker,
            _uiState.value.aimAngleRad,
            _uiState.value.pieces,
            clamped
        )
        _uiState.update {
            it.copy(
                shotPower = clamped,
                aimPrediction = prediction
            )
        }
    }

    fun setAimAndPower(angleRad: Float, power: Float) {
        if (!_uiState.value.isAiming) return
        val clampedPower = power.coerceIn(0.15f, 1.0f)
        val striker = _uiState.value.pieces.firstOrNull { it.type == PieceType.STRIKER } ?: return
        val prediction = CarromPhysics.calculateAimGuide(
            striker,
            angleRad,
            _uiState.value.pieces,
            clampedPower
        )
        _uiState.update {
            it.copy(
                aimAngleRad = angleRad,
                shotPower = clampedPower,
                aimPrediction = prediction
            )
        }
    }

    fun releaseStrike() {
        if (!_uiState.value.isAiming) return
        timerJob?.cancel()

        val power = _uiState.value.shotPower
        val angle = _uiState.value.aimAngleRad
        // Realistic tournament power-to-velocity kinetic mapping:
        // - Low power (0.12 - 0.3): Gentle, precise 28 - 45 units/frame for delicate pocket taps and positional play
        // - Mid power (0.4 - 0.7): Crisp, authoritative 60 - 110 units/frame for standard direct shots & cuts
        // - High / Max power (0.8 - 1.0): High-speed explosive 130 - 180 units/frame for powerful breaks & double-bank shots
        val baseSpeed = 20f
        val speed = baseSpeed + (power * 38f) + (power * power * 122f)

        CarromSoundEngine.playStrikerHit(power)

        val updatedPieces = _uiState.value.pieces.map {
            if (it.type == PieceType.STRIKER) {
                it.deepCopy().apply {
                    vx = cos(angle) * speed
                    vy = sin(angle) * speed
                    prevX = x
                    prevY = y
                }
            } else it.deepCopy()
        }

        _uiState.update {
            it.copy(
                pieces = updatedPieces,
                isAiming = false,
                matchStatus = MatchStatus.SIMULATING,
                aimPrediction = null,
                impactEffects = emptyList()
            )
        }

        runPhysicsSimulation()
    }

    // --- PHYSICS RUNNER & TURN RESOLUTION ---

    private fun runPhysicsSimulation() {
        physicsLoopJob?.cancel()
        val pocketedThisShot = mutableListOf<CarromPiece>()
        val activeImpacts = mutableListOf<ImpactEffect>()

        physicsLoopJob = viewModelScope.launch(Dispatchers.Default) {
            var anyStillMoving = true
            var frameCount = 0

            // Work with a mutable list of deep-copied pieces
            val simPieces = _uiState.value.pieces.map { it.deepCopy() }.toMutableList()

            while (anyStillMoving && frameCount < 480) {
                frameCount++
                delay(16) // 60 FPS update interval

                // Update previous positions for motion trails
                for (p in simPieces) {
                    p.prevX = p.x
                    p.prevY = p.y
                }

                // Age active impact shockwaves
                val iterator = activeImpacts.iterator()
                while (iterator.hasNext()) {
                    val impact = iterator.next()
                    impact.age++
                    if (impact.age > 10) {
                        iterator.remove()
                    }
                }

                anyStillMoving = CarromPhysics.updatePhysics(
                    pieces = simPieces,
                    onPocketed = { piece, _ ->
                        pocketedThisShot.add(piece)
                        CarromSoundEngine.playPocketDrop()
                    },
                    onCollisionSound = { intensity ->
                        CarromSoundEngine.playPieceClack(intensity)
                    },
                    onImpactEffect = { x, y, intensity ->
                        if (activeImpacts.size < 6) {
                            activeImpacts.add(ImpactEffect(x, y, intensity, 0))
                        }
                    }
                )

                // CRITICAL: Deep-copy all pieces into a fresh list and update animationTick!
                // This guarantees MutableStateFlow emits every single 16ms frame to Jetpack Compose!
                val snapshotPieces = simPieces.map { it.deepCopy() }
                val snapshotImpacts = activeImpacts.map { it.copy() }

                _uiState.update {
                    it.copy(
                        pieces = snapshotPieces,
                        impactEffects = snapshotImpacts,
                        animationTick = it.animationTick + 1
                    )
                }
            }

            // All pieces have stopped. Resolve turn rules.
            resolveTurn(pocketedThisShot)
        }
    }

    private fun resolveTurn(pocketedPieces: List<CarromPiece>) {
        val currentTurn = _uiState.value.matchRuleState.currentTurn
        val mode = _uiState.value.gameMode
        var foul = false
        var keepsTurn = false
        var statusMsg = ""

        // Check if striker pocketed (Foul)
        val strikerPocketed = pocketedPieces.any { it.type == PieceType.STRIKER }
        if (strikerPocketed) {
            foul = true
            CarromSoundEngine.playFoul()
            statusMsg = "خطا! استرایکر به داخل پاکت رفت (-جریمه)"
        }

        // Filter coins
        val coinsPocketed = pocketedPieces.filter { it.type != PieceType.STRIKER }

        if (!foul && coinsPocketed.isNotEmpty()) {
            when (mode) {
                GameMode.DISC_POOL, GameMode.CLASSIC, GameMode.VS_AI -> {
                    val assigned = if (currentTurn == TurnPlayer.PLAYER_1) {
                        _uiState.value.matchRuleState.player1Assigned ?: PieceType.WHITE
                    } else {
                        _uiState.value.matchRuleState.player2Assigned ?: PieceType.BLACK
                    }

                    val playerCoins = coinsPocketed.filter { it.type == assigned }
                    val queen = coinsPocketed.firstOrNull { it.type == PieceType.QUEEN }

                    if (playerCoins.isNotEmpty() || queen != null) {
                        keepsTurn = true
                        statusMsg = "آفرین! پاکت موفق، دوباره شلیک کنید"
                        if (currentTurn == TurnPlayer.PLAYER_1) {
                            _uiState.value.matchRuleState.player1Score += playerCoins.size * 10
                            _uiState.value.matchRuleState.player1PocketedPieces.addAll(playerCoins.map { it.type })
                        } else {
                            _uiState.value.matchRuleState.player2Score += playerCoins.size * 10
                            _uiState.value.matchRuleState.player2PocketedPieces.addAll(playerCoins.map { it.type })
                        }
                    } else {
                        statusMsg = "مهره حریف پاکت شد! نوبت تغییر کرد"
                    }
                }

                GameMode.FREESTYLE -> {
                    keepsTurn = true
                    var pointsGained = 0
                    for (c in coinsPocketed) {
                        pointsGained += when (c.type) {
                            PieceType.WHITE -> 10
                            PieceType.BLACK -> 5
                            PieceType.QUEEN -> 25
                            else -> 0
                        }
                    }
                    if (currentTurn == TurnPlayer.PLAYER_1) {
                        _uiState.value.matchRuleState.player1Score += pointsGained
                    } else {
                        _uiState.value.matchRuleState.player2Score += pointsGained
                    }
                    statusMsg = "عالی! +$pointsGained امتیاز"
                }

                GameMode.TRICK_SHOTS -> {
                    val remainingTargetPieces = _uiState.value.pieces.filter { !it.isPocketed && it.type != PieceType.STRIKER }
                    if (remainingTargetPieces.isEmpty()) {
                        // Level complete!
                        CarromSoundEngine.playWinFanfare()
                        _uiState.update {
                            it.copy(
                                showGameOverDialog = true,
                                matchRuleState = it.matchRuleState.copy(
                                    statusMessage = "تبریک! چالش با موفقیت انجام شد ⭐⭐⭐",
                                    winner = TurnPlayer.PLAYER_1
                                ),
                                profile = it.profile.copy(coins = it.profile.coins + 500)
                            )
                        }
                        return
                    }
                }

                else -> {}
            }
        }

        // Check Win Condition
        val remainingWhite = _uiState.value.pieces.count { !it.isPocketed && it.type == PieceType.WHITE }
        val remainingBlack = _uiState.value.pieces.count { !it.isPocketed && it.type == PieceType.BLACK }
        val remainingQueen = _uiState.value.pieces.count { !it.isPocketed && it.type == PieceType.QUEEN }

        var winner: TurnPlayer? = null
        if (mode == GameMode.DISC_POOL || mode == GameMode.CLASSIC || mode == GameMode.VS_AI) {
            if (remainingWhite == 0) winner = TurnPlayer.PLAYER_1
            else if (remainingBlack == 0) winner = TurnPlayer.PLAYER_2
        } else if (mode == GameMode.FREESTYLE) {
            if (remainingWhite == 0 && remainingBlack == 0 && remainingQueen == 0) {
                winner = if (_uiState.value.matchRuleState.player1Score >= _uiState.value.matchRuleState.player2Score) {
                    TurnPlayer.PLAYER_1
                } else TurnPlayer.PLAYER_2
            }
        }

        if (winner != null) {
            CarromSoundEngine.playWinFanfare()
            val coinsWon = 1500
            _uiState.update {
                it.copy(
                    showGameOverDialog = true,
                    matchRuleState = it.matchRuleState.copy(
                        winner = winner,
                        statusMessage = if (winner == TurnPlayer.PLAYER_1) "شما برنده شدید! جایزه: $coinsWon سکه" else "حریف برنده شد! دوباره تلاش کنید"
                    ),
                    profile = it.profile.copy(
                        wins = if (winner == TurnPlayer.PLAYER_1) it.profile.wins + 1 else it.profile.wins,
                        totalMatches = it.profile.totalMatches + 1,
                        coins = if (winner == TurnPlayer.PLAYER_1) it.profile.coins + coinsWon else it.profile.coins
                    )
                )
            }
            return
        }

        // Determine Next Turn
        val nextTurn = if (keepsTurn && !foul) currentTurn else {
            if (currentTurn == TurnPlayer.PLAYER_1) TurnPlayer.PLAYER_2 else TurnPlayer.PLAYER_1
        }

        // Reset Striker to Next Player's Baseline
        val nextStrikerY = if (nextTurn == TurnPlayer.PLAYER_1) CarromPhysics.BASELINE_P1_Y else CarromPhysics.BASELINE_P2_Y
        val nextAngle = if (nextTurn == TurnPlayer.PLAYER_1) -(PI / 2.0).toFloat() else (PI / 2.0).toFloat()
        val resetBaselineX = 500f

        val resetPieces = _uiState.value.pieces.map {
            if (it.type == PieceType.STRIKER) {
                it.copy(
                    x = resetBaselineX,
                    y = nextStrikerY,
                    vx = 0f,
                    vy = 0f,
                    isPocketed = false,
                    isSinking = false,
                    pocketScale = 1f,
                    pocketAlpha = 1f,
                    angularVelocity = 0f
                )
            } else it
        }

        val resetStriker = resetPieces.first { it.type == PieceType.STRIKER }
        val nextPrediction = CarromPhysics.calculateAimGuide(resetStriker, nextAngle, resetPieces, 0.75f)

        _uiState.update {
            it.copy(
                pieces = resetPieces,
                strikerBaselineX = resetBaselineX,
                aimAngleRad = nextAngle,
                aimPrediction = nextPrediction,
                isAiming = (nextTurn == TurnPlayer.PLAYER_1 || mode == GameMode.PASS_AND_PLAY),
                matchStatus = MatchStatus.AIMING,
                turnSecondsLeft = 15,
                matchRuleState = it.matchRuleState.copy(
                    currentTurn = nextTurn,
                    statusMessage = if (statusMsg.isNotBlank()) statusMsg else {
                        if (nextTurn == TurnPlayer.PLAYER_1) "نوبت شماست" else "نوبت حریف (هوش مصنوعی)"
                    }
                )
            )
        }

        startTurnTimer()

        // If next turn belongs to AI in VS_AI mode
        if (nextTurn == TurnPlayer.PLAYER_2 && (mode == GameMode.VS_AI || mode == GameMode.DISC_POOL || mode == GameMode.CLASSIC)) {
            triggerAiShot()
        }
    }

    private fun triggerAiShot() {
        aiThinkingJob?.cancel()
        aiThinkingJob = viewModelScope.launch {
            delay(1000) // AI thinking time

            val decision = CarromAIController.calculateBestShot(
                pieces = _uiState.value.pieces,
                aiAssignedType = _uiState.value.matchRuleState.player2Assigned,
                mode = _uiState.value.gameMode,
                difficulty = _uiState.value.aiDifficulty
            )

            // Smoothly move AI striker along baseline
            onStrikerSliderChange(decision.baselineX)
            delay(400)

            // Aim
            val striker = _uiState.value.pieces.first { it.type == PieceType.STRIKER }
            val prediction = CarromPhysics.calculateAimGuide(
                striker,
                decision.aimAngleRad,
                _uiState.value.pieces,
                decision.powerFraction
            )

            _uiState.update {
                it.copy(
                    aimAngleRad = decision.aimAngleRad,
                    shotPower = decision.powerFraction,
                    aimPrediction = prediction,
                    isAiming = true
                )
            }

            delay(600) // AI release delay
            releaseStrike()
        }
    }

    private fun startTurnTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.turnSecondsLeft > 0) {
                delay(1000)
                _uiState.update {
                    it.copy(turnSecondsLeft = it.turnSecondsLeft - 1)
                }
            }
            // Timeout -> pass turn
            if (_uiState.value.isAiming) {
                CarromSoundEngine.playFoul()
                resolveTurn(emptyList())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        physicsLoopJob?.cancel()
        timerJob?.cancel()
        aiThinkingJob?.cancel()
    }
}
