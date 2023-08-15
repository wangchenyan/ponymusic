package me.wcy.music.utils.binding

import android.app.Activity
import android.view.View

object ViewBinder {
    fun bind(activity: Activity) {
        bind(activity, activity.window.decorView)
    }

    fun bind(target: Any, source: View?) {
        val fields = target.javaClass.declaredFields
        if (fields.isNotEmpty()) {
            for (field in fields) {
                try {
                    field.isAccessible = true
                    if (field[target] != null) {
                        continue
                    }
                    val bind = field.getAnnotation(
                        Bind::class.java
                    )
                    if (bind != null) {
                        val viewId = bind.value
                        field[target] = source!!.findViewById(viewId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}