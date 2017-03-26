package eu.hithredin.bohurt.mapper.screen

import android.os.Bundle
import android.widget.Toast
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import eu.hithredin.bohurt.bohurtlib.modules.events.EventData
import eu.hithredin.bohurt.bohurtlib.modules.opendatasoft.ApiLoader
import eu.hithredin.bohurt.bohurtlib.modules.opendatasoft.EventQuery
import eu.hithredin.bohurt.mapper.R
import kotlinx.android.synthetic.main.activity_home.*
import mu.KotlinLogging
import java.util.*


/**
 * Main screen of the application.
 * Will be refactored when the qpp evolve.
 */
class HomeActivity : BaseActivity() {
    val baseUrl = "https://hithredin.my.opendatasoft.com/api/records/1.0/search/?dataset=bohurt-events"
    private lateinit var googleMap: GoogleMap
    private val logger = KotlinLogging.logger {}
    val apiLoader: ApiLoader<EventData> by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        button_test.setOnClickListener {
            logger.info { "HomeActivity Start to load the data!" }
            loadData()
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
        try {
            val query = EventQuery().dateStart(Date())
            apiLoader.queryList(query) { req, res, result ->
                val (data, err) = result
                Toast.makeText(this, "Loading end", Toast.LENGTH_SHORT).show()
                logger.info { "Result query:\n$data \n$err" }

                data?.data()?.forEach { event ->
                    val point = LatLng(event.location.lat(), event.location.lon())
                    googleMap.addMarker(MarkerOptions()
                            .position(point)
                            .title(event.event_name))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
