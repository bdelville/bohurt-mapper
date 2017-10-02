package eu.hithredin.bohurt.mapper.view.framework

/**
 * INSERT DOC
 */

interface BaseLoadView {
    fun showEmpty()
    fun showError(title: String, message: String = "", showReload: Boolean = false)
    fun clearEvents()
    fun hideError()
}