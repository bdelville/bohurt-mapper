package eu.hithredin.bohurt.mapper.screen

import android.os.Bundle
import android.widget.Toast
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.framework.BaseActivity
import eu.hithredin.bohurt.mapper.modules.event.EventData
import eu.hithredin.bohurt.mapper.modules.event.EventQuery
import eu.hithredin.easingdate.DateRangeChangeListener
import eu.hithredin.easingdate.HOUR
import eu.hithredin.easingdate.TimeMode
import eu.hithredin.ktopendatasoft.ApiLoader
import kotlinx.android.synthetic.main.activity_home.*
import mu.KotlinLogging
import java.util.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setTitle(R.string.event_home_title_page)
        /*button_test.setOnClickListener {
            logger.info { "HomeActivity Start to load the data!" }
            loadData()
        }*/

        val now = System.currentTimeMillis()
        date_picker.minDate = now - (1 * DAY)
        date_picker.maxDate = now + YEAR
        date_picker.mode = TimeMode.CUBIC
        date_picker.dateChangeSet = object : DateRangeChangeListener {
            override fun onDateChanged(lowerDate: Long, upperDate: Long) {
                Toast.makeText(this@HomeActivity, "Loading the data", Toast.LENGTH_SHORT).show()
                loadData()
            }
        }

        // TODO Search by text too (AND)
        // TODO better font and paddings
        // TODO Background if searching in the past

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this::mapReady)
    }

    fun mapReady(googleMap: GoogleMap) {
        val europe = LatLng(42.0, 5.0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(europe))
        googleMap.setOnMarkerClickListener({ marker ->
            val event: EventData = marker.tag as EventData
            EventActivity.startActivity(this, event)
            false
        })
        //TODO click on bubble to open activity

        this.googleMap = googleMap
        loadData()
    }

    fun loadData() {
        googleMap.clear()
        val query = EventQuery()
                .dateStart(Date(date_picker.lowerDate))
                .dateEnd(Date(date_picker.upperDate))

        apiLoader.queryList(query) { req, res, result ->
            result.fold({
                logger.info { "Result query:\n$it" }

                it.data()
                        ?.filter { event -> event.isValid() }
                        ?.forEach { event ->
                            val point = LatLng(event.location.lat(), event.location.lon())
                            val marker = googleMap.addMarker(MarkerOptions()
                                    .position(point)
                                    .title(event.event_name))
                            marker.tag = event
                            // TODO Nice icon
                        }
            }, {
                Toast.makeText(this, "Loading end with error", Toast.LENGTH_SHORT).show()
                logger.error { "Result query:\n$it" }
            })
        }
    }
}
