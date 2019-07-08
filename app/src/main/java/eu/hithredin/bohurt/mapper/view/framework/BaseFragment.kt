package eu.hithredin.bohurt.mapper.view.framework

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import eu.hithredin.bohurt.common.mvp.presenter.Presenter

/**
 * INSERT DOC
 */
abstract class BaseFragment : Fragment() {
    private val presenters: MutableList<Lazy<Presenter>> = ArrayList()

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
     * Extract the Views from the layout
     */
    protected open fun assignViews(root: View) {

    }

    /**
     * Set the data to the view
     */
    protected open fun populateViews(savedInstanceState: Bundle?) {

    }

    fun string(id: Int): String {
        return activity!!.getString(id)
    }
}

