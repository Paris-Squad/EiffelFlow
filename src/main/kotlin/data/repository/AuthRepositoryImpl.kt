package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.data.storage.parser.UserCsvParser
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.AuthRepository
import org.example.domain.utils.ValidationErrorMessage
import java.io.FileNotFoundException

class AuthRepositoryImpl(
    private val fileDataSource: FileDataSource,
    private val userCsvParser: UserCsvParser
) : AuthRepository {
    override fun loginUser(username: String, password: String): Result<String> {
        return runCatching {
            val lines = fileDataSource.readLinesFromFile()
            val users = lines
                .filter { it.isNotBlank() }
                .map { userCsvParser.parseCsvLine(it) }

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
            val userCsv = userCsvParser.serialize(user)
            fileDataSource.writeLinesToFile(userCsv)
            SessionManger.login(user)
            user
        }.recoverCatching {
            throw EiffelFlowException.IOException(it.message)
        }
    }

    override fun isUserLoggedIn(): Result<Boolean> {
        return runCatching {
            val lines = fileDataSource.readLinesFromFile()
            if (lines.any { it.isNotBlank() }) {
                val userCsv = lines.first { it.isNotBlank() }
                val user = userCsvParser.parseCsvLine(userCsv)
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
            fileDataSource.clearFile()
            SessionManger.logout()
            true
        }
    }

    companion object {
        const val FILE_NAME = "auth.csv"
    }
}