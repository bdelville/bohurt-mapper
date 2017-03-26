package eu.hithredin.bohurt.mapper.app

import android.app.Application
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.lazy
import com.squareup.leakcanary.LeakCanary
import eu.hithredin.bohurt.bohurtlib.modules.initBohurtLib
import eu.hithredin.bohurt.bohurtlib.modules.libModule

class BohurtApp : Application(), KodeinAware {
    override val kodein by Kodein.lazy {
        import(libModule)
        import(appModule)
    }

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
        initBohurtLib(this)
    }
}
