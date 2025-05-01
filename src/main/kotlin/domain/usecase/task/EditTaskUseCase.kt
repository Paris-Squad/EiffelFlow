package org.example.domain.usecase.task

import org.example.common.Constants
import org.example.domain.model.Task
import org.example.domain.model.User
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.TaskRepository

class EditTaskUseCase(private val taskRepository: TaskRepository) {
    fun editTask(request: Task, editor: User): Result<Task> {
        val taskResult = taskRepository.getTaskById(request.taskId)
        if (taskResult.isFailure) return taskResult

        val originalTask = taskResult.getOrThrow()
        if (originalTask == request) return Result.failure(EiffelFlowException.NoChangesException())

        val changedField = detectChangedField(originalTask, request)

        return taskRepository.updateTask(
            task = request,
            oldTask = originalTask,
            editor = editor,
            changedField = changedField
        )
    }

    private fun detectChangedField(original: Task, updated: Task): String {
        val changes = mutableListOf<Constants.TaskField>()

        if (original.title != updated.title) changes.add(Constants.TaskField.TITLE)
        if (original.description != updated.description) changes.add(Constants.TaskField.DESCRIPTION)
        if (original.assignedId != updated.assignedId) changes.add(Constants.TaskField.ASSIGNEE)
        if (original.state != updated.state) changes.add(Constants.TaskField.STATE)
        if (original.role != updated.role) changes.add(Constants.TaskField.ROLE)
        if (original.projectId != updated.projectId) changes.add(Constants.TaskField.PROJECT)

        return changes.joinToString(", ") { it.displayName }
    }
}