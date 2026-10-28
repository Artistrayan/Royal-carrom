package com.example.carrom.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import com.example.R
import com.example.carrom.model.BoardSkin
import com.example.carrom.model.CarromPiece
import com.example.carrom.model.PieceType
import com.example.carrom.model.StrikerSkin
import com.example.carrom.physics.AimPrediction
import com.example.carrom.physics.CarromPhysics
import com.example.carrom.physics.ImpactEffect
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun CarromBoardCanvas(
    pieces: List<CarromPiece>,
    aimPrediction: AimPrediction?,
    aimAngleRad: Float,
    aimPower: Float,
    isAiming: Boolean,
    boardSkin: BoardSkin,
    strikerSkin: StrikerSkin,
    impactEffects: List<ImpactEffect> = emptyList(),
    onStrikerPositionChange: (Float) -> Unit,
    onAimAndPowerChange: (Float, Float) -> Unit,
    onReleaseStrike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boardTexture = ImageBitmap.imageResource(id = R.drawable.carrom_board_texture)

    var isPullingSlingshot by remember { mutableStateOf(false) }
    var currentTouchOffset by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(modifier = modifier.aspectRatio(1f)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isAiming) {
                    if (isAiming) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentTouchOffset = offset
                                val minDim = kotlin.math.min(size.width, size.height).toFloat()
                                val scale = minDim / CarromPhysics.BOARD_SIZE
                                val touchX = offset.x / scale
                                val touchY = offset.y / scale

                                val striker = pieces.firstOrNull { it.type == PieceType.STRIKER }
                                if (striker != null) {
                                    val dist = sqrt(((touchX - striker.x) * (touchX - striker.x) + (touchY - striker.y) * (touchY - striker.y)).toDouble()).toFloat()
                                    // If user taps on the striker or baseline, position it
                                    if (dist < 60f || touchY in 740f..910f) {
                                        val clampedX = touchX.coerceIn(CarromPhysics.BASELINE_MIN_X, CarromPhysics.BASELINE_MAX_X)
                                        onStrikerPositionChange(clampedX)
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                currentTouchOffset += dragAmount
                                val minDim = kotlin.math.min(size.width, size.height).toFloat()
                                val scale = minDim / CarromPhysics.BOARD_SIZE
                                val touchX = currentTouchOffset.x / scale
                                val touchY = currentTouchOffset.y / scale

                                val striker = pieces.firstOrNull { it.type == PieceType.STRIKER } ?: return@detectDragGestures

                                val pullDx = touchX - striker.x
                                val pullDy = touchY - striker.y
                                val pullDist = sqrt((pullDx * pullDx + pullDy * pullDy).toDouble()).toFloat()

                                // If pulling downward / backward from striker or already pulling:
                                if (pullDy > 14f || (isPullingSlingshot && pullDist > 16f)) {
                                    isPullingSlingshot = true
                                    // Slingshot aim: shot flies in opposite direction of pull
                                    val angle = atan2(-pullDy.toDouble(), -pullDx.toDouble()).toFloat()
                                    val power = (pullDist / 160f).coerceIn(0.12f, 1.0f)
                                    onAimAndPowerChange(angle, power)
                                } else if (!isPullingSlingshot) {
                                    // User is dragging left/right along the baseline to reposition striker
                                    val clampedX = touchX.coerceIn(CarromPhysics.BASELINE_MIN_X, CarromPhysics.BASELINE_MAX_X)
                                    onStrikerPositionChange(clampedX)
                                }
                            },
                            onDragEnd = {
                                val minDim = kotlin.math.min(size.width, size.height).toFloat()
                                val scale = minDim / CarromPhysics.BOARD_SIZE
                                val striker = pieces.firstOrNull { it.type == PieceType.STRIKER }
                                if (isPullingSlingshot && striker != null) {
                                    val pullDx = (currentTouchOffset.x / scale) - striker.x
                                    val pullDy = (currentTouchOffset.y / scale) - striker.y
                                    val pullDist = sqrt((pullDx * pullDx + pullDy * pullDy).toDouble()).toFloat()
                                    if (pullDist > 20f && aimPower >= 0.12f) {
                                        onReleaseStrike()
                                    }
                                }
                                isPullingSlingshot = false
                            },
                            onDragCancel = {
                                isPullingSlingshot = false
                            }
                        )
                    }
                }
        ) {
            val scale = size.minDimension / CarromPhysics.BOARD_SIZE

            // 1. Draw Master 3D Photorealistic Carrom Board
            drawMasterBoard(boardTexture, scale, boardSkin)

            // 2. Draw 4 Tournament Mesh Net Pockets with 3D Depth
            draw3DPockets(scale)

            // 3. Draw Accurate Tournament Line Markings
            drawTournamentLines(scale, boardSkin)

            // 3.5. Draw Tournament Striker Positioning Rail
            val striker = pieces.firstOrNull { it.type == PieceType.STRIKER }
            drawStrikerRail(scale, striker, isAiming, strikerSkin)

            // 4. Draw Collision Impact Shockwaves
            for (impact in impactEffects) {
                drawImpactShockwave(scale, impact)
            }

            // 5. Draw Aiming Laser Guideline & Ghost Contact Ball
            if (isAiming && aimPrediction != null) {
                drawAimGuide(scale, aimPrediction, strikerSkin, aimPower)
            }

            // 6. Draw Slingshot Tension Band & Power Dial when pulling back
            if (isAiming && isPullingSlingshot && striker != null) {
                drawSlingshotPullBack(scale, striker, currentTouchOffset, aimPower, strikerSkin)
            }

            // 7. Draw Motion Blur Trails for Fast Moving Pieces
            for (p in pieces) {
                if (!p.isPocketed && p.speed() > 2.0f && !p.isSinking) {
                    drawMotionTrail(scale, p)
                }
            }

            // 8. Draw 3D Carrom Pieces & Striker with Specular Curved Lighting
            for (p in pieces) {
                if (!p.isPocketed) {
                    draw3DPiece(scale, p, strikerSkin)
                }
            }
        }
    }
}

