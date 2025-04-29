package org.example.di

import org.example.domain.usecase.auth.HashPasswordUseCase
import org.example.domain.usecase.auth.RegisterUseCase
import org.example.domain.usecase.auth.ValidatePasswordUseCase
import org.example.domain.usecase.auth.ValidateUserNameUseCase
import org.example.domain.usecase.auth.Validator
import org.koin.dsl.module

val useCasesModule = module {
    factory { ValidatePasswordUseCase(Validator) }
    factory { ValidateUserNameUseCase(Validator) }
    factory { HashPasswordUseCase() }
    factory { RegisterUseCase(get(), get(), get(), get()) }
}