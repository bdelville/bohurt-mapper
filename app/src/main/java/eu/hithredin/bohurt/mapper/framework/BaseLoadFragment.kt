package eu.hithredin.bohurt.mapper.framework

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import eu.hithredin.bohurt.mapper.R

/**
 * Base Fragment that contains helpers needed to display loading, errors or empty statuses
 */
abstract class BaseLoadFragment : BaseFragment() {

    protected var spinnerCenterLoading: ProgressBar? = null
    protected var errorReloadAction: View? = null
    protected var errorReloadText: TextView? = null
    protected var errorTitle: TextView? = null
    protected var errorText: TextView? = null

    protected fun showIoError(showReload: Boolean) {
        showError(string(R.string.info_error_io), "", showReload)
    }

    protected fun showEmpty() {
        showError(string(R.string.info_data_empty), "", false)
    }

    @JvmOverloads protected fun showError(title: String = string(R.string.info_error), message: String = "", showReload: Boolean = false) {
        errorTitle?.visibility = View.VISIBLE
        errorTitle?.text = title
        errorText?.visibility = View.VISIBLE
        errorText?.text = message
        errorReloadAction?.visibility = if (showReload) View.VISIBLE else View.INVISIBLE
    }

    protected fun setReloadAction(reload: View.OnClickListener) {
        errorReloadAction?.setOnClickListener(reload)
    }

    /**
     * Hide the error Message
     */
    protected fun hideError() {
        errorTitle?.visibility = View.GONE
        errorText?.visibility = View.GONE
        errorReloadAction?.visibility = View.GONE
    }

    protected fun setIsLoading(status: Boolean) {
        updateLoading(0)
        spinnerCenterLoading?.visibility = if (status) View.VISIBLE else View.GONE
        spinnerCenterLoading?.isActivated = status

    }

    protected fun updateLoading(percent: Int) {
        spinnerCenterLoading?.progress = percent
    }

    override fun assignViews(root: View) {
        spinnerCenterLoading = root.findViewById(R.id.info_load_indicator)
        errorReloadAction = root.findViewById(R.id.info_reload)
        errorReloadText = root.findViewById(R.id.info_reload_text)
        errorTitle = root.findViewById(R.id.info_title)
        errorText = root.findViewById(R.id.info_text)
    }

    override fun populateViews(savedInstanceState: Bundle?) {
        spinnerCenterLoading?.max = 100
    }

    /**
     * A generic function to handle error and success of simple queries
     */
    fun queryFinished(datas: Any?) {
        setIsLoading(false)
        if (datas != null) {
            onQueryResultSuccess()
        } else {
            onQueryError()
        }
    }

    /**
     * Handle the query result
     */
    protected fun onQueryResultSuccess() {}

    /**
     * Handle the query error behaviour

     * @param resultInfo
     */
    protected fun onQueryError() {
        // Todo analyse origin of error
        showError()
    }
}
/**
 * Show the standard error message
 */