private fun DrawScope.drawMasterBoard(
    texture: ImageBitmap,
    scale: Float,
    skin: BoardSkin
) {
    val w = size.width.toInt()
    val h = size.height.toInt()

    drawImage(
        image = texture,
        dstSize = IntSize(w, h)
    )

    if (skin.id != "classic_teak") {
        drawRect(
            color = Color(skin.surfaceColor).copy(alpha = 0.35f),
            topLeft = Offset(CarromPhysics.RAIL_LEFT * scale, CarromPhysics.RAIL_TOP * scale),
            size = Size(
                (CarromPhysics.RAIL_RIGHT - CarromPhysics.RAIL_LEFT) * scale,
                (CarromPhysics.RAIL_BOTTOM - CarromPhysics.RAIL_TOP) * scale
            )
        )
    }

    val railLeft = CarromPhysics.RAIL_LEFT * scale
    val railTop = CarromPhysics.RAIL_TOP * scale
    val railSize = (CarromPhysics.RAIL_RIGHT - CarromPhysics.RAIL_LEFT) * scale
    val shadowDepth = 18f * scale

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent),
            startY = railTop,
            endY = railTop + shadowDepth
        ),
        topLeft = Offset(railLeft, railTop),
        size = Size(railSize, shadowDepth)
    )
}

