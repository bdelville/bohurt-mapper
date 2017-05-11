package eu.hithredin.bohurt.mapper.screen

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.modules.event.EventData

/**
 * UI for the content of a Event
 */
open class EventFragment : Fragment() {
    lateinit var event: EventData

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_event, container, true)
        // TODO extract the data from a local store linked with query with intelligent caching system
        return rootView
    }

}
