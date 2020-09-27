package com.sm.darinterview.ui.base

import com.sm.darinterview.data.models.City

sealed class Result
class Success(val data: List<City>) : Result()
object Empty : Result()
class Error(val exception: Throwable) : Result()