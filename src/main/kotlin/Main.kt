package org.example

import com.mongodb.kotlin.client.coroutine.MongoClient
import org.example.di.appModule
import org.example.di.mongoModule
import org.example.di.uiModule
import org.example.di.useCasesModule
import org.example.presentation.UIContainer
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() {
    startKoin {
        modules(appModule, useCasesModule, uiModule, mongoModule)
    }

    val mongoClient = getKoin().get<MongoClient>()
    start()
    mongoClient.close()
}


fun start() {
    println("Welcome to the EiffelFlow.")
    val uiContainer = getKoin().get<UIContainer>()
    while (true) {
        println(
            """
            Choose an option:
            1. login 
            2. register
            3. create project
            4. create task
            5. delete project
            6. get project
            7. update project
            8. view project audit logs
            0. Exit
            """.trimIndent()
        )

        print("Enter your choice from the above list: ")
        val input = readlnOrNull()

        when (input) {
            "1" -> uiContainer.loginCLI.start()
            "2" -> uiContainer.registerCLI.start()
            "3" -> uiContainer.getProjectCLI.start()
            "4" -> uiContainer.createTaskCLI.start()
            "5" -> uiContainer.deleteProjectCLI.start()
            "6" -> uiContainer.getProjectCLI.start()
            "7" -> uiContainer.updateProjectCLI.start()
            "8" -> uiContainer.getProjectAuditLogsCLI.getProjectAuditLogsInput()
            "0" -> {
                println("Thanks for using our app!")
                break
            }

            else -> println("Invalid input. Please enter a number from 0 to 8.")
        }

        println("\n-------------------------------\n")
    }
}
