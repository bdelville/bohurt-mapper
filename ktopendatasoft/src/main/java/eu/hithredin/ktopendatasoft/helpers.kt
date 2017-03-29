package eu.hithredin.ktopendatasoft

import com.google.gson.reflect.TypeToken

/**
 * Unclassificable helper methods
 */

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type
