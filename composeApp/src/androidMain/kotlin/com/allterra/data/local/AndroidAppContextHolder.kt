package com.allterra.data.local

import android.content.Context

object AndroidAppContextHolder {
    lateinit var appContext: Context
        private set

    fun initialize(context: Context) {
        if (!::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }
}
