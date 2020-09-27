package com.sm.darinterview.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CityWeather")
data class City(
    @PrimaryKey
    val city: String,
    val temperature: Double?,
    val createDate: Long?
)