package com.sm.darinterview.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sm.darinterview.data.models.City
import com.sm.darinterview.data.models.Search

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Search)

    @Query("SELECT * FROM Search WHERE term = :term")
    suspend fun getItems(term: String): Search

    @Query("DELETE FROM Search WHERE createDate < :date")
    suspend fun deleteExpiredItems(date: Long)
}
