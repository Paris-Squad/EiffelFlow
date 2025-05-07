package org.example.presentation.view.project

import org.example.presentation.presenter.project.GetProjectPresenter
import java.util.UUID

class GetProjectCLI(
    private val presenter: GetProjectPresenter
) {
    fun displayProject(projectId: UUID) {
        presenter.getProjectById(projectId)
    }

    fun displayProjects() {
        presenter.getProjects()
    }
}