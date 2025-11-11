package com.example.agentapp.domain.repository

import com.example.agentapp.domain.model.agent.InstructionModel

interface AgentRepository {
    suspend fun resolveInput(input: String) : Result<InstructionModel>
}