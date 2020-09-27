package com.sm.darinterview.di

import android.content.Context
import android.content.SharedPreferences
import com.sm.darinterview.R
import com.sm.darinterview.api.SearchApi
import com.sm.darinterview.data.db.CityDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("CityWeather", Context.MODE_PRIVATE)
    }


    /*
    * Differentiate different implementations of the same type classes with annotations
    * */
    @Singleton
    @Provides
    @WeatherApi
    fun provideWeatherApiService(@ApplicationContext context: Context): SearchApi {
        return SearchApi.create(SearchApi.Companion.ApiType.WEATHER, context.getString(R.string.open_weather_api_key))
    }

    @Singleton
    @Provides
    @PlacesApi
    fun providePlacesApiService(@ApplicationContext context: Context): SearchApi {
        return SearchApi.create(SearchApi.Companion.ApiType.PLACES, context.getString(R.string.google_places_api_key))
    }

    @Singleton
    @Provides
    fun provideCityDatabase(@ApplicationContext context: Context) = CityDb.create(context)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlacesApi
