package me.wcy.music.common

import android.content.Intent
import android.os.Bundle
import me.wcy.common.ui.activity.BaseActivity
import me.wcy.music.service.PlayService

/**
 * Created by wangchenyan.top on 2023/9/4.
 */
abstract class BaseMusicActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(this, PlayService::class.java))
    }
}