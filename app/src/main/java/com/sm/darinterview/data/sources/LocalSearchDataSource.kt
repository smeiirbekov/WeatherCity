package com.sm.darinterview.data.sources

import com.sm.darinterview.data.cacheTtl
import com.sm.darinterview.data.db.CityDb
import com.sm.darinterview.data.models.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class LocalSearchDataSource @Inject constructor(
    private val db: CityDb
): SearchDataSource {
    override suspend fun getCityWeatherList(term: String): List<City>? {
        return withContext(Dispatchers.IO) {
            val search = db.searches().getItems(term)
            db.cities().getItems(Date().time - cacheTtl, search.predictions)
        }
    }
}