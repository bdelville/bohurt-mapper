package eu.hithredin.bohurt.mapper.modules

import android.app.Application

import com.squareup.leakcanary.LeakCanary

class ButhurtApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }
}