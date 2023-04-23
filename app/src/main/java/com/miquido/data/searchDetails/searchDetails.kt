package com.miquido.data.searchDetails

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search")
data class searchDetails(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val page: Int,
    val query:String ="Cats"
)
