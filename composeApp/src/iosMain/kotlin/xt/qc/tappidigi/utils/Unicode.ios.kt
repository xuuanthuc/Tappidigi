package xt.qc.tappidigi.utils

import platform.Foundation.NSString

actual fun Int.toUnicode(): String {
    return "UnicodeScalar(this)"
}