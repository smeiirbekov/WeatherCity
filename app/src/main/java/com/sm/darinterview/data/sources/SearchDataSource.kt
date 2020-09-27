package com.sm.darinterview.data.sources

import com.sm.darinterview.data.models.City

interface SearchDataSource {

    suspend fun getCityWeatherList(term: String): List<City>?

}