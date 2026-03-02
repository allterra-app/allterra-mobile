package com.allterra.presentation.routes

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RouteGpxParserTest {

    @Test
    fun analyzeGpx_parsesSelfClosingPoints() {
        val gpx = """
            <gpx>
              <trk>
                <name>Self Closing</name>
                <trkseg>
                  <trkpt lat="51.8402" lon="16.5748" />
                  <trkpt lat="51.8427" lon="16.5864" />
                  <trkpt lat="51.8490" lon="16.5932" />
                </trkseg>
              </trk>
            </gpx>
        """.trimIndent()

        val result = analyzeGpx(gpx)

        assertEquals("Self Closing", result.routeName)
        assertEquals(3, result.pointCount)
        assertNotNull(result.distanceKm)
        assertTrue(result.distanceKm > 0.0)
    }
}
