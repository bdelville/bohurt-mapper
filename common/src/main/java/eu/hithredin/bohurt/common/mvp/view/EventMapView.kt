package eu.hithredin.bohurt.common.mvp.view

import eu.hithredin.bohurt.common.data.EventData
import eu.hithredin.bohurt.common.mvp.viewmodel.SearchViewModel
import eu.hithredin.ktopendatasoft.GpsLocation

/**
 * Display bohurt events on a map
 */
interface EventMapView {
    fun showError(text:String)
    fun showLoader(loading:Boolean)
    fun setEvents(events: List<EventData>, allowZoom: Boolean)
    fun setMapLocation(location: GpsLocation)
    fun displayDates(searchVM: SearchViewModel)
    fun pickDates(searchVM: SearchViewModel): Boolean
}