package eu.hithredin.bohurt.mapper.view.fragment

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import eu.hithredin.bohurt.common.data.EventData
import eu.hithredin.bohurt.common.utils.DATA_KEY
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.utils.openUrlLink
import eu.hithredin.bohurt.mapper.view.framework.BaseFragment
import kotlinx.android.synthetic.main.fragment_event.*

/**
 * UI for the content of a Event
 */
class EventFragment : BaseFragment() {
    private lateinit var event: EventData

    companion object {
        fun startFragment(extras: Bundle): EventFragment {
            val fragment = EventFragment()
            if (!extras.containsKey(DATA_KEY))
                throw IllegalArgumentException("Extras must contains an Event object")
            fragment.arguments = extras
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_event, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateViews(savedInstanceState)
    }

    override fun populateViews(savedInstanceState: Bundle?) {
        super.populateViews(savedInstanceState)
        event = arguments!!.get(DATA_KEY) as EventData

        text_name.text = event.event_name
        text_city.text = event.city
        text_country.text = event.country
        text_date.text = DateFormat.getLongDateFormat(context).format(event.date)

        var description = if (event.fight_categories.isNotEmpty()) "${event.fight_categories}\n\n" else ""
        description += if (event.description.isNotEmpty()) "${event.description}\n\n" else ""
        text_details.text = description

        if(event.link.isNotEmpty()) {
            btn_details.visibility = VISIBLE
            btn_details.setOnClickListener { openUrlLink(context!!, event.link) }
        } else {
            btn_details.visibility = GONE
        }
    }
}
