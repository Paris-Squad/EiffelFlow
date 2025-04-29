package org.example.domain.usecase.auth

import org.example.domain.repository.UserRepository


class LoginUseCase(private val userRepository: UserRepository){

}