package org.widmerkillenberger.backend.model.dto

data class WorkflowRuleDTO(
    val id: String?,
    val fromColumnId: String,
    val toColumnIds: List<String>
)

data class WorkflowRuleResponse(
    val id: String,
    val fromColumnId: String,
    val toColumnIds: List<String>
)

data class CreateWorkflowRuleRequest(
    val fromColumnId: String,
    val toColumnIds: List<String>
)

data class UpdateWorkflowRuleRequest(
    val fromColumnId: String? = null,
    val toColumnIds: List<String>? = null
)
