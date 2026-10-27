package com.example.carrom.model

data class TrickShotScenario(
    val id: Int,
    val titleFa: String,
    val titleEn: String,
    val descriptionFa: String,
    val descriptionEn: String,
    val targetPocketsDesc: String,
    val pieces: List<CarromPiece>,
    val strikerStartX: Float,
    val strikerStartY: Float,
    val maxShots: Int = 1,
    val starsUnlocked: Int = 0
)

object TrickShotBank {
    fun getLevels(): List<TrickShotScenario> {
        return listOf(
            TrickShotScenario(
                id = 1,
                titleFa = "ضربه مستقیم به گوشه",
                titleEn = "Direct Corner Pocket",
                descriptionFa = "مهره سفید را مستقیماً به پاکت پایین سمت راست بفرستید.",
                descriptionEn = "Pocket the white piece directly into the bottom-right pocket.",
                targetPocketsDesc = "پاکت راست پایین",
                pieces = listOf(
                    CarromPiece(1, PieceType.WHITE, 700f, 700f, radius = 24f, mass = 1.0f)
                ),
                strikerStartX = 350f,
                strikerStartY = 840f
            ),
            TrickShotScenario(
                id = 2,
                titleFa = "کات زاویه تند",
                titleEn = "Sharp Angle Cut",
                descriptionFa = "با ضربه مایل، مهره مشکی را به پاکت سمت چپ بفرستید.",
                descriptionEn = "Cut the black coin at a sharp angle into the top-left pocket.",
                targetPocketsDesc = "پاکت چپ بالا",
                pieces = listOf(
                    CarromPiece(1, PieceType.BLACK, 400f, 350f, radius = 24f, mass = 1.0f)
                ),
                strikerStartX = 650f,
                strikerStartY = 840f
            ),
            TrickShotScenario(
                id = 3,
                titleFa = "ضربه بازتاب از دیواره (بانک)",
                titleEn = "Cushion Bank Rebound",
                descriptionFa = "به دیواره سمت راست ضربه بزنید تا با بازتاب مهره ملکه را پاکت کنید.",
                descriptionEn = "Bank off the right cushion to ricochet into the Queen.",
                targetPocketsDesc = "پاکت راست بالا",
                pieces = listOf(
                    CarromPiece(1, PieceType.QUEEN, 750f, 300f, radius = 24f, mass = 1.0f),
                    CarromPiece(2, PieceType.BLACK, 600f, 500f, radius = 24f, mass = 1.0f) // obstacle
                ),
                strikerStartX = 300f,
                strikerStartY = 840f
            ),
            TrickShotScenario(
                id = 4,
                titleFa = "عبور از میان دو مانع",
                titleEn = "Needle Through Obstacles",
                descriptionFa = "استرایکر را با دقت از میان دو مهره مانع رد کرده و مهره سفید را پاکت کنید.",
                descriptionEn = "Shoot cleanly between the two black defenders to pocket the white coin.",
                targetPocketsDesc = "پاکت مرکز بالا",
                pieces = listOf(
                    CarromPiece(1, PieceType.WHITE, 500f, 250f, radius = 24f, mass = 1.0f),
                    CarromPiece(2, PieceType.BLACK, 440f, 500f, radius = 24f, mass = 1.0f),
                    CarromPiece(3, PieceType.BLACK, 560f, 500f, radius = 24f, mass = 1.0f)
                ),
                strikerStartX = 500f,
                strikerStartY = 840f
            ),
            TrickShotScenario(
                id = 5,
                titleFa = "ضربه دوگانه ملکه و کاور",
                titleEn = "Queen & Cover Combo",
                descriptionFa = "ملکه را به پاکت چپ و مهره سفید را به پاکت راست هدایت کنید.",
                descriptionEn = "Pocket the Queen and its white cover simultaneously.",
                targetPocketsDesc = "دو پاکت همزمان",
                pieces = listOf(
                    CarromPiece(1, PieceType.QUEEN, 470f, 480f, radius = 24f, mass = 1.0f),
                    CarromPiece(2, PieceType.WHITE, 530f, 480f, radius = 24f, mass = 1.0f)
                ),
                strikerStartX = 500f,
                strikerStartY = 840f,
                maxShots = 2
            ),
            TrickShotScenario(
                id = 6,
                titleFa = "شوت استاد بزرگ (چالش نهایی)",
                titleEn = "Grandmaster Final Trick",
                descriptionFa = "با یک ضربه پرقدرت ۳ مهره را در وضعیت‌های مختلف حرکت داده و پاکت کنید.",
                descriptionEn = "Clear all remaining pieces with masterful precision rebounds.",
                targetPocketsDesc = "هر پاکتی",
                pieces = listOf(
                    CarromPiece(1, PieceType.WHITE, 300f, 300f, radius = 24f, mass = 1.0f),
                    CarromPiece(2, PieceType.WHITE, 700f, 300f, radius = 24f, mass = 1.0f),
                    CarromPiece(3, PieceType.QUEEN, 500f, 400f, radius = 24f, mass = 1.0f)
                ),
                strikerStartX = 500f,
                strikerStartY = 840f,
                maxShots = 3
            )
        )
    }
}
