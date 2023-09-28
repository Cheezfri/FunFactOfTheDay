package com.example.funfactoftheday.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.database.reletions.CategoryModelCrossRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Database(
    entities = [
        CategoryModel::class,
        FactModel::class,
        CategoryModelCrossRef::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao():AppDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    database.populateDatabase(database.appDao())
                }
            }
        }
    }

    suspend fun populateDatabase(appDao: AppDao) {
        // Delete all content here
        Timber.e("Database Populated")
        appDao.deleteAll()

        val testFacts = mutableListOf(
            FactModel( "The World Is Flat", true),
            FactModel( "Lebron James is a goat", true),
            FactModel( "Ryan Gosling to HOT", false),
            FactModel( "Lebron James is 3 Feet and 2 Inches", false),
            FactModel( "Dabbington City Shall We?", true)
        )

        val testCategories = mutableListOf(
            CategoryModel("Sports"),
            CategoryModel("Science"),
            CategoryModel("Entertainment")
        )

        val factCategoryRelations = mutableListOf(
            CategoryModelCrossRef("The World Is Flat", "Science"),
            CategoryModelCrossRef("Lebron James is a goat", "Sports"),
            CategoryModelCrossRef("Lebron James is a goat", "Entertainment"),
            CategoryModelCrossRef("Ryan Gosling to HOT", "Entertainment"),
            CategoryModelCrossRef("Lebron James is 3 Feet and 2 Inches", "Sports"),
            CategoryModelCrossRef("Dabbington City Shall We", "Entertainment")
        )

        testFacts.forEach{appDao.insertFact(it)}
        testCategories.forEach{appDao.insertCategory(it)}
        factCategoryRelations.forEach{appDao.insertCategoryModelCrossRef(it)}

    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
                return INSTANCE ?: synchronized(this){
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_db"
                    )
                        .addCallback(AppDatabaseCallback(scope))
                        .build()
                    INSTANCE = instance
                    instance
                }
        }
    }
}