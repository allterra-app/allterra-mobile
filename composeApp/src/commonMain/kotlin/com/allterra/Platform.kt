package com.allterra

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform