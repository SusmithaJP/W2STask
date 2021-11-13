package com.example.task.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.task.data.repository.MainRepository
import com.example.task.ui.viewmodel.ListDataViewModel
import com.example.task.ui.viewmodel.MapViewModel

class TaskViewModelFactory constructor(private val repository: MainRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
         if (modelClass.isAssignableFrom(ListDataViewModel::class.java)) {
            return ListDataViewModel(this.repository) as T
        }else if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}