package com.example.carrom.physics

import com.example.carrom.audio.CarromSoundEngine
import com.example.carrom.model.CarromPiece
import com.example.carrom.model.PieceType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Pocket(val x: Float, val y: Float, val radius: Float = 50f)

data class TrajectoryPoint(
    val x: Float,
    val y: Float
)

data class AimPrediction(
    val path: List<TrajectoryPoint>,
    val hitPiece: CarromPiece? = null,
    val ghostStrikerX: Float = 0f,
    val ghostStrikerY: Float = 0f,
    val hitPieceTargetTrajectory: List<TrajectoryPoint>? = null
)

data class ImpactEffect(
    val x: Float,
    val y: Float,
    val intensity: Float,
    var age: Int = 0 // frames
)

object CarromPhysics {
    const val BOARD_SIZE = 1000f
    const val RAIL_LEFT = 95f
    const val RAIL_RIGHT = 905f
    const val RAIL_TOP = 95f
    const val RAIL_BOTTOM = 905f

    const val BASELINE_P1_Y = 815f
    const val BASELINE_P2_Y = 185f
    const val BASELINE_MIN_X = 220f
    const val BASELINE_MAX_X = 780f

    val POCKETS = listOf(
        Pocket(95f, 95f),
        Pocket(905f, 95f),
        Pocket(95f, 905f),
        Pocket(905f, 905f)
    )

    // Calibrated tournament physics: snappy wooden impacts, natural boric powder glide, authentic velocity transfer
    private const val RESTITUTION_COIN = 0.78f // Crisp, snappy disc-to-disc elasticity
    private const val RESTITUTION_RAIL = 0.68f // Solid timber cushion bounce
    private const val FRICTION_MULT = 0.978f    // Smooth boric powder glide
    private const val KINETIC_DECEL = 0.18f     // Linear Coulomb sliding friction
    private const val STOP_THRESHOLD = 0.22f    // Smooth zero-velocity resting threshold
    private const val ANGULAR_DECAY = 0.94f    // Rotational spin decay on wooden surface
    private const val TANGENTIAL_FRICTION = 0.075f // Coulomb friction between colliding disc rims

