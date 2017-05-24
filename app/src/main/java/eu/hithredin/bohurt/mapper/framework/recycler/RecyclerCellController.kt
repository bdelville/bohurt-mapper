package eu.hithredin.bohurt.mapper.framework.recycler

import android.view.View

/**
 * Controller used by a view to handle the cell of the recyclerView
 */
abstract class RecyclerCellController<T> {

    /**

     * @param holderType
     * *
     * @return the id of the layout for this type
     */
    abstract fun getLayoutId(holderType: Int): Int

    /**

     * @param holderType
     * *
     * @param view
     * *
     * @return a new CellHolder instance for this type
     */
    abstract fun getHolder(holderType: Int, view: View): ReCellHolder<T>

    /**
     * By default only one cell type
     * @param position
     * *
     * @param data
     * *
     * @return Type of CellHolder to use, most important
     */
    open fun getHolderType(position: Int, data: T): Int {
        return 0
    }

    /**
     * Invoked when the cell is filled with datas
     * @param position
     * *
     * @param cell
     */
    fun onCellFilled(position: Int, cell: View) {

    }
}
