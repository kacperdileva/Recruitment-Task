package com.miquido.data.unsplash

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unsplash_response")
data class UnsplashResponse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val results: List<UnsplashPhoto>
)