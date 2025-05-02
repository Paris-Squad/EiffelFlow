package org.example.presentation.presenter

import org.example.domain.usecase.auth.LogoutUseCase

class LogoutPresenter(private  val logoutUseCase: LogoutUseCase) {

    fun logout(): Result<Unit>{
        TODO("Not implement yet")
    }
}