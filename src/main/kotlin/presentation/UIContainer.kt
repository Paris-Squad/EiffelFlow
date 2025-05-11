package org.example.presentation

import org.example.presentation.audit.GetAuditLogsCLI
import org.example.presentation.audit.GetProjectAuditLogsCLI
import org.example.presentation.audit.GetTaskAuditLogsCLI
import org.example.presentation.auth.LoginCLI
import org.example.presentation.auth.LogoutCLI
import org.example.presentation.user.CreateUserCLI
import org.example.presentation.project.CreateProjectCLI
import org.example.presentation.project.DeleteProjectCLI
import org.example.presentation.project.GetProjectCLI
import org.example.presentation.project.UpdateProjectCLI
import org.example.presentation.task.CreateTaskCLI
import org.example.presentation.task.DeleteTaskCLI
import org.example.presentation.task.EditTaskCli
import org.example.presentation.user.UpdateUserCLI

data class UIContainer(
    val getProjectAuditLogsCLI: GetProjectAuditLogsCLI,
    val loginCLI: LoginCLI,
    val registerCLI: CreateUserCLI,
    val createProjectCLI: CreateProjectCLI,
    val deleteProjectCLI: DeleteProjectCLI,
    val getProjectCLI: GetProjectCLI,
    val updateProjectCLI: UpdateProjectCLI,
    val createTaskCLI: CreateTaskCLI,
    val editTaskCli: EditTaskCli,
    val deleteTaskCLI: DeleteTaskCLI,
    val logoutCLI: LogoutCLI,
    val getTaskAuditLogsCLI: GetTaskAuditLogsCLI,
    val getAuditLogsCLI: GetAuditLogsCLI,
    val updateUserCLI: UpdateUserCLI
)