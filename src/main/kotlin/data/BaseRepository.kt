package org.example.data

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
}