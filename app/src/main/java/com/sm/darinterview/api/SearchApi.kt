package com.sm.darinterview.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.sm.darinterview.data.models.City
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit


const val OPEN_WEATHER_URL = "http://api.openweathermap.org/data/2.5/"
const val GOOGLE_PLACES_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/"
const val REQUEST_TIMEOUT_MS = 10000L

interface SearchApi {

    @GET("json?types=(cities)&components=country:kz")
    suspend fun getCityPredictions(@Query("input") term: String): CityPredictionsResponse?

    @GET("weather?units=metric")
    suspend fun getWeather(@Query("q") city: String): City?

    data class CityPredictionsResponse (val predictions: List<String>)

    /* Manual deserialization for nested variables instead of creating unnecessary data classes
    * with SerializedName annotations
    * */
    class CityPredictionsDeserializer: JsonDeserializer<CityPredictionsResponse> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): CityPredictionsResponse? {
            json?.asJsonObject?.get("predictions")?.asJsonArray?.let {
                val predictions = arrayListOf<String>()
                it.forEach{ arrayElement ->
                    arrayElement.asJsonObject.get("structured_formatting")?.asJsonObject?.get("main_text")?.asString?.let { s ->
                        predictions.add(s)
                    }
                }
                return CityPredictionsResponse(predictions)
            }
            return null
        }
    }

    class CityDeserializer: JsonDeserializer<City> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): City? {
            json?.asJsonObject?.get("name")?.asString?.let {
                return City(it, json.asJsonObject?.get("main")?.asJsonObject?.get("temp")?.asDouble, Date().time)
            }
            return null
        }
    }

    companion object {

        enum class ApiType {
            WEATHER,
            PLACES
        }

        fun create(type: ApiType, apiKey: String): SearchApi {
            val baseUrl: String
            val query: String
            when (type) {
                ApiType.WEATHER -> {
                    baseUrl = OPEN_WEATHER_URL
                    query = "appId"
                }
                ApiType.PLACES -> {
                    baseUrl = GOOGLE_PLACES_URL
                    query = "key"
                }
            }
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC
            val queryAppender = Interceptor { chain ->
                val request: Request = chain.request()
                val url = request.url().newBuilder().addQueryParameter(query, apiKey).build()
                chain.proceed(request.newBuilder().url(url).build())
            }
            val gson = GsonBuilder()
                .registerTypeAdapter(CityPredictionsResponse::class.java, CityPredictionsDeserializer())
                .registerTypeAdapter(City::class.java, CityDeserializer())
                .create()
            return Retrofit.Builder()
                .baseUrl(HttpUrl.parse(baseUrl)!!)
                .client(OkHttpClient.Builder()
                    .connectTimeout(REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    .addInterceptor(logger)
                    .addInterceptor(queryAppender)
                    .build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(SearchApi::class.java)
        }
    }
}