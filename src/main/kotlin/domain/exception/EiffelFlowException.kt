package org.example.domain.exception

import org.example.common.Constants

abstract class EiffelFlowException(message: String?) : Exception(message) {

    class IOException(message: String?) : EiffelFlowException(message)

    class PasswordValidationException(errors: Set<Constants.ValidationRule>) :
        EiffelFlowException("Password validation failed: ${errors.joinToString(", ") { it.message }}")

    class UserNameValidationException(errors: Set<Constants.ValidationRule>) :
        EiffelFlowException("Username validation failed: ${errors.joinToString(", ") { it.message }}")

    class AuthorizationException(message: String?) : EiffelFlowException(message)

    class NotFoundException(message: String?) : EiffelFlowException(message)

}