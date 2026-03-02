package com.allterra.platform

actual object LocalFileAccess {
    actual fun readBytes(pathOrUri: String): ByteArray? = null

    actual fun fileName(pathOrUri: String): String = "file"

    actual fun contentType(pathOrUri: String): String = "application/octet-stream"
}
