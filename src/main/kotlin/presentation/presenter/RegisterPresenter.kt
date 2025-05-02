package org.example.presentation.presenter

import org.example.domain.model.RoleType
import org.example.domain.usecase.auth.RegisterUseCase

class RegisterPresenter(
    private val registerUseCase: RegisterUseCase
) {
    fun register(username: String, password: String, role: RoleType) {
        TODO("Not implement yet")
    }
}