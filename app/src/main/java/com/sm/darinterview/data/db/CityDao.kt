package com.sm.darinterview.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sm.darinterview.data.models.City

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: City)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<City>)

    @Query("DELETE FROM CityWeather WHERE createDate < :date")
    suspend fun deleteExpiredItems(date: Long)

//    @Query("SELECT * FROM CityWeather WHERE createDate >= :date AND city LIKE (:term || '%')")
//    suspend fun getItems(date: Long, term: String): List<City>

    @Query("SELECT * FROM CityWeather WHERE createDate >= :date AND city IN (:list)")
    suspend fun getItems(date: Long, list: List<String>): List<City>

}
