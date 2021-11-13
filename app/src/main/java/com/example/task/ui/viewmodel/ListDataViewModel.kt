package com.example.task.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.task.data.model.ListData
import com.example.task.data.repository.MainRepository
import com.example.task.utils.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListDataViewModel constructor(private val repository: MainRepository)  : ViewModel() {

    val mListData = MutableLiveData<Resource<List<ListData>>>()

    init {
        fetchData()
    }

    fun fetchData() {
        mListData.postValue(Resource.loading(null))
        val response = repository.getAllListDatas()
        response.enqueue(object : Callback<List<ListData>> {
            override fun onResponse(call: Call<List<ListData>>, response: Response<List<ListData>>) {
                mListData.postValue(Resource.success(response.body()))
            }
            override fun onFailure(call: Call<List<ListData>>, t: Throwable) {
                mListData.postValue(Resource.error(t.message.toString(), null))
            }
        })
    }
    fun getAllListDatas(): LiveData<Resource<List<ListData>>> {
        return mListData
    }
}