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
        /*button_test.setOnClickListener {
            logger.info { "HomeActivity Start to load the data!" }
            loadData()
        }*/

        val now = System.currentTimeMillis()
        date_picker.minDate = now - (10 * DAY)
        date_picker.maxDate = now + 90 * DAY
        date_picker.mode = TimeMode.CUBIC
        date_picker.dateChangeSet = object : DateRangeChangeListener {
            override fun onDateChanged(lowerDate: Long, upperDate: Long) {
                Toast.makeText(this@HomeActivity, "Loading the data", Toast.LENGTH_SHORT).show()
                loadData()
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this::mapReady)
    }

    fun mapReady(googleMap: GoogleMap) {
        val europe = LatLng(42.0, 5.0)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(europe))
        this.googleMap = googleMap
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
                            googleMap.addMarker(MarkerOptions()
                                    .position(point)
                                    .title(event.event_name))
                        }
            }, {
                Toast.makeText(this, "Loading end with error", Toast.LENGTH_SHORT).show()
                logger.error { "Result query:\n$it" }
            })
        }
    }
}
