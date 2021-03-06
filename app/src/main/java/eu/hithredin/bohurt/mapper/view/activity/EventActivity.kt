package eu.hithredin.bohurt.mapper.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import eu.hithredin.bohurt.common.data.EventData
import eu.hithredin.bohurt.common.utils.DATA_KEY
import eu.hithredin.bohurt.common.utils.DATA_TITLE
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.view.fragment.EventFragment
import eu.hithredin.bohurt.mapper.view.framework.BaseActivity
import kotlinx.android.synthetic.main.activity_base_fragment.*

/**
 * Details page of the Event
 */
class EventActivity : BaseActivity() {

    companion object {
        fun startActivity(context: Activity, event: EventData) {
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra(DATA_KEY, event)
            intent.putExtra(DATA_TITLE, event.event_name)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_fragment)
        setFragment(EventFragment.startFragment(intent.extras))
        title = intent.getStringExtra(DATA_TITLE)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
