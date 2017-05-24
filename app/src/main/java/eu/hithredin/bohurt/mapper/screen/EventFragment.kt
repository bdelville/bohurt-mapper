package eu.hithredin.bohurt.mapper.screen

import android.os.Bundle
import android.view.View
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.app.DATA_KEY
import eu.hithredin.bohurt.mapper.framework.recycler.BaseRecyclerFragment
import eu.hithredin.bohurt.mapper.framework.recycler.ReCellHolder
import eu.hithredin.bohurt.mapper.framework.recycler.RecyclerCellController
import eu.hithredin.bohurt.mapper.modules.event.EventData

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

    }

    lateinit var eventSelected: EventData

    override fun fragmentLayout(): Int{
        return R.layout.fragment_event
    }

    override fun populateViews(savedInstanceState: Bundle?) {
        super.populateViews(savedInstanceState)
        eventSelected = arguments.getSerializable(DATA_KEY) as EventData

        adapter.addData(eventSelected)
    }
}
