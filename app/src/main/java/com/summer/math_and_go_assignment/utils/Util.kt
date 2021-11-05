package com.summer.math_and_go_assignment.utils

import android.content.Context
import android.widget.Toast
import java.time.LocalDate

object Util {
    fun getLocalDate(inputDate: String): String {
        val localDate =
            LocalDate.parse(inputDate, Constants.API_PROVIDING_DATE_FORMAT)
        return Constants.DATE_DISPLAY_FORMAT.format(localDate)
    }

    fun showShortToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}