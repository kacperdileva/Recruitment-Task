package com.miquido.data.unsplash


import com.miquido.api.UnsplashApi
import com.miquido.data.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(
    private val api: UnsplashApi,
    private val database: AppDatabase
) {
    private val dao = database.unsplashDao()

     fun getSearchResultFromAPI(
        query: String,
        page: Int,
        perPage: Int
    ) = api.searchPhotos(
        query = query,
        page = page,
        perPage = perPage,
    )

    suspend fun getSearchFromDB() = dao.getResponse()

    suspend fun deleteResponseFromDB() = dao.deleteResponse()

    suspend fun insertResponseToDB(data: UnsplashResponse) = dao.insertResponse(response = data)
}