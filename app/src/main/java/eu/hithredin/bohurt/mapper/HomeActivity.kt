package eu.hithredin.bohurt.mapper

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.kittinunf.fuel.httpGet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import eu.hithredin.bohurt.mapper.modules.events.EventData
import kotlinx.android.synthetic.main.activity_home.*
import mu.KLogging


/**
 * All feature will be quickly dev here: testing kotlin stuff
 * Clean archi will come later
 */
class HomeActivity : AppCompatActivity() {

    val baseUrl = "https://hithredin.my.opendatasoft.com/api/records/1.0/search/?dataset=bohurt-events"
    private lateinit var googleMap: GoogleMap

    companion object : KLogging()

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
            "$baseUrl&q=+start_date_date+%3E%3D+%23now()+&rows=12&sort=-start_date_date"
                    .httpGet()
                    .responseObject(EventData.Deserializer()) { req, res, result ->
                        val (data, err) = result
                        Toast.makeText(this, "Loading end", Toast.LENGTH_SHORT).show()
                        logger.info { "Result query:\n$data \n$err" }

                        data?.data()?.forEach { event ->
                            run {
                                val point = LatLng(event.location.lat(), event.location.lon())
                                googleMap.addMarker(MarkerOptions()
                                        .position(point)
                                        .title(event.event_name))
                            }
                        }
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
