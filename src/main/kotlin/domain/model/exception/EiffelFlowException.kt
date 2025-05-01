package org.example.domain.model.exception

import org.example.common.Constants

abstract class EiffelFlowException(message: String?) : Exception(message) {

    class UserCreationException(message: String? = null) : EiffelFlowException(message ?: "Failed to create user")

    class UserStorageException(message: String? = null) :
        EiffelFlowException(message ?: "User storage operation failed")

    class PasswordValidationException(errors: Set<Constants.ValidationRule>) :
        EiffelFlowException("Password validation failed: ${errors.joinToString(", ") { it.message }}")

    class UserNameValidationException(errors: Set<Constants.ValidationRule>) :
        EiffelFlowException("Username validation failed: ${errors.joinToString(", ") { it.message }}")

    class UsernameAlreadyExistsException : EiffelFlowException("Username already exists")
    class UnableToDeleteProjectException : EiffelFlowException("unable to delete project")
    class UnableToCreateAuditLogException : EiffelFlowException("unable to create AuditLog")
    class UnableToFindTheCorrectProject : EiffelFlowException("unable to find project")

    class UnauthorizedRegistrationException : EiffelFlowException("Only admins can register new users")

    class ElementNotFoundException(message: String) : EiffelFlowException(message)
    class ProjectCreationException(message: String) : EiffelFlowException(message)
     class TaskNotFoundException(message: String? = null) :
        EiffelFlowException(message ?: "Task not found")

    class NoChangesException(message: String? = null) :
        EiffelFlowException(message ?: "No changes detected")
    class TaskDeletionException : EiffelFlowException("An error occurred while deleting the task")


}

