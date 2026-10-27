package com.example.carrom.model

enum class GameMode(val titleFa: String, val titleEn: String, val descFa: String) {
    DISC_POOL("دیسک پول", "Disc Pool", "اولین بازیکنی که تمام مهره‌های خود را پاکت کند برنده است"),
    CLASSIC("کاروم کلاسیک", "Classic Carrom", "قوانین رسمی به همراه مهره ملکه و لزوم کاور آن"),
    FREESTYLE("سبک آزاد (امتیازی)", "Freestyle Points", "سفید ۱۰ امتیاز، مشکی ۵ امتیاز، ملکه ۲۵ امتیاز"),
    VS_AI("نبرد با هوش مصنوعی", "vs Smart AI", "مسابقه با ربات هوشمند با ۳ سطح دشواری"),
    PASS_AND_PLAY("دونفره آفلاین", "Pass & Play", "بازی ۲ نفره روی یک گوشی مشترک"),
    TRICK_SHOTS("چالش و تمرین", "Trick Shots", "تمرین شوت‌های تخصصی، بازتاب و پاکت‌های سخت")
}

enum class AIDifficulty(val labelFa: String, val labelEn: String, val precision: Float) {
    EASY("مبتدی", "Novice", 0.70f),
    MEDIUM("حرفه‌ای", "Pro", 0.88f),
    MASTER("استاد بزرگ", "Grandmaster", 0.98f)
}

enum class TurnPlayer {
    PLAYER_1,
    PLAYER_2
}

enum class MatchStatus {
    AIMING,
    SIMULATING,
    TURN_RESOLVING,
    GAME_OVER
}

data class StrikerSkin(
    val id: String,
    val nameFa: String,
    val nameEn: String,
    val primaryColor: Long,
    val accentColor: Long,
    val powerRating: Int, // 1-5
    val aimLineRating: Int, // 1-5
    val price: Int,
    val isUnlocked: Boolean = false
)

data class BoardSkin(
    val id: String,
    val nameFa: String,
    val nameEn: String,
    val borderWoodColor: Long,
    val surfaceColor: Long,
    val linesColor: Long,
    val price: Int,
    val isUnlocked: Boolean = false
)

data class PlayerProfile(
    val name: String = "قهرمان کاروم",
    val avatarIndex: Int = 0,
    val coins: Int = 12500,
    val gems: Int = 150,
    val trophies: Int = 1420,
    val level: Int = 7,
    val xp: Int = 340,
    val wins: Int = 38,
    val totalMatches: Int = 45,
    val queenCovers: Int = 22,
    val highestScore: Int = 110,
    val equippedStrikerId: String = "royal_gold",
    val equippedBoardSkinId: String = "classic_teak"
)

data class MatchRuleState(
    var mode: GameMode = GameMode.DISC_POOL,
    var aiDifficulty: AIDifficulty = AIDifficulty.MEDIUM,
    var currentTurn: TurnPlayer = TurnPlayer.PLAYER_1,
    var player1Score: Int = 0,
    var player2Score: Int = 0,
    var player1Assigned: PieceType? = null,
    var player2Assigned: PieceType? = null,
    var queenPocketedBy: TurnPlayer? = null,
    var isQueenWaitingForCover: Boolean = false,
    var player1PocketedPieces: MutableList<PieceType> = mutableListOf(),
    var player2PocketedPieces: MutableList<PieceType> = mutableListOf(),
    var statusMessage: String = "",
    var isFoul: Boolean = false,
    var winner: TurnPlayer? = null
)
