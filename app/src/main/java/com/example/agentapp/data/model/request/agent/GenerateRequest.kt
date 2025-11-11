package com.example.agentapp.data.model.request.agent

import kotlinx.serialization.Serializable

@Serializable
class GenerateRequest(
    val model: String,
    val prompt: String
)
