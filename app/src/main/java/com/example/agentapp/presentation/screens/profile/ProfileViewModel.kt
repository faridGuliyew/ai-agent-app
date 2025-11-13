package com.example.agentapp.presentation.screens.profile

import androidx.lifecycle.viewModelScope
import com.example.agentapp.domain.model.agent.EventData
import com.example.agentapp.domain.model.agent.UpdateFieldId
import com.example.agentapp.domain.model.agent.ViewFieldId
import com.example.agentapp.domain.model.agent.supportedRoutes
import com.example.agentapp.presentation.AgentViewModel
import com.example.agentapp.presentation.Note
import com.example.agentapp.presentation.navigation.Routes.PROFILE
import com.example.agentapp.presentation.screens.home.favorites
import com.example.agentapp.utils.InstructionChannel
import com.example.agentapp.utils.findByTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel (
    instructionChannel: InstructionChannel<EventData, String>
) : AgentViewModel(
    instructionChannel = instructionChannel,
    screenRoute = PROFILE
) {
    val state = MutableStateFlow(ProfileState())

    init {
        observeFavorites()
    }

    fun viewNote(note: Note) {
        state.update { it.copy(viewedNote = note) }
    }

    fun dismissViewedNote() {
        state.update { it.copy(viewedNote = null) }
    }

    fun dismissEditState() {
        state.update { it.copy(editState = null) }
    }

    fun toggleEditState() {
        state.update {
            if (it.editState != null) {
                it.copy(
                    username = it.editState.username,
                    bio = it.editState.bio,
                    editState = null
                )
            } else {
                it.copy(editState = EditState(username = it.username, bio = it.bio) )
            }
        }
    }

    fun updateEditState(newState: EditState) {
        state.update { it.copy(editState = newState) }
    }

    fun observeFavorites() {
        viewModelScope.launch (Dispatchers.IO) {
            favorites.collectLatest { fav->
                state.update { it.copy(favorites = fav) }
            }
        }
    }

    // Agent handler methods
    override suspend fun handleAgentEvent(data: EventData): String? {
        return when (data) {
            is EventData.UpdateField -> updateField(data)
            is EventData.View -> viewField(data)
            else -> null
        }
    }
    fun updateField(data: EventData.UpdateField) : String? {
        if (PROFILE !in data.key.supportedRoutes) return null

        when (data.key) {
            UpdateFieldId.BIO -> {
                state.update { it.copy(bio = data.value) }
            }
            UpdateFieldId.USERNAME -> {
                state.update { it.copy(username = data.value) }
            }
            else -> return null
        }

        return "Updated ${data.key} field in $PROFILE screen"
    }

    fun viewField(data: EventData.View) : String? {
        if (PROFILE !in data.key.supportedRoutes) return null

        when (data.key) {
            ViewFieldId.NOTE_TITLE -> {
                val note = state.value.favorites.findByTitle(data.value) ?: return "Note with title ${data.value} not found"
                viewNote(note)
            }
        }

        return "Viewed ${data.value}"
    }
}