    /**
     * Executes one frame of simulation, split into sub-steps for smooth 60fps dynamics
     */
    fun updatePhysics(
        pieces: List<CarromPiece>,
        onPocketed: (CarromPiece, Pocket) -> Unit,
        onCollisionSound: (Float) -> Unit,
        onImpactEffect: (Float, Float, Float) -> Unit = { _, _, _ -> },
        subSteps: Int = 8
    ): Boolean {
        var anyMoved = false
        val activePieces = pieces.filter { !it.isPocketed }

        // Update sinking pieces into pockets
        for (p in activePieces) {
            if (p.isSinking) {
                anyMoved = true
                p.pocketScale -= 0.075f
                p.pocketAlpha -= 0.075f
                p.rotation += p.angularVelocity * 0.1f * 57.2958f
                if (p.pocketScale <= 0.05f) {
                    p.isPocketed = true
                    p.isSinking = false
                    p.pocketScale = 0f
                    p.pocketAlpha = 0f
                }
            }
        }

        val movingOrCollidable = activePieces.filter { !it.isSinking }

        repeat(subSteps) {
            val dtFactor = 1.0f / subSteps
            val stepFriction = Math.pow(FRICTION_MULT.toDouble(), dtFactor.toDouble()).toFloat()
            val stepAngularDecay = Math.pow(ANGULAR_DECAY.toDouble(), dtFactor.toDouble()).toFloat()
            val stepDecel = KINETIC_DECEL * dtFactor

            // 1. Move positions with real carrom powder kinetic + viscous deceleration and spin rotation
            for (p in movingOrCollidable) {
                val spd = p.speed()
                val angSpd = kotlin.math.abs(p.angularVelocity)

                if (spd > 0.005f || angSpd > 0.01f) {
                    anyMoved = true
                    p.prevX = p.x
                    p.prevY = p.y
                    p.x += p.vx * dtFactor
                    p.y += p.vy * dtFactor

                    // Natural spin integration
                    p.rotation += p.angularVelocity * dtFactor * 57.2958f
                    p.angularVelocity *= stepAngularDecay
                    if (kotlin.math.abs(p.angularVelocity) < 0.01f) {
                        p.angularVelocity = 0f
                    }

                    // Viscous drag + constant Coulomb surface friction
                    if (spd > 0.005f) {
                        val newSpd = (spd * stepFriction - stepDecel).coerceAtLeast(0f)
                        if (newSpd < STOP_THRESHOLD) {
                            p.vx = 0f
                            p.vy = 0f
                        } else {
                            val factor = newSpd / spd
                            p.vx *= factor
                            p.vy *= factor
                        }
                    }
                }
            }

            // 2. Pocket Suction & Gravity Well (Hyper-smooth drop into pocket)
            for (p in movingOrCollidable) {
                for (pocket in POCKETS) {
                    val dx = pocket.x - p.x
                    val dy = pocket.y - p.y
                    val dist = sqrt(dx * dx + dy * dy)
                    val suctionZone = pocket.radius * 1.45f

                    if (dist < suctionZone) {
                        anyMoved = true
                        val pullForce = 0.55f * dtFactor
                        p.vx += dx * pullForce
                        p.vy += dy * pullForce
                        // Add vortex spin when sucked towards pocket
                        p.angularVelocity += (dx * p.vy - dy * p.vx) * 0.0008f

                        // Fall into pocket threshold
                        if (dist < pocket.radius * 0.74f && !p.isSinking && !p.isPocketed) {
                            p.isSinking = true
                            p.vx *= 0.12f
                            p.vy *= 0.12f
                            onPocketed(p, pocket)
                            break
                        }
                    }
                }
            }

            // 3. Rail & Pocket Jaw Collisions with Wood Restitution & Tangential Friction
            for (p in movingOrCollidable) {
                if (p.isSinking) continue
                var bouncedRail = false

                // Left Rail
                if (p.x - p.radius < RAIL_LEFT && (p.y > 135f && p.y < 865f)) {
                    p.x = RAIL_LEFT + p.radius
                    p.vx = -p.vx * RESTITUTION_RAIL
                    p.vy *= 0.90f // Cushion friction
                    p.angularVelocity += p.vy * 0.01f // Induced spin
                    bouncedRail = true
                }
                // Right Rail
                if (p.x + p.radius > RAIL_RIGHT && (p.y > 135f && p.y < 865f)) {
                    p.x = RAIL_RIGHT - p.radius
                    p.vx = -p.vx * RESTITUTION_RAIL
                    p.vy *= 0.90f
                    p.angularVelocity -= p.vy * 0.01f
                    bouncedRail = true
                }
                // Top Rail
                if (p.y - p.radius < RAIL_TOP && (p.x > 135f && p.x < 865f)) {
                    p.y = RAIL_TOP + p.radius
                    p.vy = -p.vy * RESTITUTION_RAIL
                    p.vx *= 0.90f
                    p.angularVelocity -= p.vx * 0.01f
                    bouncedRail = true
                }
                // Bottom Rail
                if (p.y + p.radius > RAIL_BOTTOM && (p.x > 135f && p.x < 865f)) {
                    p.y = RAIL_BOTTOM - p.radius
                    p.vy = -p.vy * RESTITUTION_RAIL
                    p.vx *= 0.90f
                    p.angularVelocity += p.vx * 0.01f
                    bouncedRail = true
                }

                if (bouncedRail) {
                    CarromSoundEngine.playRailBounce()
                }
            }

            // 4. Circle - Circle Elastic Collisions with Realistic Momentum & Spin Transfer
            for (i in 0 until movingOrCollidable.size) {
                val p1 = movingOrCollidable[i]
                if (p1.isSinking) continue

                for (j in i + 1 until movingOrCollidable.size) {
                    val p2 = movingOrCollidable[j]
                    if (p2.isSinking) continue

                    val dx = p2.x - p1.x
                    val dy = p2.y - p1.y
                    val distSq = dx * dx + dy * dy
                    val minDist = p1.radius + p2.radius

                    if (distSq < minDist * minDist && distSq > 0.0001f) {
                        val dist = sqrt(distSq)
                        val nx = dx / dist
                        val ny = dy / dist
                        // Tangent vector
                        val tx = -ny
                        val ty = nx

                        // Positional separation based on inverse mass to prevent sticking
                        val overlap = minDist - dist
                        val totalMass = p1.mass + p2.mass
                        val p1Ratio = p2.mass / totalMass
                        val p2Ratio = p1.mass / totalMass

                        p1.x -= nx * overlap * p1Ratio
                        p1.y -= ny * overlap * p1Ratio
                        p2.x += nx * overlap * p2Ratio
                        p2.y += ny * overlap * p2Ratio

                        // Relative velocity along collision normal
                        val rvx = p2.vx - p1.vx
                        val rvy = p2.vy - p1.vy
                        val velAlongNormal = rvx * nx + rvy * ny

                        if (velAlongNormal < 0) {
                            // 1. Normal restitution impulse
                            val impulseNormal = -(1 + RESTITUTION_COIN) * velAlongNormal / (1f / p1.mass + 1f / p2.mass)

                            // 2. Tangential friction & spin exchange
                            val surfaceRelVelTangent = (rvx * tx + rvy * ty) - (p1.angularVelocity * p1.radius + p2.angularVelocity * p2.radius)
                            val maxFrictionImpulse = TANGENTIAL_FRICTION * impulseNormal
                            // Effective tangential inertia (3/m for uniform circular disc where I = 0.5 * m * r^2)
                            val effMassTangent = 1f / (3f / p1.mass + 3f / p2.mass)
                            val impulseTangent = (-surfaceRelVelTangent * effMassTangent).coerceIn(-maxFrictionImpulse, maxFrictionImpulse)

                            // Apply impulses to linear velocities
                            val totalImpulseX = impulseNormal * nx + impulseTangent * tx
                            val totalImpulseY = impulseNormal * ny + impulseTangent * ty

                            p1.vx -= totalImpulseX / p1.mass
                            p1.vy -= totalImpulseY / p1.mass
                            p2.vx += totalImpulseX / p2.mass
                            p2.vy += totalImpulseY / p2.mass

                            // Apply torque to angular velocities
                            p1.angularVelocity -= (2f * impulseTangent) / (p1.mass * p1.radius)
                            p2.angularVelocity -= (2f * impulseTangent) / (p2.mass * p2.radius)

                            val contactX = (p1.x + p2.x) / 2f
                            val contactY = (p1.y + p2.y) / 2f
                            val intensity = (kotlin.math.abs(impulseNormal) / 18f).coerceIn(0f, 1f)

                            onCollisionSound(intensity)
                            onImpactEffect(contactX, contactY, intensity)
                        }
                    }
                }
            }
        }

        return anyMoved
    }

