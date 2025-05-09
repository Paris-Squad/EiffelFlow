package org.example

import org.example.di.appModule
import org.example.di.mongoModule
import org.example.di.uiModule
import org.example.di.useCasesModule
import org.example.presentation.task.CreateTaskCLI
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() {
    startKoin {
        modules(appModule, useCasesModule, uiModule , mongoModule)
    }
    //getUser data to know the state
    val mealConsoleUI = getKoin().get<CreateTaskCLI>()

    mealConsoleUI.start()

}