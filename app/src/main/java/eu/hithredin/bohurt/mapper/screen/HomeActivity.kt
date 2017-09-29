package eu.hithredin.bohurt.mapper.screen

import android.os.Bundle
import android.support.v4.widget.ContentLoadingProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.badoualy.datepicker.DatePickerTimeline
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.framework.BaseActivity
import eu.hithredin.bohurt.mapper.modules.event.EventData
import eu.hithredin.bohurt.mapper.modules.event.EventQuery
import eu.hithredin.bohurt.mapper.utils.selectedDate
import eu.hithredin.ktopendatasoft.ApiLoader
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Main screen of the application.
 * Will be refactored when the app evolve.
 *
 * TODO PR the library to disable the day subtitle and customize the color
 */
class HomeActivity : BaseActivity() {
    private val logger = KotlinLogging.logger {}
    val apiLoader: ApiLoader<EventData> by injector.instance()
    private val observeLoad = PublishSubject.create<Boolean>()
    private val disposables = CompositeDisposable()

    private lateinit var iconTourney: BitmapDescriptor
    private lateinit var datePickerStart: DatePickerTimeline
    private lateinit var datePickerEnd: DatePickerTimeline
    private lateinit var googleMap: GoogleMap

    private lateinit var titleStart: TextView
    private lateinit var titleEnd: TextView
    private lateinit var spinner: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setTitle(R.string.event_home_title_page)
        iconTourney = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_tourney_dark)

        // Load views
        datePickerStart = findViewById(R.id.datepicker_start)
        datePickerEnd = findViewById(R.id.datepicker_end)
        titleStart = findViewById(R.id.title_date_start)
        titleEnd = findViewById(R.id.title_date_end)
        spinner = findViewById(R.id.loader_map)

        datePickerStart.onDateSelectedListener = object : DatePickerTimeline.OnDateSelectedListener {
            override fun onDateSelected(year: Int, month: Int, day: Int, index: Int) {
                if (datePickerStart.selectedDate().after(datePickerEnd.selectedDate())) {
                    datePickerEnd.setSelectedDate(datePickerStart.selectedYear, datePickerStart.selectedMonth, datePickerStart.selectedDay)
                }
                titleStart.text = getString(R.string.search_date_title_start, datePickerStart.selectedYear.toString())
                observeLoad.onNext(true)
            }
        }
        datePickerEnd.onDateSelectedListener = DatePickerTimeline.OnDateSelectedListener { year, month, day, index ->
            if (datePickerStart.selectedDate().after(datePickerEnd.selectedDate())) {
                datePickerStart.setSelectedDate(datePickerEnd.selectedYear, datePickerEnd.selectedMonth, datePickerEnd.selectedDay)
            }
            titleEnd.text = getString(R.string.search_date_title_end, datePickerEnd.selectedYear.toString())
            observeLoad.onNext(true)
        }

        val now = Calendar.getInstance()
        datePickerStart.setLastVisibleDate(now.get(Calendar.YEAR) + 2, Calendar.JANUARY, 1)
        datePickerEnd.setLastVisibleDate(now.get(Calendar.YEAR) + 2, Calendar.JANUARY, 1)

        datePickerStart.setSelectedDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) - 1)
        datePickerEnd.setSelectedDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 3, now.get(Calendar.DAY_OF_MONTH))
        datePickerEnd.setFirstVisibleDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 3, now.get(Calendar.DAY_OF_MONTH))

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this::mapReady)
    }

    override fun onResume() {
        super.onResume()
        disposables.add(
                observeLoad.throttleLast(3, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { _ -> loadData() },
                                { e -> logger.error { "observeLoad:\n$e" } }))
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    fun mapReady(googleMap: GoogleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        val europe = LatLng(42.0, 5.0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(europe))
        googleMap.setOnMarkerClickListener({ marker ->
            val event: EventData = marker.tag as EventData
            // Enable details feature when function is ready
            //EventActivity.startActivity(this, event)
            true
        })

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isTiltGesturesEnabled = false
        this.googleMap = googleMap

        Single.just(0).delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { _ -> observeLoad.onNext(true) },
                        { e -> logger.error { "Single:\n$e" } })
    }

    fun loadData() {
        //TODO seems it uses cache when not cached
        spinner.show()
        googleMap.clear()
        val query = EventQuery()
                .dateStart(datePickerStart.selectedDate())
                .dateEnd(datePickerEnd.selectedDate())
        //TODO Rx queries and process
        //TODO Open details event

        apiLoader.queryList(query) { req, res, result ->
            result.fold({
                logger.info { "Result query:\n$it" }
                spinner.hide()
                it.data()
                        ?.filter { event -> event.isValid() }
                        ?.forEach { event ->
                            val point = LatLng(event.location.lat(), event.location.lon())
                            val marker = googleMap.addMarker(MarkerOptions()
                                    .position(point)
                                    .icon(iconTourney)
                                    .title(event.event_name))
                            marker.tag = event
                            //TODO Clustering according to Zoom Level
                        }
            }, {
                Toast.makeText(this, "Loading end with error", Toast.LENGTH_SHORT).show()
                logger.error { "Result query:\n$it" }
                spinner.hide()
            })
        }
    }
}
