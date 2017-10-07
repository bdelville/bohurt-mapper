package eu.hithredin.bohurt.mapper.utils

import java.util.*

inline fun convertToDate(year: Int, month: Int, day: Int): Date {
    val ca = Calendar.getInstance()
    ca.set(year, month, day)
    return ca.time
}