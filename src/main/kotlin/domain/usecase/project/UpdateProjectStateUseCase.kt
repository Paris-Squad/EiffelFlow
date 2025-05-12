package org.example.domain.usecase.project

import org.example.data.utils.SessionManger
import org.example.domain.exception.EiffelFlowException
import org.example.domain.mapper.toAuditLog
import org.example.domain.model.AuditLogAction
import org.example.domain.model.Project
import org.example.domain.repository.AuditRepository
import org.example.domain.repository.ProjectRepository
import java.util.UUID

class UpdateProjectStateUseCase(
    private val projectRepository: ProjectRepository,
    private val auditRepository: AuditRepository
) {

    suspend fun execute(projectId: UUID, stateId: UUID, newName: String): Project {
        val originalProject = projectRepository.getProjectById(projectId)

        val index = originalProject.taskStates.indexOfFirst { it.stateId == stateId }
        if (index == -1){
            throw EiffelFlowException.NotFoundException("State with id '${stateId}' not found")
        }

        val updatedStates = originalProject.taskStates.toMutableList()
            .apply {
                this[index] = this[index].copy(name = newName)
            }

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
            actionType = AuditLogAction.UPDATE,
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
