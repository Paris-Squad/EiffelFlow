package org.example.di

import org.example.presentation.EiffelFlowConsoleCLI
import org.example.presentation.UIContainer
import org.example.presentation.audit.GetAuditLogsCLI
import org.example.presentation.audit.GetProjectAuditLogsCLI
import org.example.presentation.audit.GetTaskAuditLogsCLI
import org.example.presentation.auth.CheckCurrentSessionCLI
import org.example.presentation.auth.LoginCLI
import org.example.presentation.auth.LogoutCLI
import org.example.presentation.user.CreateUserCLI
import org.example.presentation.helper.ConsolePrinter
import org.example.presentation.io.ConsoleReader
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.project.CreateProjectCLI
import org.example.presentation.project.DeleteProjectCLI
import org.example.presentation.project.GetProjectCLI
import org.example.presentation.project.ShowProjectSwimLanesCLI
import org.example.presentation.project.UpdateProjectCLI
import org.example.presentation.task.CreateTaskCLI
import org.example.presentation.task.DeleteTaskCLI
import org.example.presentation.task.EditTaskCli
import org.example.presentation.task.GetTaskCLI
import org.example.presentation.user.DeleteUserCLI
import org.example.presentation.user.GetUserCLI
import org.example.presentation.user.UpdateUserCLI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {

    //Helper
    single<Printer> { ConsolePrinter() }
    single<InputReader> { ConsoleReader() }

    //Audit
    singleOf(::GetAuditLogsCLI)
    singleOf(::GetProjectAuditLogsCLI)
    singleOf(::GetTaskAuditLogsCLI)

    //Auth
    singleOf(::CheckCurrentSessionCLI)
    singleOf(::LoginCLI)
    singleOf(::LogoutCLI)

    //User
    singleOf(::CreateUserCLI)
    singleOf(::DeleteUserCLI)
    singleOf(::UpdateUserCLI)
    singleOf(::GetUserCLI)


    //Project
    singleOf(::CreateProjectCLI)
    singleOf(::DeleteProjectCLI)
    singleOf(::GetProjectCLI)
    singleOf(::UpdateProjectCLI)
    singleOf(::ShowProjectSwimLanesCLI)

    //Task
    singleOf(::CreateTaskCLI)
    singleOf(::DeleteTaskCLI)
    singleOf(::EditTaskCli)
    singleOf(::GetTaskCLI)

    //Main UI
    singleOf(::UIContainer)
    singleOf(::EiffelFlowConsoleCLI)




}