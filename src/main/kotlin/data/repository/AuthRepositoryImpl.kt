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
    private val fileDataSource: FileDataSource, private val userCsvParser: UserCsvParser
) : AuthRepository {

    override suspend fun loginUser(username: String, password: String): User {
        return try {
            val lines = fileDataSource.readLinesFromFile()
            val users = lines.filter { it.isNotBlank() }.map { userCsvParser.parseCsvLine(it) }

            val user = users.find { it.username == username } ?: throw EiffelFlowException.AuthenticationException(
                setOf(ValidationErrorMessage.INVALID_USERNAME)
            )

            if (user.password != password) {
                throw EiffelFlowException.AuthenticationException(setOf(ValidationErrorMessage.INVALID_PASSWORD))
            }

            saveUserLogin(user)
        } catch (e: Exception) {
            when (e) {
                is EiffelFlowException -> throw e
                else -> throw EiffelFlowException.IOException(e.message)
            }
        }

    }

    override suspend fun saveUserLogin(user: User): User {
        return try {
            val userCsv = userCsvParser.serialize(user)
            fileDataSource.writeLinesToFile(userCsv)
            SessionManger.login(user)
            user
        } catch (e: Exception) {
            throw EiffelFlowException.IOException(e.message)
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return try {
            val lines = fileDataSource.readLinesFromFile()
            if (lines.any { it.isNotBlank() }) {
                val userCsv = lines.first { it.isNotBlank() }
                val user = userCsvParser.parseCsvLine(userCsv)
                SessionManger.login(user)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            if (e is FileNotFoundException) return false
            throw EiffelFlowException.IOException(e.message)
        }
    }

    override suspend fun clearLogin() {
        return try {
            fileDataSource.clearFile()
            SessionManger.logout()
        } catch (e: Exception) {
            throw EiffelFlowException.IOException(e.message)
        }
    }

    companion object {
        const val FILE_NAME = "auth.csv"
    }
}