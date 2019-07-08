package eu.hithredin.bohurt.common

import android.app.Application
import com.github.kittinunf.fuse.core.Fuse
import org.kodein.di.Kodein
import eu.hithredin.bohurt.common.data.EventData
import eu.hithredin.ktopendatasoft.ApiLoader
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

/**
 * Declare Libraries (OpenDataSoft) modules here, to be injected in Application
 */
val libModule = Kodein.Module("common") {
    bind<ApiLoader<EventData>>() with singleton {
        ApiLoader("hithredin", EventData.Deserializer())
    }
}

/**
 * Init all the feature needed for the common module
 */
fun initBohurtLib(app: Application){
    Fuse.init(app.cacheDir.path)
}