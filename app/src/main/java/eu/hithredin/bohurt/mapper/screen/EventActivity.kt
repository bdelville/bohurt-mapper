package eu.hithredin.bohurt.mapper.screen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import eu.hithredin.bohurt.mapper.R
import eu.hithredin.bohurt.mapper.app.DATA_KEY
import eu.hithredin.bohurt.mapper.modules.event.EventData

fun openEventActivity(activity: Activity, event: EventData){
    val intent = Intent(activity, EventActivity::class.java)
    intent.putExtra(DATA_KEY, event.timestamp)
    activity.startActivity(intent)
}

/**
 * INSERT DOC
 */
class EventActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_fragment)

        val fragment = EventFragment()
        fragment.arguments = intent.extras
        setFragment(fragment, true)
    }
}
