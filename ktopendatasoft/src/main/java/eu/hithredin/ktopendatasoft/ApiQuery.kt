package eu.hithredin.ktopendatasoft

import java.text.SimpleDateFormat
import java.util.*

val dateFormat = SimpleDateFormat("yyyy/MM/dd")
val dayFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")

/**
 * Container of parameters to query an OpenDatasoft basic data
 */
open class ApiQuery(val dataset: String) {

    protected val params = HashMap<String, String>()
    protected val andsQ = HashSet<String>()

    fun dateToString(date: Date): String {
        return dateFormat.format(date)
    }

    /**
     * rows count
     * page starting with 0 as the first page
     */
    fun rowCount(rows: Int = 15, page: Int = 0): ApiQuery {
        params.put("rows", rows.toString())
        params.put("start", (page * rows).toString())
        return this
    }

    fun sortBy(fieldName: String, ascending: Boolean = false): ApiQuery {
        params.put("sort", if (ascending) "+" else "-" + fieldName)
        return this
    }

    fun insideLocation(lat: Double, lon: Double, meters: Int = 5000): ApiQuery {
        andsQ.add("geofilter.distance=$lat,$lon,$meters")
        return this
    }

    open fun buildQuery(): String {
        return andsQ.joinToString(" AND ")
    }

    open fun buildParams(): HashMap<String, String> {
        params.put("dataset", dataset)
        params.put("q", buildQuery())
        return params
    }
}