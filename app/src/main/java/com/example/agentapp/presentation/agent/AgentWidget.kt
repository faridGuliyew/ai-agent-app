package com.example.agentapp.presentation.agent

import android.R.attr.onClick
import android.R.attr.translationX
import android.R.attr.translationY
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agentapp.R
import com.example.agentapp.utils.draggable
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun AgentWidget(
    modifier: Modifier = Modifier,
    onMessageSent: suspend (String) -> String
) {
    var isActive by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(if (!isActive) 1F else 2.5F)
    // Movement states
    val screenSize = LocalConfiguration.current.run { screenWidthDp.dp to screenHeightDp.dp }

    Box(modifier = modifier.fillMaxSize()) {

        // AI icon with drag
        Image(
            modifier = Modifier
                .size(72.dp)
                .graphicsLayer {
                    this.scaleY = animatedScale
                    this.scaleX = animatedScale
                    this.alpha = if (!isActive) 1F else 1F - animatedScale / 2.5F
                }
                .draggable(screenSize = screenSize)
                .clickable(onClick = { isActive = true }),
            imageVector = ImageVector.vectorResource(R.drawable.ic_ai),
            contentDescription = null
        )

        // AI dialog
        AnimatedVisibility(
            modifier = Modifier.draggable(
                screenSize = screenSize,
                initialY = 500F
            ),
            visible = isActive,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            AIDialog(
                onMessageSent = onMessageSent,
                onDismiss = { isActive = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AgentWidgetPrev() {
    AgentWidget(
        onMessageSent = { "DOne" }
    )
}