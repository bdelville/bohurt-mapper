package eu.hithredin.bohurt.mapper.modules.event

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import eu.hithredin.ktopendatasoft.Coordinates
import eu.hithredin.ktopendatasoft.ListResult
import eu.hithredin.ktopendatasoft.genericType
import java.io.Serializable
import java.util.*

/**
 * Tournament, Open training or other bohurt events
 */
data class EventData(
        val city: String,
        val link: String?,
        val event_name: String,
        val duel_fight_categories: String,
        val group_fight_categories: String,
        val date: Date,
        var location: Coordinates,
        var end_date: Date?,
        val timestamp: Date,
        val country: String
) : Serializable {
    class Deserializer : ResponseDeserializable<ListResult<EventData>> {
        override fun deserialize(content: String) = Gson().fromJson<ListResult<EventData>>(content)
    }

    /**
     * Sanitize qnd validate after Gson deserialization
     */
    fun isValid(): Boolean {
        if(event_name == null)
            return false
        if(location == null) {
            if (city == null || country == null)
                return false
            // TODO geocode with google
        }
        if(date == null)
            return false
        if(end_date == null)
            end_date = date
        return true
    }
}

val ListResultType = genericType<ListResult<EventData>>()