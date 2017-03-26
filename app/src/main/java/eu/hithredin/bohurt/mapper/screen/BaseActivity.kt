package eu.hithredin.bohurt.mapper.screen

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.salomonbrys.kodein.KodeinInjector
import eu.hithredin.bohurt.mapper.app.BohurtApp

/**
 * Functionnalities needed by all activities are declared here
 */
open class BaseActivity : AppCompatActivity() {
    protected val injector = KodeinInjector()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject((application as BohurtApp).kodein)
    }
}