package com.juhalion.base.mvvm.models.response.comments


import com.google.gson.annotations.SerializedName
import com.juhalion.base.mvvm.models.response.User

data class Comment(
    @SerializedName("body")
    val body: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("user")
    val user: User
)