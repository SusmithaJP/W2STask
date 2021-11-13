package com.example.task.data.repository

import com.example.task.data.model.DirectionResponses
import com.example.task.data.model.ListData
import com.google.maps.android.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface RetrofitService {
    @GET("posts")
    fun getAllListDatas(): Call<List<ListData>>

    @GET("maps/api/directions/json")
    fun getDirectionRoutes(@Query("origin") origin: String,
                     @Query("destination") destination: String,
                     @Query("key") apiKey: String): Call<DirectionResponses>



    companion object {

        var retrofitService: RetrofitService? = null
        var retrofitMapService: RetrofitService? = null
        const val BASE_URL: String = "https://jsonplaceholder.typicode.com/"
        const val MAP_DIRECTION_BASE_URL: String = "https://maps.googleapis.com/"
        private val httpLoggerClient = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
        fun getInstance() : RetrofitService {

            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpLoggerClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }
        fun getMApInstance() : RetrofitService {

            if (retrofitMapService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(MAP_DIRECTION_BASE_URL)
                    .client(httpLoggerClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitMapService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitMapService!!
        }
    }
}