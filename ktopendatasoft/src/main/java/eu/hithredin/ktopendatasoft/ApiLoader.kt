package eu.hithredin.ktopendatasoft

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.rx.rx_responseObject
import com.github.kittinunf.result.Result
import io.reactivex.Single
import mu.KotlinLogging

/**
 * OpenDataSoft's API client
 */
open class ApiLoader<T : Any>(
        domain: String,
        private val deserializer: ResponseDeserializable<ListResult<T>>) {
    private var baseUrl: String = "https://$domain.my.opendatasoft.com/api/records/1.0/search/?"
    private val logger = KotlinLogging.logger {}

    fun queryList(query: ApiQuery): Single<Pair<Response, Result<ListResult<T>, FuelError>>> {
        var url = baseUrl
        for ((k, v) in query.buildParams()) {
            url += "$k=$v&"
        }

        // TODO Cache
        return Fuel.get(url).rx_responseObject(deserializer)
    }
}