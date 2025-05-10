package org.example.presentation

import kotlinx.coroutines.runBlocking
import org.example.data.utils.SessionManger
import org.example.domain.usecase.auth.CheckCurrentSessionUseCase
import org.koin.java.KoinJavaComponent.getKoin

class EiffelFlowConsoleCLI {
    fun start() {
        val currentSessionUseCase = getKoin().get<CheckCurrentSessionUseCase>()
        val uiContainer = getKoin().get<UIContainer>()

        println("Welcome to the EiffelFlow.\nPlease wait Checking the current session...")

        runBlocking {
           val  currentSession = currentSessionUseCase.getCurrentSessionUser()
            if (currentSession == null) {
                println("You are not logged in. Please login First.")
                loginUI(uiContainer)
            }else{
                println("Welcome back ${currentSession.username}")
                startUI(uiContainer)
            }
        }
    }

    fun loginUI(uiContainer: UIContainer) {
        while (SessionManger.isLoggedIn().not()){
            uiContainer.loginCLI.start()
        }
        startUI(uiContainer)
    }

    fun startUI(uiContainer: UIContainer) {
        while (true) {
            println(
                """
            Choose an option:
            1. register
            2. create project
            3. create task
            4. delete project
            5. get project
            5. update project
            7. view project audit logs
            8. edit task
            0. Exit
            """.trimIndent()
            )

            print("Enter your choice from the above list: ")
            val input = readlnOrNull()

            when (input) {
                "1" -> uiContainer.registerCLI.start()
                "2" -> uiContainer.getProjectCLI.start()
                "3" -> uiContainer.createTaskCLI.start()
                "4" -> uiContainer.deleteProjectCLI.start()
                "5" -> uiContainer.getProjectCLI.start()
                "6" -> uiContainer.updateProjectCLI.start()
                "7" -> uiContainer.getProjectAuditLogsCLI.getProjectAuditLogsInput()
                "8" -> uiContainer.editTaskCli.start()
                "0" -> {
                    println("Thanks for using our app!")
                    break
                }

                else -> println("Invalid input. Please enter a number from 0 to 8.")
            }

            println("\n-------------------------------\n")
        }
    }
}