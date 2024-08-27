package xt.qc.tappidigi.utils
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice

actual class Platform {
    actual val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}