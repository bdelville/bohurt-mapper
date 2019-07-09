package eu.hithredin.bohurt.common.utils

import java.util.*

fun convertToDate(year: Int, month: Int, day: Int): Date {
    val ca = Calendar.getInstance()
    ca.set(year, month, day)
    return ca.time
}