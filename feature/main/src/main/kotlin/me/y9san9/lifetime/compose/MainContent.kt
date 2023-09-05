package me.y9san9.lifetime.compose

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import me.y9san9.lifetime.core.type.SecondStashedTimeView
import me.y9san9.lifetime.core.type.StashedTimeView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainContent(
    time: StashedTimeView,
    secondAddedTime: State<SecondStashedTimeView>,
    countdown: Boolean,
    onTimerClick: () -> Unit,
    onNavigateStatistics: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val interactionSource = remember { MutableInteractionSource() }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = rememberRipple(),
                    onLongClick = { onNavigateStatistics() },
                    onClick = { onTimerClick() }
                )
        ) {
            var textSize by remember { mutableStateOf(1f) }

            if (countdown) {
                LaunchedEffect(time.string) {
                    animate(
                        initialValue = textSize,
                        targetValue = 1.15f
                    ) { value, _ ->
                        textSize = value
                    }
                    animate(
                        initialValue = textSize,
                        targetValue = 1f
                    ) { value, _ ->
                        textSize = value
                    }
                }
            }

            Text(
                text = time.string,
                fontSize = (30 * textSize).sp,
                color = if (countdown) Color.Red else MaterialTheme.colors.onBackground
            )

            CircularIndicator(secondAddedTime, countdown)
        }
    }
}

@Composable
private fun CircularIndicator(
    secondAddedTime: State<SecondStashedTimeView>,
    countdown: Boolean
) {
    var animatedProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        snapshotFlow { secondAddedTime.value }.collectLatest { time ->
            animate(
                initialValue = time.progress(),
                targetValue = 1f,
                animationSpec = tween(time.updateDelay().toInt())
            ) { value, _ ->
                animatedProgress = value
            }
        }
    }

    CircularProgressIndicator(
        progress = animatedProgress,
        modifier = Modifier.size(200.dp),
        color = if (countdown) Color.Red else MaterialTheme.colors.primary
    )
}
