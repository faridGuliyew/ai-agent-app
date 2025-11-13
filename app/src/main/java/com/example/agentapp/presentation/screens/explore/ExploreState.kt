package com.example.agentapp.presentation.screens.explore

import com.example.agentapp.presentation.Note

data class ExploreState(
    val query: String = "",
    val allNotes: List<Note> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val favorites: List<Note> = emptyList(),
)