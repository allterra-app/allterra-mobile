package com.allterra.platform

expect object LocalFileAccess {
    fun readBytes(pathOrUri: String): ByteArray?
    fun fileName(pathOrUri: String): String
    fun contentType(pathOrUri: String): String
}
