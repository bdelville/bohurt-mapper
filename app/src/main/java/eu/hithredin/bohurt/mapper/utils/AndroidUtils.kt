package eu.hithredin.bohurt.mapper.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.constraintlayout.widget.Group
import android.view.View

/**
 * Open the browser to an URL
 */
fun openUrlLink(ctx: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    ctx.startActivity(intent)
}

/**
 * Register a listener to all view of a Group
 */
fun Group.addOnClickListener(listener: View.OnClickListener) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}