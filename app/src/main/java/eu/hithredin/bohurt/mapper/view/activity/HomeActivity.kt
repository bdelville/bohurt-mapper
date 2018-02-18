package eu.hithredin.bohurt.mapper.view.activity

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import com.borax12.materialdaterangepicker.date.DatePickerDialog
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
import eu.hithredin.bohurt.mapper.utils.convertToDate
import eu.hithredin.bohurt.mapper.view.EventListView
import eu.hithredin.bohurt.mapper.view.addOnClickListener
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
class HomeActivity : BaseActivity(), EventListView, DatePickerDialog.OnDateSetListener {

    private val logger = KotlinLogging.logger {}
    val apiLoader: ApiLoader<EventData> by injector.instance()
    private val observeLoad = PublishSubject.create<Boolean>()
    private val disposables = CompositeDisposable()
    private lateinit var googleMap: GoogleMap
    private lateinit var iconTourney: BitmapDescriptor
    private lateinit var iconTourneyOld: BitmapDescriptor
    private lateinit var dateStart: Date
    private lateinit var dateEnd: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setTitle(R.string.event_home_title_page)

        // Load views
        btn_date_range.addOnClickListener (View.OnClickListener { pickDates(true) })

        // Set up date first value
        val now = Calendar.getInstance()
        now.add(Calendar.MONTH, -1)
        dateStart = now.time
        now.add(Calendar.MONTH, 6)
        dateEnd = now.time

        // Load map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this::mapReady)
    }

    override fun onDateSet(view: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        dateStart = convertToDate(year, monthOfYear, dayOfMonth)
        dateEnd = convertToDate(yearEnd, monthOfYearEnd, dayOfMonthEnd)
        setDates()
        observeLoad.onNext(true)
    }

    fun setDates() {
        text_date_start.text = DateFormat.getLongDateFormat(this).format(dateStart)
        text_date_end.text = DateFormat.getLongDateFormat(this).format(dateEnd)
    }

    private fun pickDates(selectFrom: Boolean): Boolean {
        // todo change again to a better date picker
        val start = Calendar.getInstance()
        start.time = dateStart
        val end = Calendar.getInstance()
        end.add(Calendar.MONTH, 3)
        end.time = dateEnd

        val dpd = DatePickerDialog.newInstance(
            this,
            start.get(Calendar.YEAR),
            start.get(Calendar.MONTH),
            start.get(Calendar.DAY_OF_MONTH),
            end.get(Calendar.YEAR),
            end.get(Calendar.MONTH),
            end.get(Calendar.DAY_OF_MONTH)
        )

        // Set limits
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.MONTH, 18)
        dpd.maxDate = maxDate
        val minDate = Calendar.getInstance()
        minDate.add(Calendar.MONTH, -36)
        dpd.minDate = minDate
        dpd.isAutoHighlight = true

        dpd.show(fragmentManager, "Datepickerdialog")
        return true
    }

    override fun onResume() {
        super.onResume()
        setDates()

        disposables.add(
            observeLoad.throttleLast(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { loader_map.show() }
                .observeOn(Schedulers.io())
                .map {
                    EventQuery()
                        .dateStart(dateStart)
                        .dateEnd(dateEnd)
                        .rowCount(40)
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
        iconTourney = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_tourney)
        iconTourneyOld = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_tourney_dark)
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
        val now = Date()
        eventList.forEach { event ->
            val point = LatLng(event.location.lat(), event.location.lon())
            val marker = googleMap.addMarker(MarkerOptions()
                .position(point)
                .icon(if (event.date.before(now)) iconTourneyOld else iconTourney)
                .title(event.event_name)
                .snippet(DateFormat.getLongDateFormat(this).format(event.date)))
            marker.tag = event
            //TODO Clustering according to Zoom Level
        }
    }

    override fun setMapLocation(location: LatLng) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }
}
