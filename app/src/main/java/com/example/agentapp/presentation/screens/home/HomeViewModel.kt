package com.example.agentapp.presentation.screens.home

import androidx.lifecycle.viewModelScope
import com.example.agentapp.domain.model.agent.EventData
import com.example.agentapp.domain.model.agent.NoteOperationType
import com.example.agentapp.domain.model.agent.ViewFieldId
import com.example.agentapp.domain.model.agent.supportedRoutes
import com.example.agentapp.presentation.AgentViewModel
import com.example.agentapp.presentation.Note
import com.example.agentapp.presentation.navigation.Routes.HOME
import com.example.agentapp.presentation.navigation.Routes.PROFILE
import com.example.agentapp.utils.InstructionChannel
import com.example.agentapp.utils.findByTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

val favorites = MutableStateFlow<List<Note>>(emptyList())

class HomeViewModel (
    instructionChannel: InstructionChannel<EventData, String>
) : AgentViewModel(
    instructionChannel = instructionChannel,
    screenRoute = HOME
) {

    val state = MutableStateFlow(HomeState())

    init {
        observeFavorites()
    }

    fun showNoteEditor(originalNote: Note? = null) {
        val editorState = NoteEditorState(
            id = originalNote?.id,
            title = originalNote?.title.orEmpty(),
            content = originalNote?.content.orEmpty(),
            isFavorite = originalNote?.isFavorite ?: false
        )

        state.update { it.copy(noteEditorState = editorState) }
    }

    fun dismissNoteEditor() {
        state.update { it.copy(noteEditorState = null) }
    }

    fun updateNoteEditorState(newState: NoteEditorState) {
        state.update { it.copy(noteEditorState = newState) }
    }

    fun viewNote(note: Note) {
        state.update { it.copy(viewedNote = note) }
    }

    fun dismissViewedNote() {
        state.update { it.copy(viewedNote = null) }
    }

    fun saveNoteEditor() {
        val editorState = state.value.noteEditorState ?: return

        state.update {
            it.copy(
                notes = it.notes.toMutableList().apply {
                    when (editorState.id) {
                        null -> { // Create new note
                            add(
                                Note(
                                    title = editorState.title,
                                    content = editorState.content,
                                    authorUsername = state.value.username,
                                    id = UUID.randomUUID().toString(),
                                    isFavorite = editorState.isFavorite,
                                    lastUpdated = LocalDateTime.now()
                                )
                            )
                        }
                        else -> { // Update existing note
                            val index = indexOfFirst { it.id == editorState.id }.takeIf { it != -1 } ?: return@apply
                            this[index] = this[index].copy(
                                title = editorState.title,
                                content = editorState.content,
                                isFavorite = editorState.isFavorite,
                                lastUpdated = LocalDateTime.now()
                            )
                        }
                    }
                }
            )
        }

        dismissNoteEditor()
    }

    fun deleteNote(note: Note) {
        state.update {
            val updatedNotes = it.notes.toMutableList().apply {
                removeIf { it.id == note.id }
            }

            it.copy(notes = updatedNotes)
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
        viewModelScope.launch (Dispatchers.IO) {
            favorites.collectLatest { fav->
                val favoriteIds = fav.map { it.id }.toSet()
                state.update {
                    it.copy(
                        favorites = fav,
                        notes = it.notes.map {
                            if (it.id in favoriteIds) it.copy(isFavorite = true)
                            else return@map it.copy(isFavorite = false)
                        }
                    )
                }
            }
        }
    }

    // Agent handler methods
    override suspend fun handleAgentEvent(data: EventData): String? {
        return when (data) {
            is EventData.View -> viewField(data)
            is EventData.ModifyNote -> modifyNote(data)
            else -> null
        }
    }

    fun modifyNote(data: EventData.ModifyNote) : String? {
        if (HOME !in data.operationType.supportedRoutes) return null

        return when (data.operationType) {
            NoteOperationType.CREATE -> {
                updateNoteEditorState(
                    newState = NoteEditorState(
                        title = data.newTitle ?: "Unnamed",
                        content = data.newContent ?: "No content",
                        isFavorite = data.isFavorite ?: false
                    )
                )
                saveNoteEditor()

                "Note with title ${data.newTitle} added"
            }
            NoteOperationType.UPDATE -> {
                val note = state.value.notes.findByTitle(data.currentTitle) ?: return "Note with title ${data.currentTitle} not found."
                updateNoteEditorState(
                    newState = NoteEditorState(
                        id = note.id,
                        title = data.newTitle ?: note.title,
                        content = data.newContent ?: note.content,
                        isFavorite = data.isFavorite ?: note.isFavorite
                    )
                )
                saveNoteEditor()
                "Note with title ${data.currentTitle} updated"
            }
            NoteOperationType.DELETE -> {
                val note = state.value.notes.findByTitle(data.currentTitle) ?: return "Note with title ${data.currentTitle} not found."

                deleteNote(note)
                "Note with title ${data.currentTitle} deleted"
            }
        }
    }

    fun viewField(data: EventData.View) : String? {
        if (HOME !in data.key.supportedRoutes) return null

        when (data.key) {
            ViewFieldId.NOTE_TITLE -> {
                val note = state.value.favorites.find { it.title.uppercase() == data.value.uppercase() } ?: return "Note with title ${data.value} not found"
                viewNote(note)
            }
        }

        return "Viewed ${data.value}"
    }

}