package eu.hithredin.bohurt.mapper.screen

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.github.salomonbrys.kodein.KodeinInjector
import eu.hithredin.bohurt.mapper.R
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

    /**
     * Set the LayoutFragment to the activity. Can be overriden for custom animations
     * @param bmf
     * *
     * @param isOnStack
     */
    protected fun setFragment(bmf: Fragment, isOnStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_layout, bmf, "MainFragment")
        transaction.commit()
    }
}