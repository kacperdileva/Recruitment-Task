package com.miquido.data.searchDetails

import com.miquido.data.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class searchDetailsRepository @Inject constructor(private val database: AppDatabase) {
    private val dao = database.searchDetailsDao()

    suspend fun insertToDatabase(searchDetails: searchDetails) = dao.insertSearchDetails(searchDetails)

    suspend fun getDetailsFromDatabase() = dao.getSearchDetails()

    suspend fun deleteDetailsFromDatabase() = dao.deleteSearchDetails()
}