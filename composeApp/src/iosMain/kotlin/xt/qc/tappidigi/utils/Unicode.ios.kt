package xt.qc.tappidigi.utils
import Swift

actual fun Int.toUnicode(): String {
    return UnicodeScalar(this)
}