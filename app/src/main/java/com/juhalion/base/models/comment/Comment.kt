package com.juhalion.base.models.comment

import com.google.gson.annotations.SerializedName
import com.juhalion.base.models.comment.user.User

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