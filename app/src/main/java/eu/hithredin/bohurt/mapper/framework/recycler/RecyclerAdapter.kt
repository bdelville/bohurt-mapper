package eu.hithredin.bohurt.mapper.framework.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import java.util.*

/**
 * Custom Generic adapter for RecyclerView.
 * Abstract the type of cells resolution, inflate layout, while keeping a dynamic management (not everything is declared)
 */
class RecyclerAdapter<T> : RecyclerView.Adapter<ReCellHolder<T>>() {

    /**
     * Cell builder used to override all the cell construction methods
     */
    private lateinit var cellController: RecyclerCellController<T>

    /**
     * The items that have to be displayed in the list
     */
    private val items: MutableList<T> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ReCellHolder<T> {
        val layout = cellController.getLayoutId(viewType)
        val view: View

        if (layout > 0) {
            val inflater = LayoutInflater.from(viewGroup.context)
            view = inflater.inflate(layout, viewGroup, false)
        } else {
            view = RelativeLayout(viewGroup.context)
        }

        val holder = cellController.getHolder(viewType, view)
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        val data = getItem(position)
        return cellController.getHolderType(position, data)
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ReCellHolder<T>, position: Int) {
        val data = getItem(position)
        holder.initData(data, position)
        holder.fillCell()
        cellController.onCellFilled(position, holder.view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): T {
        return items[position]
    }

    fun setCellController(cellController: RecyclerCellController<T>) {
        this.cellController = cellController
    }

    fun addDatas(datas: List<T>) {
        val newPosition = items.size
        items.addAll(datas)
        notifyItemRangeInserted(newPosition, datas.size)
    }

    fun addData(data: T) {
        val newPosition = items.size
        items.add(data)
        notifyItemRangeInserted(newPosition, 1)
    }

    fun clear() {
        val lastSize = items.size
        items.clear()
        notifyItemRangeRemoved(0, lastSize)
    }

    fun showNextLoader(status: Boolean) {
    }
}
