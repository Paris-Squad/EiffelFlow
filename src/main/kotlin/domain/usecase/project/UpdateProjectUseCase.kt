package org.example.domain.usecase.project

import org.example.domain.exception.EiffelFlowException
import org.example.domain.model.Project
import org.example.domain.repository.ProjectRepository

class UpdateProjectUseCase(private val repository: ProjectRepository) {

    @Throws(EiffelFlowException::class)
    suspend fun updateProject(updatedProject: Project): Project {

        val project = repository.getProjectById(updatedProject.projectId)

        if (project == updatedProject) {
            throw EiffelFlowException.IOException("No changes detected")
        }

        val changedField = detectChangedField(project, updatedProject)
        return repository.updateProject(
            project = updatedProject,
            oldProject = project,
            changedField = changedField
        )
    }

    private fun detectChangedField(original: Project, updated: Project): String {
        val changes = mutableListOf<String>()
        if (original.projectName != updated.projectName) changes.add("PROJECT_NAME")
        if (original.projectDescription != updated.projectDescription) changes.add("PROJECT_DESCRIPTION")
        if (original.adminId != updated.adminId) changes.add("ADMIN_ID")
        if (original.taskStates != updated.taskStates) changes.add("TASK_STATES")

        return changes.joinToString(", ") { it }
    }
}