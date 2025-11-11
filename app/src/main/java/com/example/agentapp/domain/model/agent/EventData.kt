package com.example.agentapp.domain.model.agent

import kotlinx.serialization.Serializable

@Serializable
sealed interface EventData {
    @Serializable
    data class Navigation(val targetRoute: String) : EventData
}