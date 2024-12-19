package com.example.wibso.utils

fun Int.toUnicode(): String {
    return String(Character.toChars(this))
}
