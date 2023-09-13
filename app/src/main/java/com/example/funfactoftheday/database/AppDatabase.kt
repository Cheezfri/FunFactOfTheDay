package com.example.funfactoftheday.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.database.reletions.CategoryModelCrossRef

@Database(
    entities = [
        CategoryModel::class,
        FactModel::class,
        CategoryModelCrossRef::class
    ],
    version = 1
)

abstract class AppDatabase : RoomDatabase() {

    abstract val appDao:AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}