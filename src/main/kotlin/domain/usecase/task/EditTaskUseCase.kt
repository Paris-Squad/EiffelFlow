package org.example.domain.usecase.task

import org.example.domain.model.Task
import org.example.domain.exception.EiffelFlowException
import org.example.domain.repository.TaskRepository

class EditTaskUseCase(private val taskRepository: TaskRepository) {
    suspend fun editTask(request: Task): Task {
        val taskResult = taskRepository.getTaskById(request.taskId)

        if (taskResult == request) throw EiffelFlowException.IOException("No changes detected")

        val changedField = detectChangedField(taskResult, request)

        return taskRepository.updateTask(
            task = request,
            oldTask = taskResult,
            changedField = changedField
        )
    }

    private fun detectChangedField(original: Task, updated: Task): String {
        val changes = mutableListOf<String>()

        if (original.title != updated.title) changes.add("TITLE")
        if (original.description != updated.description) changes.add("DESCRIPTION")
        if (original.assignedId != updated.assignedId) changes.add("ASSIGNEE")
        if (original.state != updated.state) changes.add("STATE")
        if (original.role != updated.role) changes.add("ROLE")
        if (original.projectId != updated.projectId) changes.add("PROJECT")

        return changes.joinToString(", ") { it }
    }
}
