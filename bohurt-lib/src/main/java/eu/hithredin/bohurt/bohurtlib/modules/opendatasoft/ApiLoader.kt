package eu.hithredin.bohurt.bohurtlib.modules.opendatasoft

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuse.core.Fuse
import com.github.kittinunf.fuse.core.fetch.get
import com.github.kittinunf.result.Result
import mu.KotlinLogging
import java.net.URL

/**
 * OpenDataSoft's API client
 */
open class ApiLoader<T : Any>(
        domain: String,
        val deserializer: ResponseDeserializable<ListResult<T>>) {
    protected var baseUrl: String = "https://$domain.my.opendatasoft.com/api/records/1.0/search/?"
    private val logger = KotlinLogging.logger {}

    fun queryList(query: ApiQuery, apiResult: (Request, Response, Result<ListResult<T>, FuelError>) -> Unit) {
        var url = baseUrl
        for ((k, v) in query.buildParams()) {
            url += "$k=$v&"
        }
        cacheGet(url, apiResult)
    }

    private fun cacheGet(url: String, apiResult: (Request, Response, Result<ListResult<T>, FuelError>) -> Unit) {
        Fuse.bytesCache.get(URL(url)) { result ->
            result.fold({
                var response = Response().apply { data = it }
                response.httpStatusCode = 200
                val parsed = Result.of { deserializer.deserialize(response)}
                parsed.fold({
                    logger.info { "Loaded from cache:$url" }
                    apiResult.invoke(Request(), response, Result.Success(it))
                }, {
                    httpGet(url, apiResult)
                })

            }, {
                it.printStackTrace()
                httpGet(url, apiResult)
            })
        }
    }

    private fun httpGet(url: String, apiResult: (Request, Response, Result<ListResult<T>, FuelError>) -> Unit) {
        logger.info { "Loading: $url" }
        url.httpGet()
                .response { request, response, result ->
                    result.fold({
                        val parsed = Result.of { deserializer.deserialize(response) }
                        parsed.fold({
                            Fuse.bytesCache.put(url, result.get())
                            apiResult.invoke(Request(), response, Result.Success(it))
                        }, {
                            logger.debug { "parse error: $url" }
                            apiResult.invoke(Request(), response, Result.Failure(FuelError().apply { exception = it }))
                        })
                    }, {
                        logger.debug { "error: $url" }
                        apiResult.invoke(request, response, Result.Failure(it))
                    })
                }
    }
}