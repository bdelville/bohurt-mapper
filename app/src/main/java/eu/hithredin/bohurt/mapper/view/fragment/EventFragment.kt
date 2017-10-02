package eu.hithredin.bohurt.mapper.view.fragment

import android.view.View
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.app.DATA_KEY
import eu.hithredin.bohurt.mapper.view.framework.recycler.BaseRecyclerFragment
import eu.hithredin.bohurt.mapper.view.framework.recycler.ReCellHolder
import eu.hithredin.bohurt.mapper.view.framework.recycler.RecyclerCellController
import eu.hithredin.bohurt.mapper.model.event.EventData
import eu.hithredin.bohurt.mapper.view.cellholder.EventCellHolder

/**
 * UI for the content of a Event
 */
class EventFragment : BaseRecyclerFragment<EventData>() {
    override fun buildCellController(): RecyclerCellController<EventData> {
        return object : RecyclerCellController<EventData>() {
            override fun getLayoutId(holderType: Int): Int {
                return R.layout.cell_event
            }

            override fun getHolder(holderType: Int, view: View): ReCellHolder<EventData> {
                return EventCellHolder(view)
            }
        }
    }

    override fun loadListQuery() {
        eventSelected = arguments.getSerializable(DATA_KEY) as EventData
        adapter.addData(eventSelected)
        adapter.notifyDataSetChanged()
    }

    lateinit var eventSelected: EventData

    override fun fragmentLayout(): Int{
        return R.layout.fragment_event
    }
}
