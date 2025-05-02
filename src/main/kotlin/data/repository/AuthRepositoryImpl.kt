package org.example.data.repository

import org.example.data.storage.FileDataSource
import org.example.data.storage.SessionManger
import org.example.domain.model.User
import org.example.domain.repository.AuthRepository
import java.io.FileNotFoundException

class AuthRepositoryImpl(
    private val storageManager: FileDataSource
) : AuthRepository {
    override fun saveUserLogin(user: User): Result<Boolean> {
        TODO("TO handle write")
//        return try {
//            storageManager.writeLinesToFile(userID.toString())
//            SessionManger.login(user)
//            Result.success(true)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
    }

    override fun getIsUserLoggedIn(): Result<Boolean> {
        return try {
            val lines = storageManager.readLinesFromFile()
            Result.success(lines.any { it.isNotBlank() })
//            SessionManger.login(user)
            TODO("TO map the line to user and save it to SessionManager")
        } catch (_: FileNotFoundException) {
            Result.success(false)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearLogin(): Result<Boolean> {
        return try {
            storageManager.clearFile()
            SessionManger.logout()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val FILE_NAME = "auth.txt"
    }
}