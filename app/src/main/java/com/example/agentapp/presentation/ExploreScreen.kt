package com.example.agentapp.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

@Composable
fun ExploreScreen(viewModel: AppViewModel) {
	var query by remember { mutableStateOf("") }
	val all = viewModel.discoverNotes
	val filtered = if (query.isBlank()) all else all.filter {
		it.title.contains(query, ignoreCase = true) ||
			it.content.contains(query, ignoreCase = true) ||
			it.authorUsername.contains(query, ignoreCase = true)
	}

	Surface(modifier = Modifier.fillMaxSize()) {
		Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
			OutlinedTextField(
				value = query,
				onValueChange = { query = it },
				modifier = Modifier.fillMaxWidth(),
				label = { Text("Search notes") }
			)
			Spacer(modifier = Modifier.height(12.dp))
			LazyColumn {
				itemsIndexed(filtered) { index, note ->
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
							.padding(16.dp)) {
							Text(text = note.title, style = MaterialTheme.typography.titleMedium)
							Text(
								text = "by @${note.authorUsername} • ${note.lastUpdated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}",
								style = MaterialTheme.typography.labelMedium
							)
							Spacer(modifier = Modifier.height(6.dp))
							Text(
								text = note.content,
								style = MaterialTheme.typography.bodyMedium,
								maxLines = 3,
								overflow = TextOverflow.Ellipsis
							)
							Spacer(modifier = Modifier.height(6.dp))
							val scale by animateFloatAsState(
								targetValue = if (note.isFavorite) 1.2f else 1f,
								animationSpec = tween(200, easing = FastOutSlowInEasing),
								label = "favScale"
							)
							IconButton(onClick = { viewModel.toggleFavorite(note, index, true) }, modifier = Modifier.scale(scale)) {
								Icon(
									Icons.Default.Favorite,
									contentDescription = "Favorite",
									tint = if (note.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
								)
							}
						}
					}
					Spacer(modifier = Modifier.height(12.dp))
				}
			}
		}
	}
}


