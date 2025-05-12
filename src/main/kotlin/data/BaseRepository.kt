package org.example.data

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException

abstract class BaseRepository {
    protected suspend fun <T> wrapInTryCatch(block: suspend () -> T): T {
        return try {
            block()
        } catch (exception: EiffelFlowException) {
            throw exception
        } catch (exception: Throwable) {
            throw EiffelFlowException.IOException("Can't perform this action because ${exception.message}")
        }
    }

    protected suspend fun <T> executeIfAdmin(block: suspend () -> T): T {
        require(SessionManger.isAdmin()){
            throw EiffelFlowException.AuthorizationException("Not Allowed, Admin only allowed")
        }
        return wrapInTryCatch(block)
    }
}