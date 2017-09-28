package eu.hithredin.bohurt.mapper.utils

import com.github.badoualy.datepicker.DatePickerTimeline
import java.util.*

fun DatePickerTimeline.selectedDate(): Date {
    return convertToDate(selectedYear, selectedMonth, selectedDay)
}

inline fun convertToDate(year: Int, month: Int, day: Int): Date {
    val ca = Calendar.getInstance()
    ca.set(year, month, day)
    return ca.time
}