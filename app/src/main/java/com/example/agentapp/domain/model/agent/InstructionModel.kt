package com.example.agentapp.domain.model.agent

import kotlinx.serialization.Serializable

@Serializable
data class InstructionModel (
    val actions: List<AgentAction>,
    val failReason: String? = null
)