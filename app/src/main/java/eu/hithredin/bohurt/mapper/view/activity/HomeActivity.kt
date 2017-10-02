package eu.hithredin.bohurt.mapper.view.activity

import android.os.Bundle
import android.widget.Toast
import com.github.badoualy.datepicker.DatePickerTimeline
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.model.event.EventData
import eu.hithredin.bohurt.mapper.model.event.EventQuery
import eu.hithredin.bohurt.mapper.utils.selectedDate
import eu.hithredin.bohurt.mapper.view.EventListView
import eu.hithredin.bohurt.mapper.view.framework.BaseActivity
import eu.hithredin.ktopendatasoft.ApiLoader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_home.*
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Main screen of the application.
 *
 * Will be refactored when the app evolve.
 */
class HomeActivity : BaseActivity(), EventListView {

    private val logger = KotlinLogging.logger {}
    val apiLoader: ApiLoader<EventData> by injector.instance()
    private val observeLoad = PublishSubject.create<Boolean>()
    private val disposables = CompositeDisposable()
    private lateinit var googleMap: GoogleMap
    private lateinit var iconTourney: BitmapDescriptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setTitle(R.string.event_home_title_page)
        iconTourney = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_tourney_dark)

        // Load views
        datepicker_start.onDateSelectedListener = object : DatePickerTimeline.OnDateSelectedListener {
            override fun onDateSelected(year: Int, month: Int, day: Int, index: Int) {
                if (datepicker_start.selectedDate().after(datepicker_end.selectedDate())) {
                    datepicker_end.setSelectedDate(datepicker_start.selectedYear, datepicker_start.selectedMonth, datepicker_start.selectedDay)
                }
                title_date_start.text = getString(R.string.search_date_title_start, datepicker_start.selectedYear.toString())
                observeLoad.onNext(true)
            }
        }
        datepicker_end.onDateSelectedListener = DatePickerTimeline.OnDateSelectedListener { year, month, day, index ->
            if (datepicker_start.selectedDate().after(datepicker_end.selectedDate())) {
                datepicker_start.setSelectedDate(datepicker_end.selectedYear, datepicker_end.selectedMonth, datepicker_end.selectedDay)
            }
            title_date_end.text = getString(R.string.search_date_title_end, datepicker_end.selectedYear.toString())
        }

        // Set up form first value
        val now = Calendar.getInstance()
        datepicker_start.setLastVisibleDate(now.get(Calendar.YEAR) + 2, Calendar.JANUARY, 1)
        datepicker_end.setLastVisibleDate(now.get(Calendar.YEAR) + 2, Calendar.JANUARY, 1)

        datepicker_start.setSelectedDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) - 1)
        datepicker_end.setSelectedDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 3, now.get(Calendar.DAY_OF_MONTH))
        datepicker_end.setFirstVisibleDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 3, now.get(Calendar.DAY_OF_MONTH))

        // Load map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this::mapReady)
    }

    override fun onResume() {
        super.onResume()
        disposables.add(
                observeLoad.throttleLast(3, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { loader_map.show() }
                        .observeOn(Schedulers.io())
                        .map {
                            EventQuery()
                                    .dateStart(datepicker_start.selectedDate())
                                    .dateEnd(datepicker_end.selectedDate())
                        }
                        .flatMapSingle { apiLoader.queryList(it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    logger.info { "Result query:\n$it" }
                                    loader_map.hide()
                                    clearEventList()

                                    it.second.success {
                                        setEvents(it.data()
                                                ?.filter { event -> event.isValid() }
                                                .orEmpty())
                                    }
                                    it.second.failure {
                                        Toast.makeText(this, "Loading end with error", Toast.LENGTH_SHORT).show()
                                        logger.error { "Result query:\n$it" }
                                    }
                                },
                                { e -> logger.error { "observeLoad:\n$e" } }
                        ))
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    fun mapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        setMapLocation(LatLng(42.0, 5.0))
        googleMap.setOnInfoWindowClickListener({ marker ->
            val event: EventData = marker.tag as EventData
            EventActivity.startActivity(this, event)
        })

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isTiltGesturesEnabled = false
        observeLoad.onNext(true)
    }

    override fun clearEventList() {
        googleMap.clear()
    }

    override fun setEvents(eventList: List<EventData>) {
        eventList?.forEach { event ->
            val point = LatLng(event.location.lat(), event.location.lon())
            val marker = googleMap.addMarker(MarkerOptions()
                    .position(point)
                    .icon(iconTourney)
                    .title(event.event_name)
                    .snippet("${event.date}"))
            marker.tag = event
            //TODO Clustering according to Zoom Level
        }
    }

    override fun setMapLocation(location: LatLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }
}
