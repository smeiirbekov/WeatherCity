package com.sm.darinterview.data.db

import android.text.TextUtils
import androidx.room.TypeConverter

object Converters {

    @TypeConverter
    @JvmStatic
    fun strToList(str: String): List<String> = str.split(",")

    @TypeConverter
    @JvmStatic
    fun listToString(list: List<String>): String = TextUtils.join(",", list)

}