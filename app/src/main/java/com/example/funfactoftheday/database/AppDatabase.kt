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
            FactModel( "Gorillas can catch human colds and other illnesses."),
            FactModel( "The bat is the only mammal that can fly."),
            FactModel( "For every human in the world there are one million ants."),
            FactModel( "A tarantula spider can survive for more than two years without food."),
            FactModel( "Ants never sleep. Also they don’t have lungs."),
            FactModel( "Cats have lived with people for only 7,000 years."),
            FactModel( "We share 70% of our DNA with a slug."),

            FactModel( "Olympic gold medals are predominantly made from sterling silver."),
            FactModel( "Michael Phelps has won more olympic golds than Mexico."),
            FactModel( "Jerry West is the silhouette of the NBA logo."),
            FactModel( "Most NASCAR teams use nitrogen in their tires instead of air."),
            FactModel( "The longest recorded tennis match lasted for 11 hours."),
            FactModel( "Golf balls reach speeds of 170 mph."),

            FactModel("Human teeth are just as tough as shark teeth."),
            FactModel("Ten percent of your body mass is made up of your blood."),
            FactModel("In the average human adult body, there are at least 67 different species of bacteria residing in the belly button alone."),
            FactModel("Most people shed up to 22 kilograms of skin in their lifetimes."),
            FactModel("Your body produces enough heat in 30 minutes to boil half a gallon of water."),
            FactModel("A quarter of human bones are found in the feet."),

            FactModel("Dog noses are at least 40x more sensitive than ours."),
            FactModel("Dog can have a dominant left or right paw."),
            FactModel("Dogs noses are wet to help absorb scent chemicals."),
            FactModel("Three dogs survived the Titanic sinking."),
            FactModel("Dogs have three eyelids."),


        )

        val testCategories = mutableListOf(
            CategoryModel("Sports"),
            CategoryModel("Basketball"),
            CategoryModel("Medicine"),
            CategoryModel("Animals"),
            CategoryModel("Dogs"),
            CategoryModel("Cats"),
        )

        val factCategoryRelations = mutableListOf(
            CategoryModelCrossRef("Gorillas can catch human colds and other illnesses.", "Animals"),
            CategoryModelCrossRef("The bat is the only mammal that can fly.", "Animals"),
            CategoryModelCrossRef("For every human in the world there are one million ants.", "Animals"),
            CategoryModelCrossRef("A tarantula spider can survive for more than two years without food.", "Animals"),
            CategoryModelCrossRef("Ants never sleep. Also they don’t have lungs.", "Animals"),
            CategoryModelCrossRef("Cats have lived with people for only 7,000 years.", "Animals"),
            CategoryModelCrossRef("We share 70% of our DNA with a slug.", "Animals"),
            CategoryModelCrossRef("Gorillas can catch human colds and other illnesses.", "Medicine"),
            CategoryModelCrossRef("Cats have lived with people for only 7,000 years.", "Cats"),

            CategoryModelCrossRef( "Olympic gold medals are predominantly made from sterling silver.", "Sports"),
            CategoryModelCrossRef( "Michael Phelps has won more olympic golds than Mexico.", "Sports"),
            CategoryModelCrossRef( "Jerry West is the silhouette of the NBA logo.", "Sports"),
            CategoryModelCrossRef( "Most NASCAR teams use nitrogen in their tires instead of air.", "Sports"),
            CategoryModelCrossRef( "The longest recorded tennis match lasted for 11 hours.", "Sports"),
            CategoryModelCrossRef( "Golf balls reach speeds of 170 mph.", "Sports"),
            CategoryModelCrossRef( "Jerry West is the silhouette of the NBA logo.", "Basketball"),

            CategoryModelCrossRef("Human teeth are just as tough as shark teeth.", "Medicine"),
            CategoryModelCrossRef("Ten percent of your body mass is made up of your blood.", "Medicine"),
            CategoryModelCrossRef("In the average human adult body, there are at least 67 different species of bacteria residing in the belly button alone.", "Medicine"),
            CategoryModelCrossRef("Most people shed up to 22 kilograms of skin in their lifetimes.", "Medicine"),
            CategoryModelCrossRef("Your body produces enough heat in 30 minutes to boil half a gallon of water.", "Medicine"),
            CategoryModelCrossRef("A quarter of human bones are found in the feet.", "Medicine"),

            CategoryModelCrossRef("Dog noses are at least 40x more sensitive than ours.", "Dogs"),
            CategoryModelCrossRef("Dog can have a dominant left or right paw.", "Dogs"),
            CategoryModelCrossRef("Dogs noses are wet to help absorb scent chemicals.", "Dogs"),
            CategoryModelCrossRef("Three dogs survived the Titanic sinking.", "Dogs"),
            CategoryModelCrossRef("Dogs have three eyelids.", "Dogs"),
            CategoryModelCrossRef("Dog noses are at least 40x more sensitive than ours.", "Animals"),
            CategoryModelCrossRef("Dog can have a dominant left or right paw.", "Animals"),
            CategoryModelCrossRef("Dogs noses are wet to help absorb scent chemicals.", "Animals"),
            CategoryModelCrossRef("Three dogs survived the Titanic sinking.", "Animals"),
            CategoryModelCrossRef("Dogs have three eyelids.", "Animals")
        )

        testFacts.forEach{appDao.insertFact(it)}
        testCategories.forEach{appDao.insertCategory(it)}
        factCategoryRelations.forEach{appDao.insertCategoryModelCrossRef(it)}

//        Timber.e(appDao.doesFactExist("The World Is Flat!!!!").toString())
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