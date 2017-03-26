package eu.hithredin.bohurt.bohurtlib.modules.opendatasoft

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

    fun dateToString(date: Date) : String{
        return dateFormat.format(date)
    }

    fun rowCount(rows: Int){
        params.put("rows", rows.toString())
    }

    fun sortBy(fieldName: String, ascending: Boolean){
        params.put("sort", if(ascending) "+" else "-" + fieldName)
    }

    fun inCircle(lat: Double, lon: Double, meters: Int){
        andsQ.add("geofilter.distance=$lat,$lon,$meters")
    }

    open fun buildQuery(): String{
        return andsQ.reduceIndexed { index, acc, s ->   acc + s + if(index < (andsQ.size - 1)) "AND" else "" }
    }

    open fun buildParams(): HashMap<String, String> {
        params.put("dataset", dataset)
        params.put("q", buildQuery())
        return params
    }
}