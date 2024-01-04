package me.wcy.music.search

import top.wangchenyan.common.CommonApp
import top.wangchenyan.common.storage.IPreferencesFile
import top.wangchenyan.common.storage.PreferencesFile
import me.wcy.music.consts.PreferenceName

/**
 * Created by wangchenyan.top on 2023/9/21.
 */
object SearchPreference :
    IPreferencesFile by PreferencesFile(CommonApp.app, PreferenceName.SEARCH) {
    var historyKeywords by IPreferencesFile.ListProperty("history_keywords", String::class.java)
}