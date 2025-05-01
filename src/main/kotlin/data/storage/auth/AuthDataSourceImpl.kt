package org.example.data.storage.auth

import org.example.data.storage.FileStorageManager
import java.io.FileNotFoundException
import java.util.UUID

class AuthDataSourceImpl(
    private val storageManager: FileStorageManager
) : AuthDataSource {
    override fun saveUserLogin(userID: UUID): Result<Boolean> {
        return try {
            storageManager.writeLinesToFile(userID.toString())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getIsUserLoggedIn(): Result<Boolean> {
        return try {
            val lines = storageManager.readLinesFromFile()
            Result.success(lines.any { it.isNotBlank() })
        } catch (_: FileNotFoundException) {
            Result.success(false)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearLogin(): Result<Boolean> {
        return try {
            storageManager.clearFile()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val FILE_NAME = "auth.txt"
    }
}