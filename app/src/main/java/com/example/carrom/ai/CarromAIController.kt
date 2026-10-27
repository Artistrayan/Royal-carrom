package com.example.carrom.ai

import com.example.carrom.model.AIDifficulty
import com.example.carrom.model.CarromPiece
import com.example.carrom.model.GameMode
import com.example.carrom.model.PieceType
import com.example.carrom.physics.CarromPhysics
import com.example.carrom.physics.Pocket
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class AIShotDecision(
    val baselineX: Float,
    val aimAngleRad: Float,
    val powerFraction: Float
)

object CarromAIController {

    fun calculateBestShot(
        pieces: List<CarromPiece>,
        aiAssignedType: PieceType?,
        mode: GameMode,
        difficulty: AIDifficulty
    ): AIShotDecision {
        val activeCoins = pieces.filter {
            !it.isPocketed && it.type != PieceType.STRIKER &&
                    (aiAssignedType == null || it.type == aiAssignedType || it.type == PieceType.QUEEN || mode == GameMode.FREESTYLE)
        }

        if (activeCoins.isEmpty()) {
            // Default forward strike
            return AIShotDecision(
                baselineX = 500f,
                aimAngleRad = (Math.PI / 2.0).toFloat(),
                powerFraction = 0.85f
            )
        }

        var bestScore = -9999f
        var bestBaselineX = 500f
        var bestAngle = (Math.PI / 2.0).toFloat()
        var bestPower = 0.8f

        // Sample candidate baseline positions across AI baseline (Y = 185f)
        val candidateXCount = when (difficulty) {
            AIDifficulty.EASY -> 6
            AIDifficulty.MEDIUM -> 12
            AIDifficulty.MASTER -> 24
        }
        val stepX = (CarromPhysics.BASELINE_MAX_X - CarromPhysics.BASELINE_MIN_X) / candidateXCount

        for (coin in activeCoins) {
            for (pocket in CarromPhysics.POCKETS) {
                // Direction from coin to pocket
                val toPocketDx = pocket.x - coin.x
                val toPocketDy = pocket.y - coin.y
                val distCoinToPocket = sqrt(toPocketDx * toPocketDx + toPocketDy * toPocketDy)
                if (distCoinToPocket < 5f) continue

                val pocketAngle = atan2(toPocketDy, toPocketDx)

                // Ghost ball position where striker needs to hit the coin
                // Distance = strikerRadius (33) + coinRadius (24) = 57
                val hitDistance = 57f
                val ghostX = coin.x - cos(pocketAngle) * hitDistance
                val ghostY = coin.y - sin(pocketAngle) * hitDistance

                // For AI top baseline, striker Y is BASELINE_P2_Y = 185
                // Check multiple baseline X positions
                for (i in 0..candidateXCount) {
                    val testStrikerX = CarromPhysics.BASELINE_MIN_X + i * stepX
                    val testStrikerY = CarromPhysics.BASELINE_P2_Y

                    // Vector from striker to ghost position
                    val sDx = ghostX - testStrikerX
                    val sDy = ghostY - testStrikerY
                    val distStrikerToGhost = sqrt(sDx * sDx + sDy * sDy)

                    // Striker must shoot downwards (sDy > 20)
                    if (sDy < 20f) continue

                    val strikeAngle = atan2(sDy, sDx)

                    // Alignment score between striker strike vector and coin-to-pocket vector
                    val dot = cos(strikeAngle) * cos(pocketAngle) + sin(strikeAngle) * sin(pocketAngle)
                    if (dot < 0.2f) continue // Shot is too thin or backward

                    // Calculate score: higher dot product, closer to pocket
                    var score = dot * 1000f - (distCoinToPocket * 0.4f) - (distStrikerToGhost * 0.2f)

                    // Favor Queen if playable
                    if (coin.type == PieceType.QUEEN) {
                        score += 350f
                    }

                    if (score > bestScore) {
                        bestScore = score
                        bestBaselineX = testStrikerX
                        bestAngle = strikeAngle
                        // Power scales with total distance
                        bestPower = ((distStrikerToGhost + distCoinToPocket) / 950f).coerceIn(0.55f, 0.95f)
                    }
                }
            }
        }

        // Apply human-like error according to difficulty
        val maxNoise = when (difficulty) {
            AIDifficulty.EASY -> 0.12f
            AIDifficulty.MEDIUM -> 0.04f
            AIDifficulty.MASTER -> 0.006f
        }
        val noise = (Random.nextFloat() * 2f - 1f) * maxNoise
        val finalAngle = bestAngle + noise

        return AIShotDecision(
            baselineX = bestBaselineX.coerceIn(CarromPhysics.BASELINE_MIN_X, CarromPhysics.BASELINE_MAX_X),
            aimAngleRad = finalAngle,
            powerFraction = bestPower
        )
    }
}
