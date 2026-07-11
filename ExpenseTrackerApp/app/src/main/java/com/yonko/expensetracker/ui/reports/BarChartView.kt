package com.yonko.expensetracker.ui.reports

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.yonko.expensetracker.util.Formatting

/** Simple horizontal bar chart: one bar per data point, no external chart library needed. */
class BarChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    data class Bar(val label: String, val value: Double, val color: Int)

    private var bars: List<Bar> = emptyList()
    private val barHeightPx = dp(22f)
    private val gapPx = dp(14f)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8A8FA3"); textSize = dp(11f)
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EEF0F6"); textSize = dp(11f); isFakeBoldText = true
        textAlign = Paint.Align.RIGHT
    }
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1F2330") }
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun setData(data: List<Bar>) {
        bars = data
        requestLayout()
        invalidate()
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val rowHeight = barHeightPx + gapPx + dp(16f)
        val height = (bars.size * rowHeight + dp(8f)).toInt()
        setMeasuredDimension(width, height.coerceAtLeast(dp(40f).toInt()))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bars.isEmpty()) return
        val maxVal = (bars.maxOfOrNull { it.value } ?: 1.0).coerceAtLeast(1.0)
        val rowHeight = barHeightPx + gapPx + dp(16f)
        var y = dp(16f)

        bars.forEach { bar ->
            canvas.drawText(bar.label, 0f, y - dp(4f), labelPaint)
            canvas.drawRect(0f, y, width.toFloat(), y + barHeightPx, trackPaint)
            val w = (bar.value / maxVal * width).toFloat().coerceAtLeast(if (bar.value > 0) dp(3f) else 0f)
            barPaint.color = bar.color
            canvas.drawRect(0f, y, w, y + barHeightPx, barPaint)
            canvas.drawText(Formatting.money(bar.value), width.toFloat(), y + barHeightPx - dp(6f), valuePaint)
            y += rowHeight
        }
    }
}
