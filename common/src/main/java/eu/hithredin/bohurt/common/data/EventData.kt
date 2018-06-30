package eu.hithredin.bohurt.common.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import eu.hithredin.ktopendatasoft.Coordinates
import eu.hithredin.ktopendatasoft.ListResult
import eu.hithredin.ktopendatasoft.genericType
import java.io.Serializable
import java.util.Date

/**
 * Tournament, Open training or other bohurt events
 *
 * TODO get rid of GSON and avoid this weird null system
 */
data class EventData(
    val event_name: String,
    val date: Date,
    var location: Coordinates,
    var link: String?,
    val city: String = "",
    val country: String = "",
    val fight_categories: String = "",
    val description: String = "",
    var end_date: Date?,
    val timestamp: Date
) : Serializable {
    class Deserializer : ResponseDeserializable<ListResult<EventData>> {
        override fun deserialize(content: String) = Gson().fromJson<ListResult<EventData>>(content)
    }

    /**
     * Sanitize qnd validate after Gson deserialization
     */
    fun isValid(): Boolean {
        if (event_name == null)
            return false
        if (location == null) {
            if (city == null || country == null)
                return false
            // TODO geocode with google
        }
        if (date == null)
            return false
        if (end_date == null)
            end_date = date
        if (link!!.startsWith("www")) {
            link = "http://${link}"
        }
        return true
    }
}

val ListResultType = genericType<ListResult<EventData>>()