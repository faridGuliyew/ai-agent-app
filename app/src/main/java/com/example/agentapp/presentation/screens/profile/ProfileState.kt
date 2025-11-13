package com.example.agentapp.presentation.screens.profile

import com.example.agentapp.presentation.Note

data class ProfileState (
    val username: String = "johndoe",
    val bio: String = "You do not have a bio yet.",
    val favorites: List<Note> = emptyList(),
    val viewedNote: Note? = null,
    val editState: EditState? = null
)

data class EditState(
    val username: String,
    val bio: String
)