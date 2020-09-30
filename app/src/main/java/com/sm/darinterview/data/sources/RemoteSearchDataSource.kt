package com.sm.darinterview.data.sources

import com.sm.darinterview.api.SearchApi
import com.sm.darinterview.data.db.CityDb
import com.sm.darinterview.data.models.City
import com.sm.darinterview.data.models.Search
import com.sm.darinterview.di.PlacesApi
import com.sm.darinterview.di.WeatherApi
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
            supervisorScope {
                val list = mutableListOf<City>()
                val mutex = Mutex()
                val placesResult = placesApi.getCityPredictions(term)
                placesResult?.predictions?.let { predictions ->
                    db.searches().insertItem(Search(term, predictions, Date().time))
                    val deferredList = ArrayList<Deferred<*>>()
                    predictions.forEach { prediction ->
                        deferredList.add( async {
                            try {
                                val weatherResult = weatherApi.getWeather(prediction)
                                weatherResult?.let { city ->
                                    mutex.withLock {
                                        list.add(city)
                                        db.cities().insertItem(city)
                                    }
                                }
                            } catch (ignored: Exception){}
                            // Exception server: nothing found
                            // Exception connection error
                        })
                    }
                    deferredList.joinAll()
                }
                list
            }
        }
    }
}