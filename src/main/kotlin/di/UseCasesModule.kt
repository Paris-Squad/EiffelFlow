package org.example.di

import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.domain.usecase.auth.*
import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.domain.usecase.project.DeleteProjectUseCase
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.domain.usecase.project.UpdateProjectUseCase
import org.example.domain.usecase.task.CreateTaskUseCase
import org.example.domain.usecase.task.DeleteTaskUseCase
import org.example.domain.usecase.task.EditTaskUseCase
import org.koin.dsl.module

val useCasesModule = module {
    factory { ValidatePasswordUseCase() }
    factory { ValidateUserNameUseCase() }
    factory { HashPasswordUseCase() }
    factory { RegisterUseCase(get(), get()) }
    single { GetProjectUseCase(get()) }
    single { CreateTaskUseCase(get(),get()) }
    single { CreateProjectUseCase(get(),get()) }
    single { LoginUseCase(get()) }
    single { EditTaskUseCase(get(),get()) }
    single { GetProjectAuditUseCase(get()) }
    single { DeleteProjectUseCase(get(),get()) }
    single { UpdateProjectUseCase(get(),get()) }
    single { DeleteTaskUseCase(get(),get()) }
    single { CheckCurrentSessionUseCase(get()) }
}