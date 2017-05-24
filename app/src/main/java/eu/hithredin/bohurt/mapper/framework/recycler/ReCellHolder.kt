package eu.hithredin.bohurt.mapper.framework.recycler

import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.OnClickListener


/**
 * Holder of data and view for a cell of recyclerView
 */
abstract class ReCellHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {
    protected var context: Context
    var data: T? = null
        protected set
    var index: Int = 0
        protected set
    var view: View
        protected set
    var tag: Any? = null

    init {
        view = itemView
        view.setOnClickListener(this)
        context = itemView.context
        buildHolder(itemView)
    }

    fun initData(data: T, index: Int) {
        this.data = data
        this.index = index
    }

    protected abstract fun buildHolder(view: View)

    abstract fun fillCell()

    protected fun resources(): Resources {
        return view.resources
    }
}
