package eu.hithredin.bohurt.mapper.app

import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.lazy
import com.squareup.leakcanary.LeakCanary
import eu.hithredin.bohurt.common.initBohurtLib
import eu.hithredin.bohurt.common.libModule

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
