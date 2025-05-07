/*
package org.example.presentation.view.project

import org.example.domain.model.Project
import org.example.presentation.presenter.project.DeleteProjectPresenter
import java.util.UUID

class DeleteProjectCLI(private val deleteProjectPresenter: DeleteProjectPresenter) {

    operator fun invoke(projectId1: UUID) {
        println("DELETE PROJECT")
        val input = readln().trim()
        val projectId = UUID.fromString(input)
        val deletedProject = deleteProjectPresenter.deleteProject(projectId)
        showDeletedProjectDetails(deletedProject)
    }

    private fun showDeletedProjectDetails(project: Project) {
        println("the project id is ${project.projectId}")
        println("the project name is ${project.projectName}")
        println("the project description is ${project.projectDescription}")
        println("the responsible admin Id is: ${project.adminId}")
        println("the project was created at: ${project.createdAt}")
        println("the task state is: ${project.taskStates}")

    }
}*/
