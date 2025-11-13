package com.example.agentapp.presentation.screens.home

import com.example.agentapp.presentation.Note

data class HomeState (
    val notes: List<Note> = emptyList(),
    val favorites: List<Note> = emptyList(),
    val username: String = "",
    val noteEditorState: NoteEditorState? = null,
    val viewedNote: Note? = null
)

data class NoteEditorState (
    val id: String? = null,
    val title: String,
    val content: String,
    val isFavorite: Boolean
)