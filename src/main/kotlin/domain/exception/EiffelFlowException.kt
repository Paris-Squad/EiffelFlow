package org.example.domain.exception

import org.example.domain.utils.ValidationErrorMessage

abstract class EiffelFlowException(message: String?) : Exception(message) {

    class IOException(message: String?) : EiffelFlowException(message)

    class DataBaseException(message: String?) : EiffelFlowException(message)

    class AuthenticationException(errors: Set<ValidationErrorMessage>) :
        EiffelFlowException(errors.joinToString(", ") { it.message })

    class AuthorizationException(message: String?) : EiffelFlowException(message)

    class NotFoundException(message: String?) : EiffelFlowException(message)

    class StateAlreadyExist(message: String?) : EiffelFlowException(message)
}