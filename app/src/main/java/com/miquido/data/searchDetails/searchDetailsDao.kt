package com.miquido.data.searchDetails

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface searchDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchDetails(searchDetails: searchDetails)

    @Query("SELECT * FROM search")
    suspend fun getSearchDetails(): searchDetails?

    @Query("DELETE FROM search")
    suspend fun deleteSearchDetails()
}