package com.example.agentapp.domain.model.agent

import com.example.agentapp.presentation.navigation.Routes.EXPLORE
import com.example.agentapp.presentation.navigation.Routes.PROFILE

enum class UpdateFieldId {
    BIO, USERNAME, SEARCH
}

val UpdateFieldId.supportedRoutes get() = when (this) {
    UpdateFieldId.BIO -> setOf(PROFILE)
    UpdateFieldId.USERNAME -> setOf(PROFILE)
    UpdateFieldId.SEARCH -> setOf(EXPLORE)
}