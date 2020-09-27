package com.sm.darinterview.data.db

import android.content.Context
import androidx.room.*
import com.sm.darinterview.data.models.City
import com.sm.darinterview.data.models.Search

@Database(
    entities = [City::class, Search::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CityDb : RoomDatabase() {
    companion object {
        fun create(context: Context): CityDb {
            return Room.databaseBuilder(context, CityDb::class.java, "city_weather.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun cities(): CityDao
    abstract fun searches(): SearchDao
}