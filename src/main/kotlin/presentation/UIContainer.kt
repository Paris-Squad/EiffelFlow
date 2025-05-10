package org.example.presentation

import org.example.presentation.audit.GetProjectAuditLogsCLI
import org.example.presentation.auth.LoginCLI
import org.example.presentation.auth.RegisterCLI
import org.example.presentation.project.CreateProjectCLI
import org.example.presentation.project.DeleteProjectCLI
import org.example.presentation.project.GetProjectCLI
import org.example.presentation.project.UpdateProjectCLI
import org.example.presentation.task.CreateTaskCLI
import org.example.presentation.task.DeleteTaskCLI

data class UIContainer(
    val getProjectAuditLogsCLI: GetProjectAuditLogsCLI,
    val loginCLI: LoginCLI,
    val registerCLI: RegisterCLI,
    val createProjectCLI: CreateProjectCLI,
    val deleteProjectCLI: DeleteProjectCLI,
    val getProjectCLI: GetProjectCLI,
    val updateProjectCLI: UpdateProjectCLI,
    val createTaskCLI: CreateTaskCLI,
    val deleteTaskCLI: DeleteTaskCLI,
)