private fun DrawScope.draw3DPockets(scale: Float) {
    for (pocket in CarromPhysics.POCKETS) {
        val px = pocket.x * scale
        val py = pocket.y * scale
        val r = pocket.radius * scale

        // Polished brass corner ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFD54F), Color(0xFFC79100), Color(0xFF5D4037)),
                center = Offset(px - r * 0.2f, py - r * 0.2f),
                radius = r + 10f * scale
            ),
            radius = r + 8f * scale,
            center = Offset(px, py)
        )

        // Pocket hole dark depth
        drawCircle(
            color = Color(0xFF140E0A),
            radius = r,
            center = Offset(px, py)
        )

        // Cross-hatched rope net
        for (angleDeg in 0..180 step 28) {
            val rad = angleDeg * (PI / 180.0)
            val dx = (cos(rad) * r).toFloat()
            val dy = (sin(rad) * r).toFloat()
            drawLine(
                color = Color(0xFF3E2723).copy(alpha = 0.6f),
                start = Offset(px - dx, py - dy),
                end = Offset(px + dx, py + dy),
                strokeWidth = 2.2f * scale
            )
        }

        // Inner shadow hole depth
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF030303), Color(0xFF1F150E)),
                center = Offset(px, py),
                radius = r * 0.85f
            ),
            radius = r * 0.85f,
            center = Offset(px, py)
        )
    }
}

private fun DrawScope.drawTournamentLines(scale: Float, skin: BoardSkin) {
    val lineCol = Color(skin.linesColor).copy(alpha = 0.85f)
    val redCol = Color(0xFFD32F2F)
    val strokeWidth = 2.2f * scale

    val cx = 500f * scale
    val cy = 500f * scale
    val centerR = 80f * scale
    val innerR = 25f * scale

    drawCircle(
        color = lineCol,
        radius = centerR,
        center = Offset(cx, cy),
        style = Stroke(width = strokeWidth)
    )
    drawCircle(
        color = redCol,
        radius = innerR,
        center = Offset(cx, cy),
        style = Stroke(width = strokeWidth)
    )
    drawCircle(
        color = redCol,
        radius = 5.5f * scale,
        center = Offset(cx, cy),
        style = Fill
    )

    val minX = CarromPhysics.BASELINE_MIN_X * scale
    val maxX = CarromPhysics.BASELINE_MAX_X * scale
    val endCircleRadius = 18f * scale

    // P1 Baseline
    val p1Y = CarromPhysics.BASELINE_P1_Y * scale
    val p1Y2 = (CarromPhysics.BASELINE_P1_Y + 28f) * scale
    drawLine(color = lineCol, start = Offset(minX, p1Y), end = Offset(maxX, p1Y), strokeWidth = strokeWidth)
    drawLine(color = lineCol, start = Offset(minX, p1Y2), end = Offset(maxX, p1Y2), strokeWidth = strokeWidth)

    for (x in listOf(minX, maxX)) {
        val center = Offset(x, (p1Y + p1Y2) / 2f)
        drawCircle(color = lineCol, radius = endCircleRadius, center = center, style = Stroke(width = strokeWidth))
        drawCircle(color = redCol, radius = endCircleRadius * 0.72f, center = center, style = Fill)
    }

    // P2 Baseline
    val p2Y = CarromPhysics.BASELINE_P2_Y * scale
    val p2Y2 = (CarromPhysics.BASELINE_P2_Y - 28f) * scale
    drawLine(color = lineCol, start = Offset(minX, p2Y), end = Offset(maxX, p2Y), strokeWidth = strokeWidth)
    drawLine(color = lineCol, start = Offset(minX, p2Y2), end = Offset(maxX, p2Y2), strokeWidth = strokeWidth)

    for (x in listOf(minX, maxX)) {
        val center = Offset(x, (p2Y + p2Y2) / 2f)
        drawCircle(color = lineCol, radius = endCircleRadius, center = center, style = Stroke(width = strokeWidth))
        drawCircle(color = redCol, radius = endCircleRadius * 0.72f, center = center, style = Fill)
    }
}

