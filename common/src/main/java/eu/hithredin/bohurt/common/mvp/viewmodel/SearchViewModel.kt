package eu.hithredin.bohurt.common.mvp.viewmodel

import java.util.*

/**
 * Contain the criteria to search bohurt events
 */
data class SearchViewModel(
    var dateStart: Date,
    var dateEnd: Date,
    var minDate: Calendar,
    var maxDate: Calendar
)
