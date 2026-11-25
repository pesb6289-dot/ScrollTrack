package com.scrolltrack.app

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import kotlin.math.max

/** Son 7 günü gösteren, animasyonlu çubuk grafik. Son çubuk = bugün. */
class WeekBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var labels: List<String> = emptyList()
    private var values: List<Int> = emptyList()
    private var progress = 1f

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.text)
        textSize = sp(12f)
        isFakeBoldText = true
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = ContextCompat.getColor(context, R.color.text_muted)
        textSize = sp(12f)
    }
    private val rect = RectF()

    private fun sp(v: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, v, resources.displayMetrics)

    fun setData(labels: List<String>, values: List<Int>) {
        this.labels = labels
        this.values = values
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 900
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val n = values.size
        if (n == 0) return
        val d = resources.displayMetrics.density
        val topPad = 26 * d
        val labelH = 28 * d
        val chartH = height - topPad - labelH
        if (chartH <= 0) return
        val maxV = max(values.maxOrNull() ?: 0, 1)
        val slot = width.toFloat() / n
        val barW = slot * 0.52f
        val radius = 10 * d
        val bottom = topPad + chartH
        val accent = ContextCompat.getColor(context, R.color.accent)
        val accent2 = ContextCompat.getColor(context, R.color.accent2)
        val muted = ContextCompat.getColor(context, R.color.surface2)

        for (i in 0 until n) {
            val left = i * slot + (slot - barW) / 2f
            val h = max(chartH * values[i] / maxV * progress, 6 * d)
            val top = bottom - h
            rect.set(left, top, left + barW, bottom)
            if (i == n - 1) {
                barPaint.shader = LinearGradient(0f, top, 0f, bottom, accent, accent2, Shader.TileMode.CLAMP)
            } else {
                barPaint.shader = null
                barPaint.color = muted
            }
            canvas.drawRoundRect(rect, radius, radius, barPaint)
            canvas.drawText(
                (values[i] * progress).toInt().toString(),
                left + barW / 2f, top - 8 * d, valuePaint
            )
            labels.getOrNull(i)?.let {
                canvas.drawText(it, left + barW / 2f, height - 6 * d, labelPaint)
            }
        }
    }
}
