package eu.hithredin.bohurt.bohurtlib.modules.events

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import eu.hithredin.bohurt.bohurtlib.modules.genericType
import eu.hithredin.bohurt.bohurtlib.modules.opendatasoft.Coordinates
import eu.hithredin.bohurt.bohurtlib.modules.opendatasoft.ListResult
import java.util.*

/**
 * Tournament, Open training or other bohurt events
 */
data class EventData(
        val city: String,
        val link_website_contact_social_media_link_with_all_infos: String,
        val end_date: Date,
        val event_name: String,
        val duel_fight_categories: String,
        val group_fight_categories: String,
        val start_date_date: Date,
        val location: Coordinates,
        val timestamp: Date,
        val country: String
) {
    class Deserializer : ResponseDeserializable<ListResult<EventData>> {
        override fun deserialize(content: String) = Gson().fromJson<ListResult<EventData>>(content)
    }
}

val ListResultType = genericType<ListResult<EventData>>()