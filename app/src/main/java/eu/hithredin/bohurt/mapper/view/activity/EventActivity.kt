package eu.hithredin.bohurt.mapper.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.app.DATA_KEY
import eu.hithredin.bohurt.mapper.model.event.EventData
import eu.hithredin.bohurt.mapper.view.fragment.EventFragment
import eu.hithredin.bohurt.mapper.view.framework.BaseActivity

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

        setFragment(EventFragment.startFragment(intent.extras))
    }
}
