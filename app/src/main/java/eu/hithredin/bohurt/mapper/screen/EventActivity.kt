package eu.hithredin.bohurt.mapper.screen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.app.DATA_KEY
import eu.hithredin.bohurt.mapper.framework.BaseActivity
import eu.hithredin.bohurt.mapper.modules.event.EventData

/**
 * Details page of the Event
 */
class EventActivity : BaseActivity() {

    companion object {
        fun startActivity(context: Activity, event: EventData) {
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra(DATA_KEY, event)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_fragment)

        val fragment = EventFragment()
        setFragment(fragment)
    }
}