private fun DrawScope.drawSlingshotPullBack(
    scale: Float,
    striker: CarromPiece,
    touchOffset: Offset,
    aimPower: Float,
    strikerSkin: StrikerSkin
) {
    val sx = striker.x * scale
    val sy = striker.y * scale
    val tx = touchOffset.x
    val ty = touchOffset.y

    val powerColor = when {
        aimPower < 0.45f -> Color(0xFF00E5FF)
        aimPower < 0.75f -> Color(0xFFFFD54F)
        else -> Color(0xFFFF1744)
    }

    // 1. Elastic Slingshot Band
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(Color(strikerSkin.primaryColor), powerColor),
            start = Offset(sx, sy),
            end = Offset(tx, ty)
        ),
        start = Offset(sx, sy),
        end = Offset(tx, ty),
        strokeWidth = (5f * (1f - (aimPower * 0.35f))) * scale,
        cap = StrokeCap.Round
    )

    // 2. Touch Pull Handle
    drawCircle(
        color = powerColor.copy(alpha = 0.5f),
        radius = 16f * scale,
        center = Offset(tx, ty)
    )
    drawCircle(
        color = Color.White,
        radius = 7f * scale,
        center = Offset(tx, ty)
    )

    // 3. Dynamic Power Arc wrapping around the striker
    val sweepAngle = aimPower * 360f
    val r = (striker.radius + 7f) * scale
    drawArc(
        color = powerColor,
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(sx - r, sy - r),
        size = Size(r * 2f, r * 2f),
        style = Stroke(width = 4.5f * scale, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawImpactShockwave(scale: Float, impact: ImpactEffect) {
    val progress = impact.age / 10f
    val radius = (20f + progress * 60f) * scale
    val alpha = (1f - progress).coerceIn(0f, 1f) * 0.8f

    drawCircle(
        color = Color(0xFFFFD54F).copy(alpha = alpha),
        radius = radius,
        center = Offset(impact.x * scale, impact.y * scale),
        style = Stroke(width = (4f * (1f - progress) + 1f) * scale)
    )
}

private fun DrawScope.drawMotionTrail(scale: Float, piece: CarromPiece) {
    val start = Offset(piece.prevX * scale, piece.prevY * scale)
    val end = Offset(piece.x * scale, piece.y * scale)
    val r = piece.radius * scale

    val color = when (piece.type) {
        PieceType.WHITE -> Color(0xFFFAF6EE).copy(alpha = 0.35f)
        PieceType.BLACK -> Color(0xFF424242).copy(alpha = 0.35f)
        PieceType.QUEEN -> Color(0xFFFF4081).copy(alpha = 0.45f)
        PieceType.STRIKER -> Color(0xFFFFD700).copy(alpha = 0.5f)
    }

    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = r * 1.5f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawAimGuide(
    scale: Float,
    aim: AimPrediction,
    strikerSkin: StrikerSkin,
    power: Float
) {
    val primaryColor = Color(strikerSkin.primaryColor)
    val cyanGlow = Color(0xFF00E5FF)

    for (i in 0 until aim.path.size - 1) {
        val p1 = aim.path[i]
        val p2 = aim.path[i + 1]

        drawLine(
            color = cyanGlow.copy(alpha = 0.35f),
            start = Offset(p1.x * scale, p1.y * scale),
            end = Offset(p2.x * scale, p2.y * scale),
            strokeWidth = (7f + power * 4f) * scale,
            cap = StrokeCap.Round
        )
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(primaryColor, Color.White),
                start = Offset(p1.x * scale, p1.y * scale),
                end = Offset(p2.x * scale, p2.y * scale)
            ),
            start = Offset(p1.x * scale, p1.y * scale),
            end = Offset(p2.x * scale, p2.y * scale),
            strokeWidth = (3.5f + power * 2.5f) * scale,
            cap = StrokeCap.Round
        )
    }

    if (aim.hitPiece != null) {
        val gx = aim.ghostStrikerX * scale
        val gy = aim.ghostStrikerY * scale
        val sr = 33f * scale

        drawCircle(
            color = Color.White.copy(alpha = 0.35f),
            radius = sr,
            center = Offset(gx, gy),
            style = Stroke(width = 2.5f * scale)
        )
    }

    if (aim.hitPieceTargetTrajectory != null && aim.hitPieceTargetTrajectory.size >= 2) {
        val tp1 = aim.hitPieceTargetTrajectory[0]
        val tp2 = aim.hitPieceTargetTrajectory[1]
        val startPt = Offset(tp1.x * scale, tp1.y * scale)
        val endPt = Offset(tp2.x * scale, tp2.y * scale)

        drawLine(
            color = Color(0xFFFFEB3B),
            start = startPt,
            end = endPt,
            strokeWidth = 4.5f * scale,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = Color(0xFFFFEB3B),
            radius = 6f * scale,
            center = endPt
        )
        drawCircle(
            color = Color(0xFFFFEB3B).copy(alpha = 0.5f),
            radius = (aim.hitPiece?.radius ?: 24f) * 1.35f * scale,
            center = startPt,
            style = Stroke(width = 2.5f * scale)
        )
    }
}

private fun DrawScope.draw3DPiece(scale: Float, piece: CarromPiece, strikerSkin: StrikerSkin) {
    val px = piece.x * scale
    val py = piece.y * scale
    val r = piece.radius * scale * piece.pocketScale
    val alpha = piece.pocketAlpha.coerceIn(0f, 1f)

    if (r <= 0.5f || alpha <= 0.01f) return

    // 3D Drop Shadow with realistic contact occlusion
    drawCircle(
        color = Color.Black.copy(alpha = 0.42f * alpha),
        radius = r * 1.02f,
        center = Offset(px + 4f * scale, py + 6f * scale)
    )

    when (piece.type) {
        PieceType.WHITE -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFFAF6EE),
                        Color(0xFFE0D7C6),
                        Color(0xFFBCAAA4)
                    ),
                    center = Offset(px - r * 0.35f, py - r * 0.35f),
                    radius = r * 1.1f
                ),
                radius = r,
                center = Offset(px, py),
                alpha = alpha
            )
            drawCircle(
                color = Color(0xFFB0A090).copy(alpha = alpha),
                radius = r * 0.68f,
                center = Offset(px, py),
                style = Stroke(width = 1.8f * scale)
            )
            drawCircle(
                color = Color(0xFFB0A090).copy(alpha = alpha),
                radius = r * 0.32f,
                center = Offset(px, py),
                style = Stroke(width = 1.5f * scale)
            )
            // Rotating carved notches on coin surface for natural spin motion
            rotate(degrees = piece.rotation, pivot = Offset(px, py)) {
                for (a in listOf(0f, 90f, 180f, 270f)) {
                    val rad = (a * PI / 180.0).toFloat()
                    val dotDist = r * 0.50f
                    drawCircle(
                        color = Color(0xFF8D6E63).copy(alpha = 0.65f * alpha),
                        radius = 2.2f * scale,
                        center = Offset(px + cos(rad) * dotDist, py + sin(rad) * dotDist)
                    )
                }
            }
            // Specular sheen
            drawCircle(
                color = Color.White.copy(alpha = 0.7f * alpha),
                radius = r * 0.22f,
                center = Offset(px - r * 0.32f, py - r * 0.32f)
            )
        }

        PieceType.BLACK -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF5A5A5A),
                        Color(0xFF262626),
                        Color(0xFF141414),
                        Color(0xFF0A0A0A)
                    ),
                    center = Offset(px - r * 0.35f, py - r * 0.35f),
                    radius = r * 1.1f
                ),
                radius = r,
                center = Offset(px, py),
                alpha = alpha
            )
            drawCircle(
                color = Color(0xFF757575).copy(alpha = alpha),
                radius = r * 0.68f,
                center = Offset(px, py),
                style = Stroke(width = 1.8f * scale)
            )
            drawCircle(
                color = Color(0xFF757575).copy(alpha = alpha),
                radius = r * 0.32f,
                center = Offset(px, py),
                style = Stroke(width = 1.5f * scale)
            )
            // Rotating carved notches on coin surface
            rotate(degrees = piece.rotation, pivot = Offset(px, py)) {
                for (a in listOf(0f, 90f, 180f, 270f)) {
                    val rad = (a * PI / 180.0).toFloat()
                    val dotDist = r * 0.50f
                    drawCircle(
                        color = Color(0xFFAAAAAA).copy(alpha = 0.55f * alpha),
                        radius = 2.2f * scale,
                        center = Offset(px + cos(rad) * dotDist, py + sin(rad) * dotDist)
                    )
                }
            }
            drawCircle(
                color = Color(0xFFBDBDBD).copy(alpha = 0.6f * alpha),
                radius = r * 0.20f,
                center = Offset(px - r * 0.32f, py - r * 0.32f)
            )
        }

        PieceType.QUEEN -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF5252),
                        Color(0xFFE91E63),
                        Color(0xFFC2185B),
                        Color(0xFF880E4F)
                    ),
                    center = Offset(px - r * 0.35f, py - r * 0.35f),
                    radius = r * 1.1f
                ),
                radius = r,
                center = Offset(px, py),
                alpha = alpha
            )
            drawCircle(
                color = Color(0xFFFFD54F).copy(alpha = alpha),
                radius = r * 0.68f,
                center = Offset(px, py),
                style = Stroke(width = 2.2f * scale)
            )
            // Rotating Royal Crown star rays
            rotate(degrees = piece.rotation, pivot = Offset(px, py)) {
                for (a in listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f)) {
                    val rad = (a * PI / 180.0).toFloat()
                    val p1Dist = r * 0.38f
                    val p2Dist = r * 0.62f
                    drawLine(
                        color = Color(0xFFFFD54F).copy(alpha = 0.85f * alpha),
                        start = Offset(px + cos(rad) * p1Dist, py + sin(rad) * p1Dist),
                        end = Offset(px + cos(rad) * p2Dist, py + sin(rad) * p2Dist),
                        strokeWidth = 1.6f * scale
                    )
                }
            }
            drawCircle(
                color = Color(0xFFFFD54F).copy(alpha = alpha),
                radius = r * 0.28f,
                center = Offset(px, py),
                style = Fill
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.8f * alpha),
                radius = r * 0.20f,
                center = Offset(px - r * 0.32f, py - r * 0.32f)
            )
        }

        PieceType.STRIKER -> {
            val primary = Color(strikerSkin.primaryColor)
            val accent = Color(strikerSkin.accentColor)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primary, accent, Color(0xFF2C1608)),
                    center = Offset(px - r * 0.25f, py - r * 0.25f),
                    radius = r * 1.1f
                ),
                radius = r,
                center = Offset(px, py),
                alpha = alpha
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.85f * alpha),
                radius = r * 0.74f,
                center = Offset(px, py),
                style = Stroke(width = 2.6f * scale)
            )
            // Rotating Striker Star pattern
            rotate(degrees = piece.rotation, pivot = Offset(px, py)) {
                for (a in listOf(0f, 60f, 120f, 180f, 240f, 300f)) {
                    val rad = (a * PI / 180.0).toFloat()
                    val dotDist = r * 0.54f
                    drawCircle(
                        color = Color.White.copy(alpha = 0.9f * alpha),
                        radius = 3.2f * scale,
                        center = Offset(px + cos(rad) * dotDist, py + sin(rad) * dotDist)
                    )
                }
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, accent, primary),
                    center = Offset(px - r * 0.12f, py - r * 0.12f),
                    radius = r * 0.38f
                ),
                radius = r * 0.38f,
                center = Offset(px, py),
                alpha = alpha
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.9f * alpha),
                radius = r * 0.18f,
                center = Offset(px - r * 0.30f, py - r * 0.30f)
            )
        }
    }
}

