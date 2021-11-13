package com.example.task.data.model

import com.google.gson.annotations.SerializedName

data class ListData(
    @SerializedName("userId")
    val userId: Int = 0,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("body")
    val body: String = "",
    var isSelected: Boolean = false
)
