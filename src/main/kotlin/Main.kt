package org.example

import com.mongodb.kotlin.client.coroutine.MongoClient
import org.example.di.appModule
import org.example.di.mongoModule
import org.example.di.uiModule
import org.example.di.useCasesModule
import org.example.presentation.EiffelFlowConsoleCLI
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() {
    startKoin {
        modules(appModule, useCasesModule, uiModule, mongoModule)
    }

    val mongoClient = getKoin().get<MongoClient>()
    val eiffelFlowConsoleCLI = getKoin().get<EiffelFlowConsoleCLI>()
    eiffelFlowConsoleCLI.start()
    mongoClient.close()
}
