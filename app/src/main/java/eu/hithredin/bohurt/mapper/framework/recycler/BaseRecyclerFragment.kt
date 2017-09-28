package eu.hithredin.bohurt.mapper.framework.recycler

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.framework.BaseLoadFragment

/**
 * Functional Fragment based on a RecyclerView
 * Help to query data, used the RecyclerView, Manage error/reload
 */
abstract class BaseRecyclerFragment<T> : BaseLoadFragment() {

    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    protected lateinit var adapter: RecyclerAdapter<T>

    /**
     * Define you cells datas, layout, etc...
     * @return
     */
    protected abstract fun buildCellController(): RecyclerCellController<T>

    override fun assignViews(root: View) {
        super.assignViews(root)
        recyclerView = root.findViewById(R.id.recyclerview)
    }

    override fun populateViews(savedInstanceState: Bundle?) {
        super.populateViews(savedInstanceState)
        adapter = buildAdapter()
        adapter.setCellController(buildCellController())
        layoutManager = buildRecyclerLayoutManager()
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter
    }

    /**
     * Override for a specific adapter
     */
    protected fun buildAdapter(): RecyclerAdapter<T> {
        return RecyclerAdapter()
    }

    /**
     * Use your own layout, or fragment_recycler_refresh_base to enable SwipeRefresh
     * @return the layout id for this fragment
     */
    protected abstract fun fragmentLayout(): Int

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater!!.inflate(fragmentLayout(), container, false)
        assignViews(root)
        populateViews(savedInstanceState)
        cleanList()
        loadListQuery()
        return root
    }

    /**
     * Override to force a LayoutManager for the recyclerView
     */
    protected fun buildRecyclerLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(activity)
    }

    fun onRefresh() {
        cleanList()
        loadListQuery()
    }

    protected fun cleanList() {
        adapter.clear()
        setIsLoading(false)
    }

    /**
     * Call when you can start/continue the data query
     */
    protected abstract fun loadListQuery()

    /**
     * A generic function to handle error and fill the recyclerview
     */
    fun queryListFinished(idQuery: Int, datas: List<T>?) {
        setIsLoading(false)

        if (datas != null) {
            if (datas.isEmpty()) {
                // List empty
                if (!onQueryResultEmpty(adapter.itemCount == 0)) {
                    adapter.showNextLoader(false)
                }
            } else {
                //List is ok
                onListQueryResultSuccess(datas)
            }
        } else {
            onListQueryError()
        }
    }

    protected fun onListQueryResultSuccess(datas: List<T>) {
        adapter.addDatas(datas)
    }

    protected fun onListQueryError() {
        adapter.showNextLoader(false)
        if (adapter.itemCount == 0) {
            super.onQueryError()
        }
    }

    /**
     * @return true to prevent default empty behaviour
     */
    protected fun onQueryResultEmpty(listIsEmpty: Boolean): Boolean {
        if (listIsEmpty) {
            showEmpty()
        }
        return false
    }
}
