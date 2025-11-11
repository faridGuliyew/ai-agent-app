package com.example.agentapp.data.model.response.agent

import kotlinx.serialization.Serializable

@Serializable
data class GenerateResponse(
    val response: String,
    val done: Boolean
)