    /**
     * Calculates aim guideline with contact ghost-ball and deflection ray
     */
    fun calculateAimGuide(
        striker: CarromPiece,
        angleRad: Float,
        pieces: List<CarromPiece>,
        powerFraction: Float = 0.8f
    ): AimPrediction {
        val points = mutableListOf(TrajectoryPoint(striker.x, striker.y))
        var currentX = striker.x
        var currentY = striker.y
        var dirX = cos(angleRad)
        var dirY = sin(angleRad)

        var hitPiece: CarromPiece? = null
        var ghostX = 0f
        var ghostY = 0f
        var hitTargetTrajectory: MutableList<TrajectoryPoint>? = null

        val maxDist = 760f * powerFraction
        val step = 8f
        var traveled = 0f

        while (traveled < maxDist) {
            currentX += dirX * step
            currentY += dirY * step
            traveled += step

            // Check coin collision
            for (p in pieces) {
                if (p.id == striker.id || p.isPocketed) continue
                val dx = p.x - currentX
                val dy = p.y - currentY
                val dist = sqrt(dx * dx + dy * dy)
                if (dist <= striker.radius + p.radius) {
                    ghostX = currentX
                    ghostY = currentY
                    points.add(TrajectoryPoint(currentX, currentY))
                    hitPiece = p

                    // Calculate deflection vector for target coin
                    val impactAngle = atan2(dy, dx)
                    hitTargetTrajectory = mutableListOf(
                        TrajectoryPoint(p.x, p.y),
                        TrajectoryPoint(
                            p.x + cos(impactAngle) * 170f,
                            p.y + sin(impactAngle) * 170f
                        )
                    )
                    return AimPrediction(points, hitPiece, ghostX, ghostY, hitTargetTrajectory)
                }
            }

            // Check rail reflection
            var bounced = false
            if (currentX < RAIL_LEFT + striker.radius) {
                currentX = RAIL_LEFT + striker.radius
                dirX = -dirX
                bounced = true
            } else if (currentX > RAIL_RIGHT - striker.radius) {
                currentX = RAIL_RIGHT - striker.radius
                dirX = -dirX
                bounced = true
            }

            if (currentY < RAIL_TOP + striker.radius) {
                currentY = RAIL_TOP + striker.radius
                dirY = -dirY
                bounced = true
            } else if (currentY > RAIL_BOTTOM - striker.radius) {
                currentY = RAIL_BOTTOM - striker.radius
                dirY = -dirY
                bounced = true
            }

            if (bounced) {
                points.add(TrajectoryPoint(currentX, currentY))
                if (points.size >= 4) break
            }
        }

        points.add(TrajectoryPoint(currentX, currentY))
        return AimPrediction(points, null, currentX, currentY, null)
    }

