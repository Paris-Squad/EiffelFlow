package org.example.domain.model.exception

abstract class EiffelFlowException(message: String?) : Exception(message) {

    class UserCreationException(message: String? = null) : EiffelFlowException(message ?: "Failed to create user")

    class UserStorageException(message: String? = null) :
        EiffelFlowException(message ?: "User storage operation failed")

    class PasswordValidationException(errors: Set<String>) :
        EiffelFlowException("Password validation failed: ${errors.joinToString(", ")}")

    class UserNameValidationException(errors: Set<String>) :
        EiffelFlowException("Username validation failed: ${errors.joinToString(", ")}")

    class UsernameAlreadyExistsException : EiffelFlowException("Username already exists")

    class UnauthorizedRegistrationException : EiffelFlowException("Only admins can register new users")

    class ElementNotFoundException(message: String) : EiffelFlowException(message)

    class ProjectCreationException(message: String) : EiffelFlowException(message)


}
