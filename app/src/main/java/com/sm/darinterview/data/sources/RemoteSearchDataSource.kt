package com.sm.darinterview.data.sources

import com.sm.darinterview.api.SearchApi
import com.sm.darinterview.data.db.CityDb
import com.sm.darinterview.data.models.City
import com.sm.darinterview.data.models.Search
import com.sm.darinterview.di.PlacesApi
import com.sm.darinterview.di.WeatherApi
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class RemoteSearchDataSource @Inject constructor(
    @WeatherApi private val weatherApi: SearchApi,
    @PlacesApi private val placesApi: SearchApi,
    private val db: CityDb
): SearchDataSource {
    override suspend fun getCityWeatherList(term: String): List<City> {
        return withContext(Dispatchers.IO) {
            val list = mutableListOf<City>()
            val placesResult = placesApi.getCityPredictions(term)
            placesResult?.predictions?.let {
                db.searches().insertItem(Search(term, it, Date().time))
                /*
                * Sequential flow
                * */
                it.forEach {
                    try {
                        val weatherResult = weatherApi.getWeather(it)
                        weatherResult?.let {
                            list.add(it)
                            db.cities().insertItem(it)
                        }
                    } catch (ignored: Exception){}
                }
            }
            list
        }
    }
}