package eu.hithredin.bohurt.bohurtlib.modules

import com.google.gson.reflect.TypeToken

/**
 * Unclassificable helper methods
 */

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type
