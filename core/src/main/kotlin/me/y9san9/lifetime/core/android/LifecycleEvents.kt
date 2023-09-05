package me.y9san9.lifetime.core.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun OnResume(block: () -> Unit) {
    LifecycleEvent(Lifecycle.Event.ON_RESUME) { block() }
}

@Composable
fun OnPause(block: () -> Unit) {
    LifecycleEvent(Lifecycle.Event.ON_PAUSE) { block() }
}

@Composable
fun LifecycleEvent(
    event: Lifecycle.Event,
    block: () -> Unit
) {
    LifecycleEvent { _, current ->
        if (current == event) block()
    }
}

@Composable
fun LifecycleEvent(
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver(onEvent)
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
