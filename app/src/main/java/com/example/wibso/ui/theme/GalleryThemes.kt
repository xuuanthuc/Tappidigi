package com.example.wibso.utils

import androidx.compose.ui.graphics.Color

interface ItemGalleryStyle {
    val color: Color
}

class ChatGalleryStyle : ItemGalleryStyle {
    override val color: Color
        get() = ColorsPalette.forestMoss
}

class PostGalleryStyle : ItemGalleryStyle {
    override val color: Color
        get() = ColorsPalette.forestMoss
}