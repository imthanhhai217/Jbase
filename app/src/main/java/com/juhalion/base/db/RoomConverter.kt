package com.juhalion.base.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RoomConverter {

    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    @JvmStatic
    fun toList(json: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, listType)
    }
}