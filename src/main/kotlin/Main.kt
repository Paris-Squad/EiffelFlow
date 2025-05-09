package org.example

import org.example.di.appModule
import org.example.di.csvModule
import org.example.di.uiModule
import org.example.di.useCasesModule
import org.koin.core.context.startKoin


fun main() {
    startKoin {
        modules(appModule, csvModule, useCasesModule, uiModule)
    }
    //getUser data to know the state
}