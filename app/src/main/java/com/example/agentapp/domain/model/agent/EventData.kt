package com.example.agentapp.domain.model.agent

import kotlinx.serialization.Serializable

@Serializable
sealed interface EventData {
    @Serializable
    data class Navigation(val targetRoute: String) : EventData

    @Serializable
    data class ModifyNote(
        val currentTitle: String? = null,
        val newTitle: String? = null,
        val newContent: String? = null,
        val isFavorite: Boolean? = null,
        val operationType: NoteOperationType
    ) : EventData

    @Serializable
    data class UpdateField(
        val key: UpdateFieldId, // Possible values: SEARCH, BIO, USERNAME
        val value: String
    ) : EventData

    @Serializable
    data class View(
        val key: ViewFieldId, // Possible values: NOTE_TITLE
        val value: String
    ) : EventData

}