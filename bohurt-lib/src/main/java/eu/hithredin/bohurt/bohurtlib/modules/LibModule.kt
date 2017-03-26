package eu.hithredin.bohurt.bohurtlib.modules

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import eu.hithredin.bohurt.bohurtlib.modules.events.EventData
import eu.hithredin.bohurt.bohurtlib.modules.opendatasoft.ApiLoader

/**
 * Declare all Lib modules here, to be injected in Application
 */
val libModule = Kodein.Module {
    bind<ApiLoader<EventData>>() with singleton {
        ApiLoader("hithredin", EventData.Deserializer())
    }
}
