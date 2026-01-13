package org.widmerkillenberger.backend.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "workflow_rules")
data class WorkflowRule(
    @Id
    val id: String? = null,
    val fromColumnId: String,
    val toColumnIds: List<String>
)