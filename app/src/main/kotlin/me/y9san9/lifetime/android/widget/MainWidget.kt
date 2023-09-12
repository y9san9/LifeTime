package me.y9san9.lifetime.android.widget

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import androidx.annotation.FloatRange
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
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

object MainWidget : GlanceAppWidget() {
    private val looper = di.looper
    @OptIn(DelicateCoroutinesApi::class)
    private val scope = GlobalScope + CoroutineName("Main LifeTime Widgets Scope")

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
                .appWidgetBackground()
                .fillMaxSize()
                .cornerRadiusCompat(10.dp, GlanceTheme.colors.background)
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

    fun update(context: Context) {
        scope.launch {
            MainWidget.updateAll(context)
        }
    }
}

/**
 * Adds rounded corners for the current view.
 *
 * On S+ it uses [GlanceModifier.cornerRadius]
 * on <S it creates [ShapeDrawable] and sets background
 *
 * @param cornerRadius [Int] radius set to all corners of the view.
 * @param color [Int] value of a color that will be set as background
 * @param backgroundAlpha [Float] value of an alpha that will be set to background color - defaults to 1f
 */
@Composable
private fun GlanceModifier.cornerRadiusCompat(
    cornerRadius: Dp,
    color: ColorProvider,
    @FloatRange(from = 0.0, to = 1.0) backgroundAlpha: Float = 1f,
): GlanceModifier {
    val actualColor = color.getColor(LocalContext.current)

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        background(actualColor.copy(alpha = backgroundAlpha))
            .cornerRadius(cornerRadius)
    } else {
        val cornerRadiusFloat = cornerRadius.value * LocalContext.current.resources.displayMetrics.density
        val radii = FloatArray(8) { cornerRadiusFloat }
        val shape = ShapeDrawable(RoundRectShape(radii, null, null))
        shape.paint.color = ColorUtils.setAlphaComponent(actualColor.toArgb(), (255 * backgroundAlpha).toInt())
        val bitmap = shape.toBitmap(width = 150, height = 75)
        this.background(BitmapImageProvider(bitmap))
    }
}
