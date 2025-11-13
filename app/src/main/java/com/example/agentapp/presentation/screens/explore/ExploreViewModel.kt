package com.example.agentapp.presentation.screens.explore

import androidx.lifecycle.viewModelScope
import com.example.agentapp.domain.model.agent.EventData
import com.example.agentapp.domain.model.agent.UpdateFieldId
import com.example.agentapp.domain.model.agent.supportedRoutes
import com.example.agentapp.presentation.AgentViewModel
import com.example.agentapp.presentation.Note
import com.example.agentapp.presentation.navigation.Routes.EXPLORE
import com.example.agentapp.presentation.screens.home.favorites
import com.example.agentapp.utils.InstructionChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class ExploreViewModel (
    instructionChannel: InstructionChannel<EventData, String>
): AgentViewModel(
    instructionChannel = instructionChannel,
    screenRoute = EXPLORE
) {

    val state = MutableStateFlow(ExploreState())

    init {
        loadAllNotes()
        observeFilterNotes()
        observeFavorites()
    }

    fun updateQuery(value: String) {
        state.update { it.copy(query = value) }
    }

    fun loadAllNotes() {
        val allNotes = listOf(
            Note(
                id = UUID.randomUUID().toString(),
                authorUsername = "alice",
                title = "Kotlin Tips",
                content = "Use data classes and sealed interfaces.",
                isFavorite = false,
                lastUpdated = LocalDateTime.now()
            ),
            Note(
                id = UUID.randomUUID().toString(),
                authorUsername = "bob",
                title = "Compose Basics",
                content = "State hoisting is key. Remember to remember.",
                isFavorite = false,
                lastUpdated = LocalDateTime.now()
            )
        )
        state.update {
            it.copy(
                allNotes = allNotes,
                filteredNotes = allNotes
            )
        }
    }

    fun observeFilterNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            state.distinctUntilChangedBy { it.query }.collectLatest { s ->
                val filteredNotes = buildList {
                    for (note in s.allNotes) {
                        if (note.run {
                                title.contains(s.query, ignoreCase = true) ||
                                        content.contains(s.query, ignoreCase = true) ||
                                        authorUsername.contains(s.query, ignoreCase = true)
                            }) add(note)
                    }
                }

                state.update { it.copy(filteredNotes = filteredNotes) }

                val favoriteIds = s.favorites.map { it.id }.toSet()
                state.update {
                    it.copy(
                        filteredNotes = it.filteredNotes.map {
                            if (it.id in favoriteIds) it.copy(isFavorite = true)
                            else return@map it.copy(isFavorite = false)
                        }
                    )
                }
            }
        }
    }

    fun toggleFavorite(note: Note) {
        var favIndex: Int? = null

        val favoriteNotes = state.value.favorites.toMutableList()
        for (i in favoriteNotes.indices) {
            if (favoriteNotes[i].id == note.id) {
                favIndex = i
                break
            }
        }

        val isFavorite = favIndex != null

        if (isFavorite) {
            removeFavoriteNote(favIndex)
        } else {
            addFavoriteNote(note)
        }
    }

    fun addFavoriteNote(note: Note) {
        favorites.update { it.toMutableList().apply { add(note) } }
    }

    fun removeFavoriteNote(index: Int) {
        favorites.update { it.toMutableList().apply { removeAt(index) } }
    }

    fun observeFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            favorites.collectLatest { fav ->

                val favoriteIds = fav.map { it.id }.toSet()
                state.update {
                    it.copy(
                        filteredNotes = it.filteredNotes.map {
                            if (it.id in favoriteIds) it.copy(isFavorite = true)
                            else return@map it.copy(isFavorite = false)
                        }
                    )
                }

                state.update { it.copy(favorites = fav) }
            }
        }
    }

    // Agent handler methods
    override suspend fun handleAgentEvent(data: EventData): String? {
        return when (data) {
            is EventData.UpdateField -> updateField(data)
            else -> null
        }
    }
    fun updateField(data: EventData.UpdateField) : String? {
        if (EXPLORE !in data.key.supportedRoutes) return null

        when (data.key) {
            UpdateFieldId.SEARCH -> {
                updateQuery(data.value)
            }
            else -> return null
        }

        return "Updated ${data.key} field in $EXPLORE screen"
    }
}