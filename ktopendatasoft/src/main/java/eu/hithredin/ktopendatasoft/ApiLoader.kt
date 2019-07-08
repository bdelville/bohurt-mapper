package eu.hithredin.ktopendatasoft

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.rx.rxResponseObjectPair
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

    fun queryList(query: ApiQuery): Single<Pair<Response, ListResult<T>>> {
        var url = baseUrl
        for ((k, v) in query.buildParams()) {
            url += "$k=$v&"
        }

        // TODO Cache
        return Fuel.get(url).rxResponseObjectPair(deserializer)
    }
}