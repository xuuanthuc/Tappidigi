package xt.qc.tappidigi.utils

expect class Platform{
    val name: String
}

internal expect class SharedFileReader() {
    fun loadJsonFile(fileName: String): String?
}