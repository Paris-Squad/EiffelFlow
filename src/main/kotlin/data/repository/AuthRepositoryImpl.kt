package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.mapper.UserCsvMapper
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.AuthRepository
import org.example.domain.utils.ValidationErrorMessage
import java.io.FileNotFoundException

class AuthRepositoryImpl(
    private val storageManager: FileDataSource,
    private val userMapper: UserCsvMapper
) : AuthRepository {
    override fun loginUser(username: String, password: String): Result<String> {
        return runCatching {
            val lines = storageManager.readLinesFromFile()
            val users = lines
                .filter { it.isNotBlank() }
                .map { userMapper.mapFrom(it) }

            val user = users.find { it.username == username }
                ?: throw EiffelFlowException.AuthenticationException(setOf(ValidationErrorMessage.INVALID_USERNAME))

            if (user.password != password) {
                throw EiffelFlowException.AuthenticationException(setOf(ValidationErrorMessage.INVALID_PASSWORD))
            }

            saveUserLogin(user)
            "Login successfully"
        }.recoverCatching {
            when (it) {
                is EiffelFlowException -> throw it
                else -> throw EiffelFlowException.IOException(it.message)
            }
        }
    }

    override fun saveUserLogin(user: User): Result<User> {
        return runCatching {
            val userCsv = userMapper.mapTo(user)
            storageManager.writeLinesToFile(userCsv)
            SessionManger.login(user)
            user
        }.recoverCatching {
            throw EiffelFlowException.IOException(it.message)
        }
    }

    override fun isUserLoggedIn(): Result<Boolean> {
        return runCatching {
            val lines = storageManager.readLinesFromFile()
            if (lines.any { it.isNotBlank() }) {
                val userCsv = lines.first { it.isNotBlank() }
                val user = userMapper.mapFrom(userCsv)
                SessionManger.login(user)
                true
            } else {
                false
            }
        }.recoverCatching { e ->
            when (e) {
                is FileNotFoundException -> false
                else -> throw e
            }
        }
    }

    override fun clearLogin(): Result<Boolean> {
        return runCatching {
            storageManager.clearFile()
            SessionManger.logout()
            true
        }
    }

    companion object {
        const val FILE_NAME = "auth.csv"
    }
}