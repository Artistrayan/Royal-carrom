package com.example.carrom.model

object SkinsCatalog {
    val STRIKER_SKINS = listOf(
        StrikerSkin(
            id = "royal_gold",
            nameFa = "امپراتوری طلایی",
            nameEn = "Royal Emperor Gold",
            primaryColor = 0xFFFFD700,
            accentColor = 0xFFC79100,
            powerRating = 4,
            aimLineRating = 4,
            price = 0,
            isUnlocked = true
        ),
        StrikerSkin(
            id = "ruby_fire",
            nameFa = "ققنوس یاقوتی",
            nameEn = "Ruby Phoenix",
            primaryColor = 0xFFE91E63,
            accentColor = 0xFFFF5252,
            powerRating = 5,
            aimLineRating = 3,
            price = 5000,
            isUnlocked = false
        ),
        StrikerSkin(
            id = "emerald_dragon",
            nameFa = "اژدهای زمردین",
            nameEn = "Emerald Dragon",
            primaryColor = 0xFF00E676,
            accentColor = 0xFF1DE9B6,
            powerRating = 3,
            aimLineRating = 5,
            price = 8500,
            isUnlocked = false
        ),
        StrikerSkin(
            id = "cyber_neon",
            nameFa = "نئون سایبرپانک",
            nameEn = "Cyber Neon",
            primaryColor = 0xFF00E5FF,
            accentColor = 0xFF7C4DFF,
            powerRating = 5,
            aimLineRating = 5,
            price = 15000,
            isUnlocked = false
        ),
        StrikerSkin(
            id = "obsidian_shadow",
            nameFa = "سایه ابسیدین",
            nameEn = "Obsidian Titan",
            primaryColor = 0xFF37474F,
            accentColor = 0xFFFFD54F,
            powerRating = 4,
            aimLineRating = 4,
            price = 7000,
            isUnlocked = false
        )
    )

    val BOARD_SKINS = listOf(
        BoardSkin(
            id = "classic_teak",
            nameFa = "چوب ساج کلاسیک مسابقاتی",
            nameEn = "Classic Tournament Teak",
            borderWoodColor = 0xFF4A2511,
            surfaceColor = 0xFFDEB887,
            linesColor = 0xFF6B3A18,
            price = 0,
            isUnlocked = true
        ),
        BoardSkin(
            id = "royal_velvet",
            nameFa = "مخمل بنفش سلطنتی",
            nameEn = "Royal Velvet",
            borderWoodColor = 0xFF2A1538,
            surfaceColor = 0xFF2F2445,
            linesColor = 0xFFFFD700,
            price = 6000,
            isUnlocked = false
        ),
        BoardSkin(
            id = "midnight_dark",
            nameFa = "سیاه گرانیت شبانه",
            nameEn = "Midnight Granite",
            borderWoodColor = 0xFF1B1B1E,
            surfaceColor = 0xFF24272D,
            linesColor = 0xFF00E5FF,
            price = 10000,
            isUnlocked = false
        ),
        BoardSkin(
            id = "emerald_heritage",
            nameFa = "چوب زمرّدی کهن",
            nameEn = "Emerald Heritage",
            borderWoodColor = 0xFF1E3A2B,
            surfaceColor = 0xFF2E543E,
            linesColor = 0xFFFFF176,
            price = 8000,
            isUnlocked = false
        )
    )
}
