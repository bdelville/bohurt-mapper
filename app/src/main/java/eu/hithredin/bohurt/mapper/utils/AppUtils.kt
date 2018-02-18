package eu.hithredin.bohurt.mapper.utils

import com.google.android.gms.maps.model.LatLng
import eu.hithredin.ktopendatasoft.GpsLocation

/**
 * INSERT DOC
 */
fun GpsLocation.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}