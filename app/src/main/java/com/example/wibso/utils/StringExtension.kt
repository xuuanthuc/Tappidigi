package com.example.wibso.utils

fun String.removeLastChar(): String {
    if (isEmpty()) return this
    val lastIndex = length - 1
    val lastChar = this[lastIndex]
//    val codePoint = codePointAt(lastIndex)
//    if (codePoint in 0x1F3FB..0x1F3FF) {
//        if (length > 1 && this[length - 2].isSurrogate()) {
//            return dropLast(4)
//        }
//    }
//    if (endsWith("\u200B")) {
//        return dropLast(1)
//    }
    if (lastChar.isSurrogate()) {
        return dropLast(2)
    }

    return dropLast(1)
}