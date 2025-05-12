package org.example.di

import org.example.domain.usecase.audit.GetAllAuditLogsUseCase
import org.example.domain.usecase.audit.GetProjectAuditUseCase
import org.example.domain.usecase.audit.GetTaskAuditUseCase
import org.example.domain.usecase.auth.*
import org.example.domain.usecase.project.CreateProjectStateUseCase
import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.domain.usecase.project.DeleteProjectStateUseCase
import org.example.domain.usecase.project.DeleteProjectUseCase
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.domain.usecase.project.UpdateProjectStateUseCase
import org.example.domain.usecase.project.UpdateProjectUseCase
import org.example.domain.usecase.task.CreateTaskUseCase
import org.example.domain.usecase.task.DeleteTaskUseCase
import org.example.domain.usecase.task.EditTaskUseCase
import org.example.domain.usecase.task.GetTaskUseCase
import org.example.domain.usecase.user.CreateUserUseCase
import org.example.domain.usecase.user.DeleteUserUseCase
import org.example.domain.usecase.user.GetUserUseCase
import org.example.domain.usecase.user.UpdateUserUseCase
import org.koin.dsl.module

val useCasesModule = module {
    //Project
    single { CreateProjectUseCase(get(), get()) }
    single { DeleteProjectUseCase(get(), get()) }
    single { GetProjectUseCase(get()) }
    single { UpdateProjectUseCase(get(), get()) }
    single { CreateProjectStateUseCase(get(), get()) }
    single { UpdateProjectStateUseCase(get(), get()) }
    single { DeleteProjectStateUseCase(get(), get()) }

    //Task
    single { CreateTaskUseCase(get(), get()) }
    single { DeleteTaskUseCase(get(), get()) }
    single { EditTaskUseCase(get(), get()) }
    single { GetTaskUseCase(get()) }

    //Audit
    single { GetProjectAuditUseCase(get()) }
    single { GetTaskAuditUseCase(get()) }
    single { GetAllAuditLogsUseCase(get()) }

    //Auth
    single { CheckCurrentSessionUseCase(get()) }
    factory { HashPasswordUseCase() }
    single { LoginUseCase(get(),get()) }
    single { LogoutUseCase(get()) }
    factory { ValidatePasswordUseCase() }
    factory { ValidateUserNameUseCase() }

    //User
    factory { CreateUserUseCase(get(), get(),get()) }
    factory { DeleteUserUseCase(get(),get()) }
    factory { UpdateUserUseCase(get(), get(),get()) }
    factory { GetUserUseCase(get()) }

}