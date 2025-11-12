package com.example.agentapp.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import java.time.LocalDateTime
import java.util.UUID

class AppViewModel : ViewModel() {
	lateinit var navController: NavHostController
	val username = mutableStateOf("johndoe")
	val bio = mutableStateOf("Just a placeholder bio for demo.")

	private val _notes = mutableStateListOf<Note>()
	val notes: List<Note> get() = _notes

	private val _favoriteNotes = mutableStateListOf<Note>()
	val favoriteNotes: List<Note> get() = _favoriteNotes

	// Mock other users' notes for Explore
	private val _discoverNotes = mutableStateListOf(
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
	val discoverNotes: List<Note> get() = _discoverNotes

	fun createNote(title: String, content: String) {
		_notes.add(
			Note(
				id = UUID.randomUUID().toString(),
				authorUsername = username.value,
				title = title,
				content = content,
				isFavorite = false,
				lastUpdated = LocalDateTime.now()
			)
		)
	}

	fun updateNote(id: String, title: String?, content: String?, isFavorite: Boolean?) {
		val index = _notes.indexOfFirst { it.id == id }
		if (index != -1) {
			val existing = _notes[index]
			_notes[index] = existing.copy(
				title = title ?: existing.title,
				content = content ?: existing.content,
				isFavorite = isFavorite ?: existing.isFavorite,
				lastUpdated = LocalDateTime.now()
			)
		}
	}

	fun updateNoteByTitle(prevTitle: String, title: String?, content: String?, isFavorite: Boolean?) {
		val note = _notes.find { it.title == prevTitle } ?: return
		updateNote(note.id, title, content, isFavorite)
	}

	fun deleteNote(id: String) {
		_notes.removeAll { it.id == id }
	}

	fun deleteNoteByTitle(title: String) {
		_notes.removeAll { it.title.uppercase() == title.uppercase() }
	}

	fun toggleFavorite(note: Note, index: Int, isExplore: Boolean) {
		var favIndex : Int? = null
		for (i in favoriteNotes.indices) {
			if (favoriteNotes[i].id == note.id) {
				favIndex = i
				break
			}
		}

		val isFavorite = favIndex != null

		if (isFavorite) {
			_favoriteNotes.removeAt(favIndex)
		} else {
			_favoriteNotes.add(note)
		}

		val notesList = if (isExplore) _discoverNotes else _notes
		notesList[index] = notesList[index].copy(isFavorite = !note.isFavorite)
	}
}


