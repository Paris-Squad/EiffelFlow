package org.example.presentation.presenter

import org.example.domain.usecase.auth.LoginUseCase

class LoginPresenter(private val loginUseCase: LoginUseCase) {
    fun onLoginClicked(userName: String, password: String): Result<String> {
        TODO("Not implement yet")
    }
}

