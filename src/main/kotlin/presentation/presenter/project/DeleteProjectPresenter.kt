package org.example.presentation.presenter.project


import org.example.domain.model.Project
import org.example.domain.usecase.project.DeleteProjectUseCase
import java.util.UUID

class DeleteProjectPresenter(
    private val deleteProjectUseCase: DeleteProjectUseCase
) {
    operator fun invoke(){
        println("DELETE PROJECT")
        val input = readln().trim()
        val projectId = UUID.fromString(input)
        deleteProjectUseCase.deleteProject(projectId).fold(
            onSuccess = { project->
                showDeletedProjectDetails(project)
            },
            onFailure = { throwable ->
                println(throwable.message)
            }
        )

    }
    private fun showDeletedProjectDetails(project: Project){
        println("the project id is ${project.projectId}" )
        println("the project name is ${project.projectName}")
        println("the project description is ${ project.projectDescription }")
        println("the responsible admin Id is: ${project.adminId}")
        println("the project was created at: ${project.createdAt}")
        println("the task state is: ${project.taskStates}")

    }
}