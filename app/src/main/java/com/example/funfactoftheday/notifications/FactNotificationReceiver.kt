package com.example.funfactoftheday.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.funfactoftheday.database.AppDatabase
import com.example.funfactoftheday.database.AppRepository
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/*
idea to " how can i access room database in broadcast receiver with viewmodels"
you have to create settings page, make notifications daily and way to adjust frequency of
them, create viewmodel for settings page. call service.shownotification using viewmodel
allfacts data
save facts in sharedprefences, access sharedpref from broadcastrecieve
 */
class FactNotificationReceiver: BroadcastReceiver() {

    private lateinit var coroutineScope: CoroutineScope

    /*
    fun BroadcastReceiver.goAsync(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ){
        val pendingResult = goAsync()
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch(context) {
            try{
                block()
            } finally {
                pendingResult.finish()
            }
        }
    }
*/

    override fun onReceive(context: Context, intent: Intent?) {

        /*
        val test = AppDatabase.getDatabase(context, applicationScope).appDao().returnFavoriteCategories()

        for(category in test.toList()){
            Timber.e("Fact Name: ${category.categoryName}")
            categories.add(category)
            }


        val applicationScope = CoroutineScope(SupervisorJob())
        var categories:MutableList<CategoryModel> = mutableListOf()
        suspend {
            val test =
                withContext(Dispatchers.Default){
                  AppDatabase.getDatabase(context, applicationScope).appDao().returnFavoriteCategories()
                }
            for(category in test.toList()) {
                Timber.e("Fact Name: ${category.categoryName}")
                categories.add(category)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1000)

            var testFact = "Shit was null cuhhhhhhhhh"
            if(!categories.isNullOrEmpty()){
                testFact = categories[0].categoryName
            }
            val service = FunFactNotificationService(context)
            service.showNotification("Test Fact $testFact")
        }
*/
        /*
runBlocking {
    val test =
        withContext(Dispatchers.IO){
            appDao.returnFavoriteCategories()
        }
    Timber.e("withcontext called")
    for(category in test) {
        Timber.e("For loop called")
        Timber.e("Fact Name: ${category.categoryName}")
        categories.add(category)
    }


    withContext(Dispatchers.Default){
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1000)

        var testFact = "Shit was null cuhhhhhhhhh It come withe eggwoll"
        if(!categories.isNullOrEmpty()){
            testFact = categories[0].categoryName
        }
        val service = FunFactNotificationService(context)
        service.showNotification("Test Fact $testFact")
    }
}
*/


        context.let {
            val sharedPreferences = context.getSharedPreferences("FactsSharedPreferences", Context.MODE_PRIVATE)
            val facts = sharedPreferences.getStringSet("TestStringSet", HashSet<String>())
            var fact:String? = ""
            if(!facts.isNullOrEmpty()){
                fact = facts.randomOrNull()
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1000)

            val service = FunFactNotificationService(context)
            service.showNotification("$fact")
        }

    }

    private suspend fun getAllFacts(context: Context): List<CategoryModel>{
        Timber.e("GetAllFacts Called")
        coroutineScope = CoroutineScope(Dispatchers.IO)
        val database = AppDatabase.getDatabase(context, coroutineScope)
        val appRepository = AppRepository(database.appDao())

        return withContext(Dispatchers.IO){
            appRepository.getFavoriteCategories()
        }
    }

    private suspend fun testGetAllFacts(context: Context):List<CategoryModel> = coroutineScope.async{
        return@async getAllFacts(context)
    }.await()

}