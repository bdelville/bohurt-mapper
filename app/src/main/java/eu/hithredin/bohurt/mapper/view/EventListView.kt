package eu.hithredin.bohurt.mapper.view

import com.google.android.gms.maps.model.LatLng
import eu.hithredin.bohurt.mapper.model.event.EventData

/**
 * INSERT DOC
 */

interface EventListView {
    fun clearEventList()
    fun setEvents(events: List<EventData>)
    fun setMapLocation(location: LatLng)
}