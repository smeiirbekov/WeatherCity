package com.sm.darinterview.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Search")
data class Search(
    @PrimaryKey
    val term: String,
    val predictions: List<String>,
    val createDate: Long?
)