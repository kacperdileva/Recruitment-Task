package com.miquido.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.miquido.data.unsplash.UnsplashPhoto

class DataConverter{
    @TypeConverter
    fun fromUnsplashPhotoList(value: List<UnsplashPhoto>): String {
        val gson = Gson()
        val type = object : TypeToken<List<UnsplashPhoto>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toUnsplashPhotoList(value: String): List<UnsplashPhoto> {
        val gson = Gson()
        val type = object : TypeToken<List<UnsplashPhoto>>() {}.type
        return gson.fromJson(value, type)
    }
}