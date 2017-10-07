package eu.hithredin.bohurt.mapper.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Open the browser to an URL
 * @param ctx
 * @param url
 */
fun openUrlLink(ctx: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    ctx.startActivity(intent)
}