private fun DrawScope.drawStrikerRail(
    scale: Float,
    striker: CarromPiece?,
    isAiming: Boolean,
    strikerSkin: StrikerSkin
) {
    val railY = CarromPhysics.BASELINE_P1_Y * scale
    val minX = CarromPhysics.BASELINE_MIN_X * scale
    val maxX = CarromPhysics.BASELINE_MAX_X * scale
    val railHeight = 12f * scale

    // 1. Sleek metallic recessed rail track beneath baseline
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xBB150F0A),
                Color(0xEE332317),
                Color(0xBB150F0A)
            ),
            startY = railY - railHeight / 2,
            endY = railY + railHeight / 2
        ),
        topLeft = Offset(minX - 10f * scale, railY - railHeight / 2),
        size = Size(maxX - minX + 20f * scale, railHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * scale, 6f * scale)
    )

    // 2. Metallic golden rail border
    drawRoundRect(
        color = if (isAiming) Color(0xFFFFD54F).copy(alpha = 0.75f) else Color(0xFF8D6E63).copy(alpha = 0.5f),
        topLeft = Offset(minX - 10f * scale, railY - railHeight / 2),
        size = Size(maxX - minX + 20f * scale, railHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f * scale, 6f * scale),
        style = Stroke(width = 1.8f * scale)
    )

    // 3. Precision tick marks (20%, 35%, 50%, 65%, 80%) along the rail
    for (fraction in listOf(0.2f, 0.35f, 0.5f, 0.65f, 0.8f)) {
        val tx = minX + (maxX - minX) * fraction
        val tickHeight = if (fraction == 0.5f) 15f * scale else 9f * scale
        val tickColor = if (fraction == 0.5f) Color(0xFFFFD54F) else Color(0xFFD7CCC8).copy(alpha = 0.6f)
        drawLine(
            color = tickColor,
            start = Offset(tx, railY - tickHeight / 2),
            end = Offset(tx, railY + tickHeight / 2),
            strokeWidth = (if (fraction == 0.5f) 2.2f else 1.4f) * scale,
            cap = StrokeCap.Round
        )
    }

    // 4. Rail end stops (clean metallic rivets, no arrows)
    drawCircle(
        color = if (isAiming) Color(0xFFFFD54F) else Color(0xFF8D6E63),
        radius = 3.5f * scale,
        center = Offset(minX - 5f * scale, railY)
    )
    drawCircle(
        color = if (isAiming) Color(0xFFFFD54F) else Color(0xFF8D6E63),
        radius = 3.5f * scale,
        center = Offset(maxX + 5f * scale, railY)
    )

    // 5. Luminous slider carriage under the striker
    if (isAiming && striker != null) {
        val sx = striker.x * scale
        // Glowing cradle on the rail
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(strikerSkin.accentColor).copy(alpha = 0.7f),
                    Color.Transparent
                ),
                center = Offset(sx, railY),
                radius = 30f * scale
            ),
            radius = 30f * scale,
            center = Offset(sx, railY)
        )
        // Metallic slider cradle pad
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFE082), Color(strikerSkin.primaryColor), Color(0xFFFFB300)),
                start = Offset(sx - 15f * scale, railY - 6f * scale),
                end = Offset(sx + 15f * scale, railY + 6f * scale)
            ),
            topLeft = Offset(sx - 15f * scale, railY - 6f * scale),
            size = Size(30f * scale, 12f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f * scale, 4f * scale)
        )
        // Indicator center notch
        drawLine(
            color = Color.White,
            start = Offset(sx, railY - 5f * scale),
            end = Offset(sx, railY + 5f * scale),
            strokeWidth = 2f * scale,
            cap = StrokeCap.Round
        )
    }
}
