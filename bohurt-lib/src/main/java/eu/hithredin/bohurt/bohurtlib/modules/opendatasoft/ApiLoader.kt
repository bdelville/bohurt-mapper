package eu.hithredin.bohurt.bohurtlib.modules.opendatasoft

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import mu.KotlinLogging

/**
 * OpenDataSoft's API client
 */
open class ApiLoader<T : Any>(
        val domain: String,
        val deserializer: ResponseDeserializable<ListResult<T>>) {
    protected var baseUrl: String = "https://$domain.my.opendatasoft.com/api/records/1.0/search/?"
    private val logger = KotlinLogging.logger {}

    fun queryList(query: ApiQuery, result: (Request, Response, Result<ListResult<T>, FuelError>) -> Unit) {
        var url = baseUrl
        for ((k, v) in query.buildParams()) {
            url += "$k=$v&"
        }
        logger.info { "Loading:$url" }
        url.httpGet()
                .responseObject(deserializer, result)
    }
}