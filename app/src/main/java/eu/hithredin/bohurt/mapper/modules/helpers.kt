package eu.hithredin.bohurt.mapper.modules

import com.google.gson.reflect.TypeToken
import eu.hithredin.bohurt.mapper.modules.events.EventData
import eu.hithredin.bohurt.mapper.modules.opendatasoft.ListResult

/**
 * INSERT DOC
 */

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type


