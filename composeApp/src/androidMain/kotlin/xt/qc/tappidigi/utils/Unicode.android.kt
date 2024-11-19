package xt.qc.tappidigi.utils

actual fun Int.toUnicode(): String {
    return String(Character.toChars(this))
}