package com.example.agentapp.domain.model.agent

import com.example.agentapp.data.serializer.AgentActionSerializer
import kotlinx.serialization.Serializable

@Serializable(with = AgentActionSerializer::class)
data class AgentAction(
    val event: InstructionEvent,
    val data: EventData
)