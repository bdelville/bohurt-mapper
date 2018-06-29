package eu.hithredin.bohurt.mapper.view.activity

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import eu.hithredin.bohurt.common.data.EventData
import eu.hithredin.bohurt.common.mvp.presenter.EventMapPresenter
import eu.hithredin.bohurt.common.mvp.view.EventMapView
import eu.hithredin.bohurt.common.mvp.viewmodel.SearchViewModel
import eu.hithredin.bohurt.common.utils.convertToDate
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.utils.addOnClickListener
import eu.hithredin.bohurt.mapper.utils.toLatLng
import eu.hithredin.bohurt.mapper.view.framework.BaseActivity
import eu.hithredin.ktopendatasoft.GpsLocation
import kotlinx.android.synthetic.main.activity_home.*
import java.util.Calendar
import java.util.Date

/**
 * Main screen of the application.
 *
 * Will be refactored when the app evolve.
 */
class HomeActivity : BaseActivity(), EventMapView, DatePickerDialog.OnDateSetListener {

    private val presenter by loadPresenter { EventMapPresenter(this, injector) }

    private lateinit var googleMap: GoogleMap
    private lateinit var iconTourney: BitmapDescriptor
    private lateinit var iconTourneyOld: BitmapDescriptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_home)
        setTitle(R.string.event_home_title_page)

        // Load views
        btn_date_range.addOnClickListener(View.OnClickListener { pickDates(presenter.searchVM) })

        // Load map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this::mapReady)
    }

    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        presenter.setSearchDateRange(convertToDate(year, monthOfYear, dayOfMonth), convertToDate(yearEnd, monthOfYearEnd, dayOfMonthEnd))
    }

    override fun displayDates(searchVM: SearchViewModel) {
        text_date_start.text = DateFormat.getLongDateFormat(this).format(searchVM.dateStart)
        text_date_end.text = DateFormat.getLongDateFormat(this).format(searchVM.dateEnd)
    }

    override fun pickDates(searchVM: SearchViewModel): Boolean {
        // todo change again to a better date picker
        val start = Calendar.getInstance()
        start.time = searchVM.dateStart
        val end = Calendar.getInstance()
        end.add(Calendar.MONTH, 3)
        end.time = searchVM.dateEnd

        val dpd = DatePickerDialog.newInstance(
            this,
            start.get(Calendar.YEAR),
            start.get(Calendar.MONTH),
            start.get(Calendar.DAY_OF_MONTH),
            end.get(Calendar.YEAR),
            end.get(Calendar.MONTH),
            end.get(Calendar.DAY_OF_MONTH)
        )

        dpd.maxDate = searchVM.maxDate
        dpd.minDate = searchVM.minDate
        dpd.isAutoHighlight = true

        dpd.show(fragmentManager, "DatePickerDialog")
        return true
    }

    private fun mapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        iconTourney = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_tourney)
        iconTourneyOld = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_tourney_dark)
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        setMapLocation(GpsLocation(42.0, 5.0))
        googleMap.setOnInfoWindowClickListener { marker ->
            val event: EventData = marker.tag as EventData
            EventActivity.startActivity(this, event)
        }

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isTiltGesturesEnabled = false
        presenter.launchSearch()
    }

    override fun setEvents(events: List<EventData>, allowZoom: Boolean) {
        val now = Date()
        val bounds = LatLngBounds.Builder()
        googleMap.clear()

        events.forEach { event ->
            val point = LatLng(event.location.lat(), event.location.lon())
            val marker = googleMap.addMarker(MarkerOptions()
                .position(point)
                .icon(if (event.date.before(now)) iconTourneyOld else iconTourney)
                .title(event.event_name)
                .snippet(DateFormat.getLongDateFormat(this).format(event.date)))
            marker.tag = event
            bounds.include(point)
            //TODO Clustering according to Zoom Level
        }
        if (allowZoom && !events.isEmpty()) {
            val cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), 0)
            googleMap.animateCamera(cu)
        }
    }

    override fun setMapLocation(location: GpsLocation) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(location.toLatLng()))
    }

    override fun showError(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun showLoader(loading: Boolean) {
        if (loading) loader_map.show() else loader_map.hide()
    }
}
