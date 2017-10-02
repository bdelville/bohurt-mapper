package eu.hithredin.bohurt.mapper.app

import android.app.Application
import com.github.kittinunf.fuse.core.Fuse
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import eu.hithredin.bohurt.mapper.model.event.EventData
import eu.hithredin.ktopendatasoft.ApiLoader

/**
 * Declare Libraries (OpenDataSoft) modules here, to be injected in Application
 */
val libModule = Kodein.Module {
    bind<ApiLoader<EventData>>() with singleton {
        ApiLoader("hithredin", EventData.Deserializer())
    }
}

fun initBohurtLib(app: Application){
    Fuse.init(app.cacheDir.path)
}