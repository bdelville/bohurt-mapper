package eu.hithredin.bohurt.mapper.view.framework

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
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
     */
    protected fun setFragment(fragment: Fragment) {
        if (fragment.arguments == null) fragment.arguments = Bundle()
        fragment.arguments.putAll(intent.extras)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_layout, fragment, "MainFragment")
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Respond to the action bar's Up/Home button
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}