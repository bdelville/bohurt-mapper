package eu.hithredin.bohurt.mapper.view.framework.recycler

import android.view.View
import java.io.Serializable

/**
 * A model very easily usable by the RecyclerAdapter
 */
abstract class FastAdaptModel : Serializable {
    var layoutId: Int = 0
    private var action: Runnable? = null
    private var holderClass: Class<FastAdaptModel>
    // todo read generics in kotlin https://kotlinlang.org/docs/reference/generics.html

    constructor(layoutId: Int, holderClass: Class<FastAdaptModel>) {
        this.layoutId = layoutId
        this.holderClass = holderClass
    }

    constructor(layoutId: Int, holderClass: Class<FastAdaptModel>, action: Runnable) {
        this.layoutId = layoutId
        this.action = action
        this.holderClass = holderClass
    }

    fun runAction() {
        action?.run()
    }

    fun getHolder(itemView: View): ReCellHolder<FastAdaptModel> {
        val c = holderClass.getConstructor(View::class.java)
        return c?.newInstance(itemView) as ReCellHolder<FastAdaptModel>
    }

    class FastAdaptCellController<T : FastAdaptModel>(private val adapter: RecyclerAdapter<T>) : RecyclerCellController<T>() {

        override fun getLayoutId(holderType: Int): Int {
            return adapter.getItem(holderType).layoutId
        }

        override fun getHolder(holderType: Int, view: View): ReCellHolder<T> {
            return adapter.getItem(holderType).getHolder(view) as ReCellHolder<T>
        }

        override fun getHolderType(position: Int, data: T): Int {
            return position
        }
    }
}