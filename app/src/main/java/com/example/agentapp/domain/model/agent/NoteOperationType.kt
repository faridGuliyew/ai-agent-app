package com.example.agentapp.domain.model.agent

import com.example.agentapp.presentation.navigation.Routes.HOME

enum class NoteOperationType {
    CREATE, UPDATE, DELETE
}

val NoteOperationType.supportedRoutes get() = setOf(HOME)
//when (this) {
//    NoteOperationType.CREATE -> setOf(HOME)
//    NoteOperationType.UPDATE -> setOf(HOME)
//    NoteOperationType.DELETE -> setOf(HOME)
//}