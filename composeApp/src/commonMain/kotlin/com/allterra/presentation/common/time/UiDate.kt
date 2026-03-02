package com.allterra.presentation.common.time

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun currentUiDate(): String {
    val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val day = date.day.toString().padStart(2, '0')
    val month = (date.month.ordinal + 1).toString().padStart(2, '0')
    val year = date.year
    return "$day.$month.$year"
}
