package com.juhalion.base.models.comment.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class User(
    @PrimaryKey @SerializedName("id") val id: Int, @SerializedName("username") val username: String
)