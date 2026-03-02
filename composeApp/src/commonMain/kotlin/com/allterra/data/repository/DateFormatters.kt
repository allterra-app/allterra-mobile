package com.allterra.data.repository

internal fun isoDateToUiDate(input: String?): String {
    if (input.isNullOrBlank()) return ""
    val date = input.substringBefore('T')
    val parts = date.split('-')
    return if (parts.size == 3) {
        "${parts[2]}.${parts[1]}.${parts[0]}"
    } else {
        input
    }
}
