package com.summer.math_and_go_assignment.utils

import java.time.format.DateTimeFormatter
import java.util.*

object Constants {
    var API_PROVIDING_DATE_FORMAT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    var DATE_DISPLAY_FORMAT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy MMM dd", Locale.getDefault())
    const val TOTAL_SCORE = 100
    const val STARTING_PAGE_INDEX = 0
    const val EXAM_NAME = "JEE Mains"
}