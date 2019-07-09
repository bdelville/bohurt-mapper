package eu.hithredin.bohurt.common.utils

import com.squareup.moshi.Moshi

const val DATA_KEY = "data-key"
const val DATA_TITLE = "data-title"

val moshi by lazy {
    Moshi.Builder().build()
}