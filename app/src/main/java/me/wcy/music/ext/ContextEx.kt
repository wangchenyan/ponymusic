package me.wcy.music.ext

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import dagger.hilt.android.EntryPointAccessors

/**
 * Created by wangchenyan.top on 2023/7/12.
 */

inline fun <reified T : Any> Application.accessEntryPoint(): T {
    return EntryPointAccessors.fromApplication(this, T::class.java)
}

fun Context.registerReceiverCompat(receiver: BroadcastReceiver, filter: IntentFilter) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
    } else {
        registerReceiver(receiver, filter)
    }
}
