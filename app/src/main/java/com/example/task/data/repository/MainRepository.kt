package com.example.task.data.repository

import retrofit2.http.Query

class MainRepository constructor(private val retrofitService: RetrofitService){
    fun getAllListDatas() = retrofitService.getAllListDatas()
    fun getDirectionRoutes(origin: String, destination: String,apiKey: String) = retrofitService.getDirectionRoutes(origin, destination, apiKey)
}