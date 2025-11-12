package com.example.agentapp.presentation

import java.time.LocalDateTime

data class Note(
    val id: String,
    val authorUsername: String,
    val title: String,
    val content: String,
    val isFavorite: Boolean,
    val lastUpdated: LocalDateTime
)