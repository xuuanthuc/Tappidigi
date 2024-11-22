package xt.qc.tappidigi.utils
import platform.UIKit.UIDevice

actual class Platform {
    actual val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    actual fun fetchImagesFromGallery(): List<String> {
        TODO("Not yet implemented")
    }
}