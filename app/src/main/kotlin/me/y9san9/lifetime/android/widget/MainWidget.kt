package me.y9san9.lifetime.android.widget

import android.content.Context
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.material.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import app.meetacy.di.android.di
import kotlinx.coroutines.*
import me.y9san9.lifetime.R
import me.y9san9.lifetime.android.extensions.toggleCountdownFromWidget
import me.y9san9.lifetime.looper.looper

class MainWidget : GlanceAppWidget() {
    private val looper = di.looper

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val colors = ColorProviders(
                light = lightColors(),
                dark = darkColors()
            )

            val countdown by looper.countdownState.collectAsState()

            GlanceTheme(colors) {
                Content(
                    countdown = countdown,
                    onClick = { looper.toggleCountdownFromWidget(context) }
                )
            }
        }
    }

    @Composable
    fun Content(
        countdown: Boolean,
        onClick: () -> Unit
    ) {
        val color = ColorProvider(if (countdown) Color.Red else Color.Gray)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(GlanceTheme.colors.background)
                .clickable(onClick),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.Horizontal.CenterHorizontally) {
                Image(
                    provider = ImageProvider(R.drawable.stopwatch_solid),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color),
                    modifier = GlanceModifier.size(35.dp)
                )
                Spacer(modifier = GlanceModifier.height(5.dp))
                Text(
                    text = LocalContext.current.getString(
                        if (countdown) {
                            R.string.widget_stop
                        } else {
                            R.string.widget_waste
                        }
                    ),
                    style = TextStyle(color)
                )
            }
        }
    }

    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        private val scope = GlobalScope + CoroutineName("Main LifeTime Widgets Scope")

        fun update(context: Context) {
            scope.launch {
                MainWidget().updateAll(context)
            }
        }
    }
}
