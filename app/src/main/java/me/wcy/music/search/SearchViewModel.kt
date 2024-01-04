package me.wcy.music.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import top.wangchenyan.common.ext.toUnMutable
import me.wcy.music.consts.Consts

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
class SearchViewModel : ViewModel() {
    private val _keywords = MutableStateFlow("")
    val keywords = _keywords.toUnMutable()

    private val _historyKeywords = MutableStateFlow(SearchPreference.historyKeywords ?: emptyList())
    val historyKeywords = _historyKeywords.toUnMutable()

    private val _showResult = MutableStateFlow(false)
    val showResult = _showResult.toUnMutable()

    fun search(keywords: String) {
        if (keywords.isEmpty()) {
            return
        }
        _keywords.value = keywords
        _showResult.value = true

        val list = _historyKeywords.value.toMutableList()
        list.remove(keywords)
        list.add(0, keywords)
        val realList = list.take(Consts.SEARCH_HISTORY_COUNT)
        _historyKeywords.value = realList
        viewModelScope.launch(Dispatchers.IO) {
            SearchPreference.historyKeywords = realList
        }
    }

    fun showHistory() {
        _showResult.value = false
    }
}