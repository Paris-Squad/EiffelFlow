package org.example.presentation.presenter

import org.example.domain.usecase.auth.LoginUseCase

class LoginPresenter(private val loginUseCase: LoginUseCase) {
    fun onLoginClicked(userName: String, password: String): String {
        TODO("Not implement yet")
    }
}

