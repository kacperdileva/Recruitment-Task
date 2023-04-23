package com.miquido.data

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.miquido.data.searchDetails.searchDetails
import com.miquido.data.searchDetails.searchDetailsDao
import com.miquido.data.unsplash.UnsplashDao
import com.miquido.data.unsplash.UnsplashResponse
import com.miquido.di.ApplicationScope
import com.miquido.utils.DataConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@TypeConverters(DataConverter::class)
@Database(entities = [UnsplashResponse::class, searchDetails::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun unsplashDao(): UnsplashDao
    abstract fun searchDetailsDao(): searchDetailsDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("Room", "Database created")
            applicationScope.launch {
                database.get().searchDetailsDao().insertSearchDetails(searchDetails(page = INITIAL_PAGE_INDEX))
            }
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}