package com.example.task.ui

import android.view.View
import com.example.task.data.model.ListData

public interface OnRvListItemClickListener {
    public fun onItemClick(view : View?, itemSelectedPosition : Int, itemSelected: ListData, showNoDataText : Boolean)
}