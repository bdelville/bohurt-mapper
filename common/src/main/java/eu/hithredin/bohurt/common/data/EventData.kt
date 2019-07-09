package eu.hithredin.bohurt.common.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import eu.hithredin.bohurt.common.utils.moshi
import eu.hithredin.ktopendatasoft.Coordinates
import eu.hithredin.ktopendatasoft.ListResult
import eu.hithredin.ktopendatasoft.genericType
import java.io.Serializable
import java.util.Date

/**
 * Tournament, Open training or other bohurt events
 */
data class EventData(
    val event_name: String,
    val date: Date,
    var location: Coordinates,
    var link: String = "",
    val city: String = "",
    val country: String = "",
    val fight_categories: String = "",
    val description: String = "",
    var end_date: Date?,
    val timestamp: Date
) : Serializable {
    class Deserializer : ResponseDeserializable<ListResult<EventData>> {
        override fun deserialize(content: String): ListResult<EventData> {
            return try {
                // TODO allow filter out items if invalid (location null, ...)
                moshi.adapter<ListResult<EventData>>(ListResultType).fromJson(content)!!
            } catch (e: Exception) {
                ListResult()
            }
        }
    }

    fun isValid(): Boolean {
        if (end_date == null)
            end_date = date
        if (link.startsWith("www")) {
            link = "http://${link}"
        }
        return true
    }
}

val ListResultType = genericType<ListResult<EventData>>()