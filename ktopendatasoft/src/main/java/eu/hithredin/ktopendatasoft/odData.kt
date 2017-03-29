package eu.hithredin.ktopendatasoft

/**
 * Open Data Soft structure data type
 */
data class ListResult<T : Any>(val nhits: Int, val records: List<ItemResult<T>>) {
    fun data(): List<T>? {
        return records.map { item -> item.fields }
    }
}

data class ItemResult<T>(val recordid: String, val fields: T, val geometry: Geometry)

data class Geometry(val type: String)

class Coordinates : ArrayList<Double>() {

    fun lat(): Double {
        return get(0)
    }

    fun lon(): Double {
        return get(1)
    }
}