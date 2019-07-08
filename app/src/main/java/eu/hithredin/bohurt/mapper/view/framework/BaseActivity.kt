package eu.hithredin.bohurt.mapper.view.framework

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import eu.hithredin.bohurt.common.mvp.presenter.Presenter
import eu.hithredin.bohurt.mapper.R

/**
 * Functionality needed by all activities are declared here
 */
abstract class BaseActivity : AppCompatActivity() {

    // Allow Composition of Presenters for a View
    private val presenters: MutableList<Lazy<Presenter>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // (applicationContext as BohurtApp).kodein.inject((application as BohurtApp).kodein)
    }

    override fun onStart() {
        super.onStart()
        presenters.forEach { it.value.screenOpen() }
    }

    override fun onStop() {
        presenters.forEach { it.value.screenClose() }
        super.onStop()
    }

    protected fun <T : Presenter> loadPresenter(init: () -> T) = lazy(init)
        .also { presenters += it }

    /**
     * Set the main LayoutFragment to the activity. Can be overridden for custom animations
     */
    protected fun setFragment(fragment: Fragment) {
        if (fragment.arguments == null) fragment.arguments = Bundle()
        fragment.arguments!!.putAll(intent.extras)

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