package eu.hithredin.bohurt.mapper.view.framework

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View

/**
 * INSERT DOC
 */
abstract class BaseFragment : Fragment() {

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
        return activity.getString(id)
    }
}

