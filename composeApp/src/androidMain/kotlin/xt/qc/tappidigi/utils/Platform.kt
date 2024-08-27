package xt.qc.tappidigi.utils

import android.os.Build
import java.util.Locale

actual class Platform {
    actual val name: String = "Android ${Build.VERSION.SDK_INT}"
}