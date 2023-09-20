package me.y9san9.lifetime.statistics.compose

import android.graphics.Path
import android.graphics.RectF
import com.patrykandpatrick.vico.core.chart.dimensions.HorizontalDimensions
import com.patrykandpatrick.vico.core.chart.line.LineChart
import kotlin.math.max
import kotlin.math.min

class LifeTimePointConnector : LineChart.LineSpec.PointConnector {
    override fun connect(
        path: Path,
        prevX: Float,
        prevY: Float,
        x: Float,
        y: Float,
        horizontalDimensions: HorizontalDimensions,
        bounds: RectF
    ) {
        val maxY = max(prevY, y)
        val minY = min(prevY, y)

        val rect = RectF(
            prevX, minY,
            x, maxY
        )

        path.cubicTo(
            rect.centerX(),
            maxY,
            rect.centerX(),
            minY,
            x, y
        )
    }

}
