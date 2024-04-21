package com.juhalion.base.mvvm.models.response


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String
)