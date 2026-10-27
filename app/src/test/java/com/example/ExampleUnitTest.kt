package com.example

import com.example.carrom.ai.CarromAIController
import com.example.carrom.model.AIDifficulty
import com.example.carrom.model.GameMode
import com.example.carrom.model.PieceType
import com.example.carrom.physics.CarromPhysics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun standardCarromLayout_hasQueenAndCorrectCoinCounts() {
        val pieces = CarromPhysics.createStandardCarromLayout()
        val queens = pieces.filter { it.type == PieceType.QUEEN }
        val whites = pieces.filter { it.type == PieceType.WHITE }
        val blacks = pieces.filter { it.type == PieceType.BLACK }
        val strikers = pieces.filter { it.type == PieceType.STRIKER }

        assertEquals(1, queens.size)
        assertEquals(9, whites.size)
        assertEquals(9, blacks.size)
        assertEquals(1, strikers.size)
    }

    @Test
    fun aiShotDecision_generatesValidBaselinePositionAndPower() {
        val pieces = CarromPhysics.createStandardCarromLayout()
        val decision = CarromAIController.calculateBestShot(
            pieces = pieces,
            aiAssignedType = PieceType.BLACK,
            mode = GameMode.DISC_POOL,
            difficulty = AIDifficulty.MASTER
        )

        assertTrue("Striker baseline must be within valid range",
            decision.baselineX in CarromPhysics.BASELINE_MIN_X..CarromPhysics.BASELINE_MAX_X)
        assertTrue("Power must be between 0.1 and 1.0",
            decision.powerFraction in 0.1f..1.0f)
    }

    @Test
    fun aimGuide_calculatesTrajectoryPoints() {
        val pieces = CarromPhysics.createStandardCarromLayout()
        val striker = pieces.first { it.type == PieceType.STRIKER }
        val aim = CarromPhysics.calculateAimGuide(
            striker = striker,
            angleRad = -(Math.PI / 2.0).toFloat(),
            pieces = pieces,
            powerFraction = 0.8f
        )

        assertNotNull(aim)
        assertTrue(aim.path.isNotEmpty())
    }
}
