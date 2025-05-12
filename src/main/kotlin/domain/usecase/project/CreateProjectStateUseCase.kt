package org.example.domain.usecase.project

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Project
import org.example.domain.model.TaskState
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class CreateProjectStateUseCase(
    private val projectRepository: ProjectRepository,
    private val auditRepository: AuditRepository
) {
    @Throws(EiffelFlowException::class)
    suspend fun execute(projectId: UUID, newState: TaskState): Project {

        val originalProject = projectRepository.getProjectById(projectId)

        if (originalProject.taskStates.any { it.name == newState.name }) {
            throw EiffelFlowException.StateAlreadyExist("State with name '${newState.name}' already exists")
        }

        val updatedStates = originalProject.taskStates + newState
        val newProject = originalProject.copy(taskStates = updatedStates)

        val updatedProject = projectRepository.updateProject(
            project = newProject,
            oldProject = originalProject,
            changedField = CHANGED_FIELD
        )

        logAction(
            newProject = updatedProject,
            oldProject = originalProject,
            hangedFiled = CHANGED_FIELD
        )

        return updatedProject
    }

    private suspend fun logAction(
        newProject: Project,
        oldProject: Project,
        hangedFiled: String
    ) {
        val auditLog = newProject.toAuditLog(
            editor = SessionManger.getUser(),
            actionType = AuditLogAction.CREATE,
            changedField = hangedFiled,
            oldValue = oldProject.taskStates.toString(),
            newValue = newProject.taskStates.toString()
        )
        auditRepository.createAuditLog(auditLog)
    }

    companion object {
        private const val CHANGED_FIELD = "TASK_STATES"
    }
}