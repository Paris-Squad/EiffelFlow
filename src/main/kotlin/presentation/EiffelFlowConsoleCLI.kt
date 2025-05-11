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

    fun logout(uiContainer: UIContainer) {
        uiContainer.logoutCLI.start()
        while (SessionManger.isLoggedIn()){
            startUI(uiContainer)
        }
        loginUI(uiContainer)
    }

    fun startUI(uiContainer: UIContainer) {
        while (true) {
            println(
                """
            Choose an option:
            1. Create user
            2. Create project
            3. Delete project
            4. Update project
            5. View all projects
            6. view project by ID
            7. Create task
            8. Edit task
            9. Delete task
            10. View all tasks
            11. View task by ID
            12. Get Project AuditLogs by ID
            13. Get Task AuditLogs by ID
            14. Get All AuditLogs
            15. Update profile
            16. Delete User
            17. View all users
            18. Logout
            0. Exit
            """.trimIndent()
            )

            print("Enter your choice from the above list: ")
            val input = readlnOrNull()

            when (input) {
                "1" -> uiContainer.registerCLI.start()
                "2" -> uiContainer.createProjectCLI.start()
                "3" -> uiContainer.deleteProjectCLI.start()
                "4" -> uiContainer.updateProjectCLI.start()
                "5" -> uiContainer.getProjectCLI.start()
                "6" -> uiContainer.getProjectCLI.displayProjectById()
                "7" -> uiContainer.createTaskCLI.start()
                "8" -> uiContainer.editTaskCli.start()
                "9" -> uiContainer.deleteTaskCLI.start()
                "10" -> uiContainer.getTaskCLI.viewTasks()
                "11" -> uiContainer.getTaskCLI.displayTaskById()
                "12" -> uiContainer.getProjectAuditLogsCLI.getProjectAuditLogsInput()
                "13" -> uiContainer.getTaskAuditLogsCLI.getTaskAuditLogsInput()
                "14" -> uiContainer.getAuditLogsCLI.getAllAuditLogs()
                "15" -> uiContainer.updateUserCLI.start()
                "16" -> uiContainer.deleteUserCLI.start()
                "17" -> TODO("View all users")
                "18" -> uiContainer.logoutCLI.start()
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