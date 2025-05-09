package org.example.di

import org.example.domain.usecase.auth.HashPasswordUseCase
import org.example.domain.usecase.auth.RegisterUseCase
import org.example.domain.usecase.auth.ValidatePasswordUseCase
import org.example.domain.usecase.auth.ValidateUserNameUseCase
import org.example.domain.usecase.project.CreateProjectUseCase
import org.example.domain.usecase.project.GetProjectUseCase
import org.example.domain.usecase.task.CreateTaskUseCase
import org.koin.dsl.module

val useCasesModule = module {
    factory { ValidatePasswordUseCase() }
    factory { ValidateUserNameUseCase() }
    factory { HashPasswordUseCase() }
    factory { RegisterUseCase(get(), get()) }
    single { GetProjectUseCase(get()) }
    single { CreateTaskUseCase(get()) }
    single { CreateProjectUseCase(get()) }
    single { RegisterUseCase(get() , get()) }
    single { HashPasswordUseCase() }
}