package com.miquido.data.unsplash

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UnsplashDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(response: UnsplashResponse)

    @Query("SELECT * FROM unsplash_response")
    suspend fun getResponse(): UnsplashResponse?

    @Query("DELETE FROM unsplash_response")
    suspend fun deleteResponse()
}