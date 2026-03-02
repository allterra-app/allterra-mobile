package com.allterra.routes

import kotlin.test.Test
import kotlin.test.assertTrue

class SampleGpxAndroidUnitTest {

    @Test
    fun figmaGpx_isAvailableInAndroidUnitTestResources() {
        val stream = this::class.java.classLoader?.getResourceAsStream("test_gpx.gpx")
        requireNotNull(stream) { "test_gpx.gpx is missing in androidUnitTest resources" }

        val content = stream.bufferedReader().use { it.readText() }
        assertTrue(content.contains("<gpx"))
        assertTrue(content.contains("Leszno Cycling"))
    }
}
