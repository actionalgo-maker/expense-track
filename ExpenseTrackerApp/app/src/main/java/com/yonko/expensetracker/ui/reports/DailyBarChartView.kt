package com.yonko.expensetracker.ui.reports

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/** Vertical column chart for daily spend within a month. */
class DailyBarChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var values: List<Double> = emptyList()
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#5B4FE9") }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8A8FA3"); textSize = dp(9f); textAlign = Paint.Align.CENTER
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density

    fun setData(data: List<Double>) {
        values = data
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, dp(150f).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (values.isEmpty()) return
        val max = (values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
        val chartHeight = height - dp(16f)
        val barWidth = width.toFloat() / values.size
        val labelEvery = (values.size / 8).coerceAtLeast(1)

        values.forEachIndexed { index, v ->
            val barHeight = (v / max * chartHeight).toFloat()
            val left = index * barWidth + dp(1f)
            val right = (index + 1) * barWidth - dp(1f)
            val top = chartHeight - barHeight
            canvas.drawRect(left, top, right.coerceAtLeast(left + dp(1f)), chartHeight, barPaint)
            if (index % labelEvery == 0) {
                canvas.drawText((index + 1).toString(), (left + right) / 2, height.toFloat() - dp(2f), labelPaint)
            }
        }
    }
}
