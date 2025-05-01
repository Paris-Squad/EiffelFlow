package org.example.domain.exception

import org.example.common.Constants

abstract class EiffelFlowException(message: String?) : Exception(message) {

    class IOException(message: String?) : EiffelFlowException(message)

    class PasswordValidationException(errors: Set<Constants.ValidationRule>) :
        EiffelFlowException("Password validation failed: ${errors.joinToString(", ") { it.message }}")

    class UserNameValidationException(errors: Set<Constants.ValidationRule>) :
        EiffelFlowException("Username validation failed: ${errors.joinToString(", ") { it.message }}")

    class UsernameAlreadyExistsException : EiffelFlowException("Username already exists")

    class UnauthorizedRegistrationException : EiffelFlowException("Only admins can register new users")

    class ElementNotFoundException(message: String) : EiffelFlowException(message)
    class TaskNotFoundException(message: String? = null) :
        EiffelFlowException(message ?: "Task not found")

}