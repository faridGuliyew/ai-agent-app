package com.example.agentapp.domain.model.agent

import kotlinx.serialization.Serializable

@Serializable
sealed interface EventData {
    @Serializable
    data class Navigation(val targetRoute: String) : EventData

    @Serializable
    data class AddNote(
        val title: String? = null,
        val content: String? = null
    ) : EventData

    @Serializable
    data class UpdateNote(
        val pastTitle: String,
        val newTitle: String? = null,
        val newContent: String? = null,
        val isFavorite: Boolean? = null
    ) : EventData

    @Serializable
    data class DeleteNote(
        val noteTitle: String
    ) : EventData

    @Serializable
    data class UpdateField(
        val fieldId: String, // Possible values: SEARCH, BIO, USERNAME
        val newValue: String
    ) : EventData

    @Serializable
    data class View(
        val key: String, // Possible values: NOTE_TITLE
        val value: String
    ) : EventData

}