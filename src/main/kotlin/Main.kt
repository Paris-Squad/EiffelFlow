//package org.example
//
//import kotlinx.coroutines.runBlocking
//import kotlinx.datetime.LocalDateTime
//import org.example.data.storage.SessionManger
//import org.example.di.appModule
//import org.example.di.mongoModule
//import org.example.di.uiModule
//import org.example.di.useCasesModule
//import org.example.domain.model.Project
//import org.example.domain.model.RoleType
//import org.example.domain.model.Task
//import org.example.domain.model.TaskState
//import org.example.domain.model.User
//import org.example.domain.repository.AuthRepository
//import org.koin.core.context.startKoin
//import org.koin.mp.KoinPlatform.getKoin
//import java.util.UUID
//
//
//fun main() {
//    SessionManger.login(validUser)
//    startKoin {
//        modules(appModule, mongoModule, useCasesModule, uiModule)
//    }
//    //getUser data to know the state
////    val userRepository = getKoin().get<UserRepository>()
////    val auditRepository = getKoin().get<AuditRepository>()
////    val projectRepository = getKoin().get<ProjectRepository>()
////    val taskRepository = getKoin().get<TaskRepository>()
//    val authRepository = getKoin().get<AuthRepository>()
//    runBlocking {
//        SessionManger.login(validUser)
//
////        val createUser = userRepository.createUser(validUser)
////        println("createUser $createUser")
//
//
////       val deletedUser = userRepository.deleteUser(createUser.userId)
////        println("deletedUser $deletedUser")
//
////        val updateUser = userRepository.updateUser(
////            createUser.copy(
////                password = "abdo edit password",
////                username = "Abdo edit name"
////            )
////        )
////        println("updateUser $updateUser")
//
////        val users = userRepository.getUsers()
////        println("users $users")
//
////        val getUserById = userRepository.getUserById(createUser.userId)
////        println("getUserById $getUserById")
////
////        val auditLogs = auditRepository.getAuditLogs()
////        println("auditLogs $auditLogs")
////
////        val getTaskAuditLogById = auditRepository.getTaskAuditLogById(UUID.fromString("0ca05865-1c9d-406f-9f82-ea0bb28aea30"))
////        println("getTaskAuditLogById $getTaskAuditLogById")
////
////        val getProjectAuditLogById = auditRepository.getProjectAuditLogById(UUID.fromString("0eca2eab-3011-47bd-bcb3-0a2e9719f01d"))
////        println("getProjectAuditLogById $getProjectAuditLogById")
//
////       val createdProject = projectRepository.createProject(CORRECT_PROJECT.copy(projectId = UUID.randomUUID()))
////        println("createdProject $createdProject")
////
////       val createdProject2 = projectRepository.createProject(CORRECT_PROJECT.copy(projectId = UUID.randomUUID()))
////        println("createdProject2 $createdProject2")
//
////        val createdProject2 = CORRECT_PROJECT.copy(projectId = UUID.fromString("c47c8ab7-4d9b-44e0-8f56-217b833cb897"))
////       val updatedProject = projectRepository.updateProject(createdProject2.copy(
////            projectName = "Project2 Edited",
////            projectDescription = "Description2 Edited"
////        ), createdProject2, changedField = "projectDescription")
////
////        println("updatedProject $updatedProject")
////
////
////        val projects = projectRepository.getProjects()
////        println("projects $projects")
////
////
////        val getProjectById = projectRepository.getProjectById(UUID.fromString("edb1cf33-9f11-44e5-8643-ebb776ede2fe"))
////        println("getProjectById $getProjectById")
//
////       val createdTask = taskRepository.createTask(validTask)
////        println("createdTask $createdTask")
////
////       val createdTask2 = taskRepository.createTask(validTask.copy(taskId = UUID.randomUUID(),))
////        println("createdTask2 $createdTask2")
////
////        (1..10).forEach {
////            taskRepository.createTask(
////                validTask.copy(
////                    taskId = UUID.randomUUID(),
////                    title = "Task title $it",
////                    description = "Task description $it"
////                )
////            )
////        }
////
////        val tasks = taskRepository.getTasks()
////        println("tasks $tasks")
////
////        val getTaskById = taskRepository.getTaskById(createdTask.taskId)
////        println("getTaskById $getTaskById")
////
////        val updatedTask = taskRepository.updateTask(
////            createdTask.copy(
////                title = "Task Edited",
////                description = "Description Edited"
////            ),
////            createdTask,
////            changedField = "ffff"
////        )
////        println("updatedTask $updatedTask")
////
////        val deletedTask = taskRepository.deleteTask(createdTask2.taskId)
////        println("deletedTask $deletedTask")
//
//        val loggedUser = authRepository.loginUser("Abdo 2", "234554")
//        println("loggedUser $loggedUser")
//
//        val isUserLoggedIn = authRepository.isUserLoggedIn()
//        println("isUserLoggedIn $isUserLoggedIn")
//
////        authRepository.clearLogin()
//
//
//        val isUserLoggedIn2 = authRepository.isUserLoggedIn()
//        println("isUserLoggedIn2 $isUserLoggedIn2")
//    }
//
//}
//
//private val mockCreatedAt = LocalDateTime(2023, 1, 1, 12, 0)
//
//val validUser = User(
//    userId = UUID.randomUUID(),
//    username = "Test Add",
//    password = "fkskds",
//    role = RoleType.ADMIN
//)
//
//val validTask = Task(
//    taskId = UUID.randomUUID(),
//    title = "Find Devil Devil Bareq",
//    description = "try to find Devil Bareq and put him in the jail",
//    createdAt = mockCreatedAt,
//    creatorId = validUser.userId,
//    projectId = UUID.fromString("c47c8ab7-4d9b-44e0-8f56-217b833cb897"),
//    assignedId = validUser.userId,
//    role = RoleType.MATE,
//    state = TaskState(name = "todo")
//)
//
//val CORRECT_PROJECT = Project(
//    projectId = UUID.randomUUID(),
//    projectName = "Project1",
//    projectDescription = "Description1",
//    createdAt = LocalDateTime.parse("1999-08-07T22:22:22"),
//    adminId = validUser.userId,
//    taskStates = listOf(
//        TaskState(
//            stateId = UUID.randomUUID(),
//            name = "Backlog"
//        ),
//        TaskState(
//            stateId = UUID.randomUUID(),
//            name = "In Progress"
//        )
//    )
//)