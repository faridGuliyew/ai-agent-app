package com.example.agentapp.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agentapp.presentation.Note
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen() {
	val viewModel = koinViewModel<HomeViewModel>()
	val state by viewModel.state.collectAsStateWithLifecycle()

	Scaffold(
		floatingActionButton = {
			FloatingActionButton(onClick = viewModel::showNoteEditor) {
				Icon(Icons.Default.Add, contentDescription = "Add")
			}
		}
	) { inner ->
		Surface(modifier = Modifier.fillMaxSize().padding(inner)) {
			Column(modifier = Modifier.fillMaxSize()) {
				NotesList(
					notes = state.notes,
					onOpen = viewModel::viewNote,
					onEdit = viewModel::showNoteEditor,
					onDelete = viewModel::deleteNote,
					onToggleFavorite = viewModel::toggleFavorite
				)
			}
		}
	}

	AnimatedVisibility(
		visible = state.noteEditorState != null,
		enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
		exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
	) {
		state.noteEditorState?.let {
			ComposeNoteEditor(
				state = it,
				onTitleChange = { title->
					viewModel.updateNoteEditorState(it.copy(title = title))
				},
				onContentChange = { content->
					viewModel.updateNoteEditorState(it.copy(content = content))
				},
				onCancel = viewModel::dismissNoteEditor,
				onSave = viewModel::saveNoteEditor
			)
		}
	}

	state.viewedNote?.let { note ->
		AlertDialog(
			onDismissRequest = viewModel::dismissViewedNote,
			confirmButton = {
				TextButton(onClick = viewModel::dismissViewedNote) { Text("Close") }
			},
			title = { Text(note.title) },
			text = {
				Column {
					Text("by @${note.authorUsername} • ${note.lastUpdated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}", style = MaterialTheme.typography.labelMedium)
					Spacer(modifier = Modifier.height(8.dp))
					Text(note.content, style = MaterialTheme.typography.bodyLarge)
				}
			}
		)
	}
}

@Composable
private fun NotesList(
    notes: List<Note>,
    onOpen: (Note) -> Unit,
    onEdit: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onToggleFavorite: (Note) -> Unit
) {
	if (notes.isEmpty()) {
		EmptyState()
		return
	}

	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(16.dp)
	) {
		itemsIndexed(notes) { index, note ->
			NoteRow(
				index = index,
				note = note,
				onOpen = onOpen,
				onEdit = onEdit,
				onDelete = onDelete,
				onToggleFavorite = onToggleFavorite
			)
			Spacer(modifier = Modifier.height(12.dp))
		}
	}
}

@Composable
private fun NoteRow(
    note: Note,
    index: Int,
    onOpen: (Note) -> Unit,
    onEdit: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onToggleFavorite: (Note) -> Unit
) {
	Surface(
		tonalElevation = 2.dp,
		shadowElevation = 1.dp,
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(16.dp)
	) {
		Column(modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surfaceContainerHighest)
			.clickable { onOpen(note) }
			.padding(16.dp)) {
			Row(modifier = Modifier.fillMaxWidth()) {
				Text(
					text = note.title,
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier.weight(1f),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				val scale by animateFloatAsState(
					targetValue = if (note.isFavorite) 1.2f else 1f,
					animationSpec = tween(200, easing = FastOutSlowInEasing),
					label = "favScale"
				)
				IconButton(onClick = { onToggleFavorite(note) }, modifier = Modifier.scale(scale)) {
					Icon(
						Icons.Default.Favorite,
						contentDescription = "Favorite",
						tint = if (note.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
				IconButton(onClick = { onEdit(note) }) {
					Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
				}
				IconButton(onClick = { onDelete(note) }) {
					Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
				}
			}
			Text(
				text = "by @${note.authorUsername} • ${note.lastUpdated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}",
				style = MaterialTheme.typography.labelMedium
			)
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = note.content,
				style = MaterialTheme.typography.bodyMedium,
				maxLines = 4,
				overflow = TextOverflow.Ellipsis
			)
			Spacer(modifier = Modifier.height(8.dp))
		}
	}
}

@Composable
private fun ComposeNoteEditor(
	state: NoteEditorState,
	onTitleChange: (String) -> Unit,
	onContentChange: (String) -> Unit,
	onCancel: () -> Unit,
	onSave: () -> Unit
) {
	Surface(
		tonalElevation = 8.dp,
		shadowElevation = 8.dp,
		shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.surface)
			.padding(16.dp)) {
			Text(text = "Note Editor", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
			Spacer(modifier = Modifier.height(8.dp))
			OutlinedTextField(
				value = state.title,
				onValueChange = onTitleChange,
				modifier = Modifier.fillMaxWidth(),
				label = { Text("Title") }
			)
			Spacer(modifier = Modifier.height(8.dp))
			OutlinedTextField(
				value = state.content,
				onValueChange = onContentChange,
				modifier = Modifier.fillMaxWidth().height(120.dp),
				label = { Text("Content") }
			)
			Spacer(modifier = Modifier.height(8.dp))
			Row {
				TextButton(onClick = onCancel) { Text("Cancel") }
				Spacer(modifier = Modifier.height(0.dp))
				Button(onClick = onSave) { Text("Save") }
			}
		}
	}
}

@Composable
private fun EmptyState() {
	Surface(
		tonalElevation = 1.dp,
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		shape = RoundedCornerShape(20.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.surfaceVariant)
				.padding(24.dp),
			verticalArrangement = Arrangement.Center
		) {
			Text(
				text = "No notes yet",
				style = MaterialTheme.typography.headlineSmall,
				color = MaterialTheme.colorScheme.primary
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = "Tap the + button to create your first note. You can favorite, edit, and read them in full.",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
	}
}