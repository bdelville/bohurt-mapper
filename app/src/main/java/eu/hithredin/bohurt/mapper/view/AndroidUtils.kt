package eu.hithredin.bohurt.mapper.view

import android.support.constraint.Group
import android.view.View

/**
 * INSERT DOC
 */
fun Group.addOnClickListener(listener: View.OnClickListener) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}