package com.example.wibso.utils

import androidx.compose.ui.graphics.Color

interface ChatThemes {
    val backgroundColor: Color
    val ownerColor: Color
    val otherColor: Color
    val sendButtonColor: Color
    val informationColor: Color
}

object GreenPalette: ChatThemes {
    override val backgroundColor: Color
        get() = ColorsPalette.softFern
    override val ownerColor: Color
        get() = ColorsPalette.forestMoss
    override val otherColor: Color
        get() = Color.White
    override val sendButtonColor: Color
        get() = ColorsPalette.sageGreen
    override val informationColor: Color
        get() = ColorsPalette.sageGreen
}