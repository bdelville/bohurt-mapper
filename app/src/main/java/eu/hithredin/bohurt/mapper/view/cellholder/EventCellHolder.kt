package eu.hithredin.bohurt.mapper.view.cellholder

import android.view.View
import android.widget.TextView
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.view.framework.recycler.ReCellHolder
import eu.hithredin.bohurt.mapper.model.event.EventData

/**
 * INSERT DOC
 */
class EventCellHolder(itemView: View) : ReCellHolder<EventData>(itemView) {

    private var titleView: TextView? = null

    override fun onClick(v: View?) {

    }

    override fun buildHolder(view: View) {
        titleView = view.findViewById(R.id.title_view)
    }

    override fun fillCell() {
        titleView?.text = data?.event_name
    }

}