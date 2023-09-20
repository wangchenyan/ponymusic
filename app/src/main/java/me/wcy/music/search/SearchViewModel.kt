package me.wcy.music.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import me.wcy.common.ext.toUnMutable

/**
 * Created by wangchenyan.top on 2023/9/20.
 */
class SearchViewModel : ViewModel() {
    private val _keywords = MutableStateFlow("")
    val keywords = _keywords.toUnMutable()

    private val _showResult = MutableStateFlow(false)
    val showResult = _showResult.toUnMutable()

    fun search(keywords: String) {
        if (keywords.isEmpty()) {
            return
        }
        _keywords.value = keywords
        _showResult.value = true
    }

    fun showHistory() {
        _showResult.value = false
    }
}