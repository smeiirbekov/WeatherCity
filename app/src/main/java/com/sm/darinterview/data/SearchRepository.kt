package com.sm.darinterview.data

import com.sm.darinterview.data.db.CityDb
import com.sm.darinterview.data.models.City
import com.sm.darinterview.data.sources.LocalSearchDataSource
import com.sm.darinterview.data.sources.RemoteSearchDataSource
import com.sm.darinterview.data.sources.SearchDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

const val cacheTtl = 60*60*60 // 1 Hour time to live

class SearchRepository @Inject constructor(
    private val local: LocalSearchDataSource,
    private val remote: RemoteSearchDataSource,
    private val db: CityDb
): SearchDataSource {

    init {
        /*
        * Delete expired cached items on start
        * */
        CoroutineScope(Dispatchers.IO).launch {
            val expDateHigher = Date().time - cacheTtl
            db.cities().deleteExpiredItems(expDateHigher)
            db.searches().deleteExpiredItems(expDateHigher)
        }
    }

    override suspend fun getCityWeatherList(term: String): List<City>? {
        return withContext(Dispatchers.IO) {
            var list: List<City>? = null
            try {
                local.getCityWeatherList(term)?.let {
                    list = it
                }
            } catch (ignored: Exception){}
            // Exception Db: nothing found
            if (list.isNullOrEmpty()) {
                list = remote.getCityWeatherList(term)
            }
            list
        }
    }

}