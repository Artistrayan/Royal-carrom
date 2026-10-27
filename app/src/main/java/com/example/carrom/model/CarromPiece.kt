package com.example.carrom.model

enum class PieceType {
    WHITE,
    BLACK,
    QUEEN,
    STRIKER
}

data class CarromPiece(
    val id: Int,
    val type: PieceType,
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val radius: Float,
    val mass: Float,
    var isPocketed: Boolean = false,
    var pocketScale: Float = 1.0f,
    var pocketAlpha: Float = 1.0f,
    var isSinking: Boolean = false,
    var prevX: Float = x,
    var prevY: Float = y,
    var rotation: Float = 0f,
    var angularVelocity: Float = 0f
) {
    fun speed(): Float = kotlin.math.sqrt(vx * vx + vy * vy)
    fun isMoving(): Boolean = isSinking || speed() > 0.08f || kotlin.math.abs(angularVelocity) > 0.05f

    fun deepCopy(): CarromPiece = CarromPiece(
        id = id,
        type = type,
        x = x,
        y = y,
        vx = vx,
        vy = vy,
        radius = radius,
        mass = mass,
        isPocketed = isPocketed,
        pocketScale = pocketScale,
        pocketAlpha = pocketAlpha,
        isSinking = isSinking,
        prevX = prevX,
        prevY = prevY,
        rotation = rotation,
        angularVelocity = angularVelocity
    )
}
