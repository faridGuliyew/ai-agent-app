package com.example.agentapp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.agentapp.R
import com.example.agentapp.domain.model.agent.EventData
import com.example.agentapp.utils.FeedbackChannel
import com.example.agentapp.utils.profileFields
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(viewModel: AppViewModel) {
	var username by remember { mutableStateOf(viewModel.username.value) }
	var bio by remember { mutableStateOf(viewModel.bio.value) }
	val notes = viewModel.notes
	val favoriteNotes = viewModel.favoriteNotes
	var showNote by remember { mutableStateOf<Note?>(null) }
	var isEditing by remember { mutableStateOf(false) }
	val feedbackChannel: FeedbackChannel<EventData, String> = koinInject()

	fun updateField(data: EventData.UpdateField) : String? {
		if (data.fieldId !in profileFields) return null

		when (data.fieldId) {
			"BIO" -> {
				bio = data.newValue
				viewModel.bio.value = data.newValue
			}
			"USERNAME" -> {
				username = data.newValue
				viewModel.username.value = data.newValue
			}
		}

		return "OK"
	}

	DisposableEffect(Unit) {
		val tag = "PROFILE"
		feedbackChannel.addObserver(tag) {
			when (it) {
                is EventData.UpdateField -> updateField(it)
				else -> null
            }
		}
		onDispose {
			feedbackChannel.removeObserver(tag)
		}
	}

	Scaffold(
		topBar = {
			Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
				Text(
					text = "Profile",
					style = MaterialTheme.typography.titleLarge,
					modifier = Modifier.weight(1f)
				)
				IconButton(onClick = {
					if (isEditing) {
						viewModel.username.value = username
						viewModel.bio.value = bio
					}
					isEditing = !isEditing
				}) {
					if (isEditing) {
						Icon(Icons.Default.ThumbUp, contentDescription = "Save")
					} else {
						Icon(Icons.Default.Edit, contentDescription = "Edit")
					}
				}
			}
		}
	) { inner ->
		Surface(modifier = Modifier.fillMaxSize().padding(inner).padding(16.dp)) {
			Column {
				Image(
					painter = painterResource(id = R.drawable.ic_launcher_background),
					contentDescription = "Profile",
					contentScale = ContentScale.Crop
				)
				Spacer(modifier = Modifier.height(12.dp))
				if (isEditing) {
					OutlinedTextField(
						value = username,
						onValueChange = { username = it },
						label = { Text("Username") }
					)
					Spacer(modifier = Modifier.height(8.dp))
					OutlinedTextField(
						value = bio,
						onValueChange = { bio = it },
						label = { Text("Bio") }
					)
				} else {
					Text(text = "@$username", style = MaterialTheme.typography.titleLarge)
					Text(text = bio, style = MaterialTheme.typography.bodyMedium)
				}
				Spacer(modifier = Modifier.height(12.dp))
				Text(text = "Notes count: ${notes.size}", style = MaterialTheme.typography.bodyLarge)
				Spacer(modifier = Modifier.height(12.dp))
				Text(text = "Favorite notes", style = MaterialTheme.typography.titleMedium)
				Spacer(modifier = Modifier.height(8.dp))
				if (favoriteNotes.isEmpty()) {
					Text(text = "No favorites yet", style = MaterialTheme.typography.bodyMedium)
				} else {
					LazyRow (
						horizontalArrangement = Arrangement.spacedBy(12.dp)
					) {
						items(favoriteNotes) { n ->
							Surface(
								tonalElevation = 2.dp,
								shadowElevation = 1.dp,
								shape = RoundedCornerShape(16.dp),
								modifier = Modifier
//									.background(MaterialTheme.colorScheme.surfaceVariant)
							) {
								Column(modifier = Modifier
									.fillMaxWidth()
									.background(MaterialTheme.colorScheme.surfaceVariant)
									.clickable(onClick = {
										showNote = n
									})
									.padding(6.dp)
									.then(Modifier)) {
									Text(text = n.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
									Spacer(modifier = Modifier.height(4.dp))
									Text(text = n.content, style = MaterialTheme.typography.bodySmall, maxLines = 2)
									Spacer(modifier = Modifier.height(6.dp))
									Text(
										text = "Tap to preview",
										style = MaterialTheme.typography.labelSmall,
										color = MaterialTheme.colorScheme.onSurfaceVariant,
										modifier = Modifier
											.background(MaterialTheme.colorScheme.surfaceVariant)
											.padding(0.dp)
											.fillMaxWidth()
											.then(Modifier)
									)
								}
							}
						}
					}
				}
			}
		}
	}

	showNote?.let { note ->
		AlertDialog(
			onDismissRequest = { showNote = null },
			confirmButton = {
				Button(onClick = { showNote = null }) { Text("Close") }
			},
			title = { Text(note.title) },
			text = {
				Column {
					Text("by @${note.authorUsername}", style = MaterialTheme.typography.labelMedium)
					Spacer(modifier = Modifier.height(8.dp))
					Text(note.content, style = MaterialTheme.typography.bodyLarge)
				}
			}
		)
	}
}


