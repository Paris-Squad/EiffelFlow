package org.example

import org.example.di.appModule
import org.example.di.mongoModule
import org.example.di.uiModule
import org.example.di.useCasesModule
import org.example.presentation.auth.LoginCLI
import org.example.presentation.auth.RegisterCLI
import org.example.presentation.project.CreateProjectCLI
import org.example.presentation.project.DeleteProjectCLI
import org.example.presentation.project.GetProjectCLI
import org.example.presentation.project.UpdateProjectCLI
import org.example.presentation.task.CreateTaskCLI
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() {
    startKoin {
        modules(appModule, useCasesModule, uiModule, mongoModule)
    }

//    mealConsoleUI.createProjectInput()

    start()

}


fun start() {
    println("Welcome to the plan-mit.")
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
            0. Exit
            """.trimIndent()
        )

        print("Enter your choice from the above list: ")
        val input = readlnOrNull()

        when (input) {
            "1" -> login()
            "2" -> register()
            "3" -> createProject()
            "4" -> createTask()
            "5" -> deleteProject()
            "6" -> getProject()
            "7" -> updateProject()
            "0" -> {
                println("Thanks for using our app!")
                break
            }

            else -> println("Invalid input. Please enter a number from 0 to 15.")
        }

        println("\n-------------------------------\n")
    }
}


private fun login() {
    val login = getKoin().get<LoginCLI>()
    login.onLoginClicked()
}

private fun register() {
    val register = getKoin().get<RegisterCLI>()
    register.onRegisterClick()
}

private fun createProject() {
    val createProjectCLI = getKoin().get<CreateProjectCLI>()
    createProjectCLI.createProjectInput()
}

private fun createTask() {
    val createTaskCLI = getKoin().get<CreateTaskCLI>()
    createTaskCLI.start()
}

private fun deleteProject() {
    val deleteProjectCLI = getKoin().get<DeleteProjectCLI>()
    deleteProjectCLI.deleteProjectInput()
}

private fun getProject() {
    val getProjectCLI = getKoin().get<GetProjectCLI>()
    getProjectCLI.displayProjects()
}

private fun updateProject() {
    val updateProjectCLI = getKoin().get<UpdateProjectCLI>()
    updateProjectCLI.updateProjectInput()
}


