package com.example.agentapp.domain.model.agent

import com.example.agentapp.presentation.navigation.Routes.HOME
import com.example.agentapp.presentation.navigation.Routes.PROFILE

enum class ViewFieldId {
    NOTE_TITLE
}

val ViewFieldId.supportedRoutes get() = when (this) {
    ViewFieldId.NOTE_TITLE -> setOf(PROFILE, HOME)
}