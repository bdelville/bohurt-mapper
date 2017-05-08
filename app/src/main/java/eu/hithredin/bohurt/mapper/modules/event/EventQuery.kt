package eu.hithredin.bohurt.mapper.modules.event

import eu.hithredin.ktopendatasoft.ApiQuery
import java.util.*

val dataSetEvents = "bohurt-events"

/**
 * Container of parameters to query an event
 */
class EventQuery : ApiQuery(dataSetEvents) {

    fun dateRange(firstDate: Date, lastDate: Date): EventQuery {
        andsQ.add("start_date_date >= " + dateToString(firstDate))
        andsQ.add("start_date_date <= " + dateToString(lastDate))
        return this
    }

    fun dateStart(firstDate: Date): EventQuery {
        andsQ.add("start_date_date >= " + dateToString(firstDate))
        return this
    }

    fun dateEnd(lastDate: Date): EventQuery {
        andsQ.add("end_date <= " + dateToString(lastDate))
        return this
    }
}