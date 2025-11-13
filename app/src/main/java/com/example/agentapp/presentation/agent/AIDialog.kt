package com.example.agentapp.presentation.agent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

data class Message(
    val text: String,
    val isUser: Boolean
)

@Composable
@Preview
fun AIDialog(
    modifier: Modifier = Modifier,
    onMessageSent: suspend (String) -> String = {
        delay(5.seconds)
        "Done!"
    },
    onDismiss: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(false) }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isEmpty()) return@LaunchedEffect

        listState.scrollToItem(messages.lastIndex)
    }

    Box (
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(1F)
                .fillMaxHeight(0.3F)
                .background(Color(0xFFF3F3F3), RoundedCornerShape(16.dp))
                .border(width = 1.dp, color = Color(0xFFAAAAAA), RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                verticalArrangement = Arrangement.Bottom
            ) {
                items(messages) { msg ->
                    MessageBubble(msg)
                }

                item {
                    AnimatedVisibility(visible = isLoading) {
                        ThinkingAnimation()
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White, shape = CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Type something...") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF0078FF)
                    ),
                    maxLines = 3
                )

                val scope = rememberCoroutineScope()
                FilledIconButton(
                    onClick = {
                        if (input.isBlank()) return@FilledIconButton
                        val command = input
                        input = ""

                        messages = messages + Message(command, true)
                        scope.launch {
                            isLoading = true
                            messages += Message(onMessageSent(command), false)
                            isLoading = false
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color(0xFF0078FF),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }

        FilledIconButton(
            modifier = Modifier
                .padding(16.dp)
                .size(36.dp),
            onClick = onDismiss,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color(0xFFFF3333),
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close"
            )
        }
    }
}

@Composable
fun MessageBubble(msg: Message) {
    val bgColor = if (msg.isUser) Color(0xFF0078FF) else Color.White
    val textColor = if (msg.isUser) Color.White else Color.Black
    val alignment = if (msg.isUser) Alignment.BottomEnd else Alignment.BottomStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        contentAlignment = alignment
    ) {
        Surface(
            color = bgColor,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 2.dp
        ) {
            Text(
                text = msg.text,
                color = textColor,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
fun ThinkingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            tween(500),
            RepeatMode.Reverse
        ), label = "dot1"
    )
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            tween(500, delayMillis = 150),
            RepeatMode.Reverse
        ), label = "dot2"
    )
    val scale3 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            tween(500, delayMillis = 300),
            RepeatMode.Reverse
        ), label = "dot3"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        listOf(scale1, scale2, scale3).forEach {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .scale(it)
                    .background(Color.Gray, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
    }
}
