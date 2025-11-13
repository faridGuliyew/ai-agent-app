package com.example.agentapp.presentation.screens.profile

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agentapp.R
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ProfileScreen() {
	val viewModel = koinViewModel<ProfileViewModel>()
	val state by viewModel.state.collectAsStateWithLifecycle()

	Scaffold(
		topBar = {
			Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
				Text(
					text = "Profile",
					style = MaterialTheme.typography.titleLarge,
					modifier = Modifier.weight(1f)
				)
				IconButton(onClick = viewModel::toggleEditState) {
					if (state.editState != null) {
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
				val editState = state.editState
				if (editState != null) {
					OutlinedTextField(
						value = editState.username,
						onValueChange = { viewModel.updateEditState(editState.copy(username = it)) },
						label = { Text("Username") }
					)
					Spacer(modifier = Modifier.height(8.dp))
					OutlinedTextField(
						value = editState.bio,
						onValueChange = { viewModel.updateEditState(editState.copy(bio = it)) },
						label = { Text("Bio") }
					)
				} else {
					Text(text = "@${state.username}", style = MaterialTheme.typography.titleLarge)
					Text(text = state.bio, style = MaterialTheme.typography.bodyMedium)
				}
				Spacer(modifier = Modifier.height(12.dp))
				Text(text = "Favorites count: ${state.favorites.size}", style = MaterialTheme.typography.bodyLarge)
				Spacer(modifier = Modifier.height(12.dp))
				Text(text = "Favorite notes", style = MaterialTheme.typography.titleMedium)
				Spacer(modifier = Modifier.height(8.dp))
				if (state.favorites.isEmpty()) {
					Text(text = "No favorites yet", style = MaterialTheme.typography.bodyMedium)
				} else {
					LazyRow (
						horizontalArrangement = Arrangement.spacedBy(12.dp)
					) {
						items(state.favorites) { n ->
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
										viewModel.viewNote(n)
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


