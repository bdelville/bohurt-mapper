package eu.hithredin.bohurt.mapper.screen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.framework.BaseActivity
import eu.hithredin.bohurt.mapper.modules.event.EventData
import eu.hithredin.bohurt.mapper.modules.event.EventQuery
import eu.hithredin.easingdate.DateRangeChangeListener
import eu.hithredin.easingdate.HOUR
import eu.hithredin.easingdate.TimeMode
import eu.hithredin.ktopendatasoft.ApiLoader
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_home.*
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.TimeUnit

val DAY: Long = HOUR * 24
val YEAR: Long = DAY * 355

/**
 * Main screen of the application.
 * Will be refactored when the app evolve.
 */
class HomeActivity : BaseActivity() {
    private lateinit var googleMap: GoogleMap
    private val logger = KotlinLogging.logger {}
    val apiLoader: ApiLoader<EventData> by injector.instance()
    private lateinit var iconTourney: BitmapDescriptor
    private val observeLoad = PublishSubject.create<Boolean>()
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setTitle(R.string.event_home_title_page)
        iconTourney = BitmapDescriptorFactory.fromResource(R.drawable.map_icon_tourney_dark)

        val now = System.currentTimeMillis()
        search_date_picker.minDate = now - (1 * DAY)
        search_date_picker.maxDate = now + YEAR
        search_date_picker.mode = TimeMode.CUBIC
        search_date_picker.dateChangeSet = object : DateRangeChangeListener {
            override fun onDateChanged(lowerDate: Long, upperDate: Long) {
                Toast.makeText(this@HomeActivity, "Loading the data", Toast.LENGTH_SHORT).show()
                observeLoad.onNext(true)
            }
        }

        search_query.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                observeLoad.onNext(true)
            }
        })

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
            false
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
        googleMap.clear()
        val query = EventQuery()
                .dateStart(Date(search_date_picker.lowerDate))
                .dateEnd(Date(search_date_picker.upperDate))
                //TODO Manage search query .searchText(search_query.text.toString())

        apiLoader.queryList(query) { req, res, result ->
            result.fold({
                logger.info { "Result query:\n$it" }

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
            })
        }
    }
}
