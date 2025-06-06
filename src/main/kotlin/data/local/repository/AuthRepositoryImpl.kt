package org.example.data.local.csvrepository

import org.example.data.BaseRepository
import org.example.data.local.FileDataSource
import org.example.data.local.parser.UserCsvParser
import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.User
import org.example.domain.repository.AuthRepository
import org.example.domain.utils.ValidationErrorMessage

class AuthRepositoryImpl(
    private val authFileDataSource: FileDataSource,
    private val usersFileDataSource: FileDataSource,
    private val userCsvParser: UserCsvParser
) : BaseRepository(), AuthRepository {

    override suspend fun loginUser(username: String, password: String): User {
        return executeSafely {
            val lines = usersFileDataSource.readLinesFromFile()
            val users = lines.filter { it.isNotBlank() }.map { userCsvParser.parseCsvLine(it) }

            val user = users.find { it.username == username } ?: throw EiffelFlowException.AuthenticationException(
                setOf(ValidationErrorMessage.INVALID_USERNAME)
            )

            if (user.password != password) {
                throw EiffelFlowException.AuthenticationException(setOf(ValidationErrorMessage.INVALID_PASSWORD))
            }
            saveUserLogin(user)
        }

    }

    override suspend fun saveUserLogin(user: User): User {
        return executeSafely {
            val userCsv = userCsvParser.serialize(user)
            authFileDataSource.writeLinesToFile(userCsv)
            SessionManger.login(user)
            user
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return executeSafely {
            val lines = authFileDataSource.readLinesFromFile()
            if (lines.any { it.isNotBlank() }) {
                val userCsv = lines.first { it.isNotBlank() }
                val user = userCsvParser.parseCsvLine(userCsv)
                SessionManger.login(user)
                true
            } else {
                false
            }
        }
    }

    override suspend fun clearLogin() {
        return executeSafely {
            authFileDataSource.clearFile()
            SessionManger.logout()
        }
    }

    companion object {
        const val FILE_NAME = "auth.csv"
    }
}