    /**
     * Initializes pieces for standard Carrom game
     */
    fun createStandardCarromLayout(): List<CarromPiece> {
        val list = mutableListOf<CarromPiece>()
        var idCounter = 1
        val centerX = 500f
        val centerY = 500f
        val r = 24f

        // Center Queen (Red)
        list.add(CarromPiece(idCounter++, PieceType.QUEEN, centerX, centerY, radius = r, mass = 1.0f))

        // Inner circle (6 pieces, radius = 2 * r)
        val innerRadius = r * 2.02f
        for (i in 0 until 6) {
            val angle = (i * 60.0) * (PI / 180.0)
            val type = if (i % 2 == 0) PieceType.WHITE else PieceType.BLACK
            list.add(
                CarromPiece(
                    id = idCounter++,
                    type = type,
                    x = centerX + (cos(angle) * innerRadius).toFloat(),
                    y = centerY + (sin(angle) * innerRadius).toFloat(),
                    radius = r,
                    mass = 1.0f
                )
            )
        }

        // Outer circle (12 pieces, radius ~ 3.85 * r)
        val outerRadius = r * 3.85f
        for (i in 0 until 12) {
            val angle = (i * 30.0 + 15.0) * (PI / 180.0)
            val type = if (i % 2 == 0) PieceType.BLACK else PieceType.WHITE
            list.add(
                CarromPiece(
                    id = idCounter++,
                    type = type,
                    x = centerX + (cos(angle) * outerRadius).toFloat(),
                    y = centerY + (sin(angle) * outerRadius).toFloat(),
                    radius = r,
                    mass = 1.0f
                )
            )
        }

        // Striker with calibrated tournament mass ratio ~2.6x of coin mass (official: 15g striker vs 5.5g coins)
        list.add(
            CarromPiece(
                id = 999,
                type = PieceType.STRIKER,
                x = 500f,
                y = BASELINE_P1_Y,
                radius = 33f,
                mass = 2.6f
            )
        )

        return list
    }

    private const val PI = kotlin.math.PI
}
