package org.example.di

import org.example.presentation.auth.LoginCLI
import org.example.presentation.auth.RegisterCLI
import org.example.presentation.io.ConsolePrinter
import org.example.presentation.io.ConsoleReader
import org.example.presentation.io.InputReader
import org.example.presentation.io.Printer
import org.example.presentation.project.CreateProjectCLI
import org.example.presentation.project.DeleteProjectCLI
import org.example.presentation.project.GetProjectCLI
import org.example.presentation.project.UpdateProjectCLI
import org.example.presentation.task.CreateTaskCLI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {
    single<Printer> { ConsolePrinter() }
    single<InputReader> { ConsoleReader() }
    singleOf(::CreateTaskCLI)
    singleOf(::CreateProjectCLI)
    singleOf(::RegisterCLI)
    singleOf(::DeleteProjectCLI)
    singleOf(::GetProjectCLI)
    singleOf(::UpdateProjectCLI)
    singleOf(::LoginCLI)




}