package com.example.task.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.task.data.model.DirectionResponses
import com.example.task.data.repository.MainRepository
import com.example.task.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewModel constructor(private val repository: MainRepository)  : ViewModel(){
    val mDirectionData = MutableLiveData<Resource<DirectionResponses>>()

    fun fetchData(origin: String, destination: String,apiKey: String) {
        mDirectionData.postValue(Resource.loading(null))
        val response = repository.getDirectionRoutes(origin, destination, apiKey)
        response.enqueue(object : Callback<DirectionResponses>{
            override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                mDirectionData.postValue(Resource.success(response.body()))
            }

            override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                mDirectionData.postValue(Resource.error(t.message.toString(), null))
            }
        })
    }
    fun getDirectionRoutes(): LiveData<Resource<DirectionResponses>> {
        return mDirectionData
    }



}