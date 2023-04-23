package com.miquido.api

import com.miquido.BuildConfig
import com.miquido.data.unsplash.UnsplashResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashApi {
    companion object{
        const val BASE_URL ="https://api.unsplash.com/"
        private const val CLIENT_ID = BuildConfig.UNSPLASH_ACCESS_KEY
    }

    @Headers("Accept-Version: v1", "Authorization:  Client-ID $CLIENT_ID")
    @GET("search/photos")
    fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): Call<UnsplashResponse?>
}