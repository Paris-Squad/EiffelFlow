package utils

import kotlinx.datetime.LocalDateTime
import org.example.domain.model.entities.AuditAction
import org.example.domain.model.entities.AuditLog
import java.util.UUID

object MockAuditLog {
    val AUDIT_LOG = AuditLog(
        auditId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5748"),
        itemId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5740"),
        itemName = "Task",
        userId = UUID.fromString("02ad4499-5d4c-4450-8fd1-8294f1bb5741"),
        userName = "User1",
        actionType = AuditAction.CREATE,
        auditTime = LocalDateTime.parse("1999-08-07T22:22:22"),
        changedField = "Title",
        oldValue = "OldTitle",
        newValue = "NewTitle"
    )

    const val FULL_CSV_STRING_LINE =
        "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,Title,OldTitle,NewTitle"
    const val CSV_STRING_LINE_WITH_NEW_VALUE_NULL =
        "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,Title,OldTitle,"
    const val CSV_STRING_LINE_WITH_OLD_VALUE_NULL =
        "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,Title,,NewTitle"
    const val CSV_STRING_LINE_WITH_CHANGED_FIELD_NULL =
        "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,,OldTitle,NewTitle"
    const val CSV_STRING_LINE_MISSING_CHANGED_FIELD_AND_OLD_VALUE_AND_NEW_VALUE =
        "02ad4499-5d4c-4450-8fd1-8294f1bb5748,02ad4499-5d4c-4450-8fd1-8294f1bb5740,Task,02ad4499-5d4c-4450-8fd1-8294f1bb5741,User1,CREATE,1999-08-07T22:22:22,,,"
}