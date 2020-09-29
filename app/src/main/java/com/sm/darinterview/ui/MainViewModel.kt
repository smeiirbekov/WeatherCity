package com.sm.darinterview.ui

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.sm.darinterview.data.SearchRepository
import com.sm.darinterview.ui.base.Empty
import com.sm.darinterview.ui.base.Success
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

private const val SEARCH_DELAY_MS = 300L
private const val MIN_QUERY_LENGTH = 2
const val QUERY = "query"

@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel @ViewModelInject constructor(
    private val searchRepository: SearchRepository,
    private val preferences: SharedPreferences
): ViewModel(){

    val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    init {
        preferences.getString(QUERY, "")?.let { viewModelScope.launch { queryChannel.send(it) } }
    }

    val results = queryChannel.asFlow().debounce(SEARCH_DELAY_MS).mapLatest {
        preferences.edit().putString(QUERY, it).apply()
        if (it.length>=MIN_QUERY_LENGTH) {
            try {
                val searchResult = withContext(Dispatchers.IO) {
                    searchRepository.getCityWeatherList(it)
                }
                if (searchResult!=null) {
                    Success(searchResult)
                } else {
                    Error()
                }
            } catch (e: Throwable) {
                if (e !is CancellationException) com.sm.darinterview.ui.base.Error(e)
                throw e
            }
        } else Empty
    }.catch { emit(Error()) }.asLiveData()

}