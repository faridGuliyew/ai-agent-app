package com.example.agentapp.utils

import android.R.attr.screenSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp

fun Modifier.draggable(
    screenSize: Pair<Dp, Dp>,
    initialY: Float = 0F
) = composed {
    var translationX by remember { mutableFloatStateOf(0F) }
    val animatedTranslationX by animateFloatAsState(translationX)
    var translationY by remember { mutableFloatStateOf(initialY) }
    val animatedTranslationY by animateFloatAsState(translationY)

    graphicsLayer {
        this.translationX = animatedTranslationX
        this.translationY = animatedTranslationY
    }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragEnd = {
                    // Snap to closest point
                    val screenWidth = screenSize.first.toPx()
                    if (translationX / screenWidth >= 0.5F) {
                        translationX = screenWidth - size.width
                    } else {
                        translationX = 0F
                    }
                }
            ) { _, dragAmount ->
                translationX = (translationX + dragAmount.x).coerceIn(
                    0F,
                    (screenSize.first.toPx() - size.width).coerceAtLeast(0F)
                )
                translationY = (translationY + dragAmount.y).coerceIn(
                    0F,
                    screenSize.second.toPx() - size.height
                )
            }
        }
}