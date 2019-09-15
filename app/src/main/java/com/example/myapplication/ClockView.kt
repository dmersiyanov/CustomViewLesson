package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.util.TypedValue
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class ClockView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var clockHeight: Float = 0F
    private var clockWidth: Float = 0F
    private var padding = 0
    private var fontSize = 0
    private val numeralSpacing = 0
    private var handTruncation: Int = 0
    private var hourHandTruncation: Int = 0
    private var radius = 0
    private var paint = Paint()
    private var hourHandPaint = Paint().apply {
        strokeWidth = 10F
        color = ContextCompat.getColor(context, android.R.color.white)
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private var minuteHandPaint = Paint().apply {
        strokeWidth = 7F
        color = ContextCompat.getColor(context, android.R.color.white)
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private var isInit: Boolean = false
    private val numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val rect = Rect()

    init {
        clockHeight = height.toFloat()
        clockWidth = width.toFloat()
        padding = numeralSpacing + 50
        fontSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 13f,
            resources.displayMetrics
        ).toInt()
        val min = min(clockHeight, clockWidth).toInt()
        handTruncation = min / 20
        hourHandTruncation = min / 7
        isInit = true

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        clockWidth = w.toFloat()
        clockHeight = h.toFloat()
        radius = ((min(clockWidth, clockHeight) / 2 * 0.8).toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawColor(Color.BLACK)
        drawCircle(canvas)
        drawCenter(canvas)
        drawNumeral(canvas)
        drawHands(canvas)
        postInvalidateDelayed(500)
        invalidate()
    }

    private fun drawHand(canvas: Canvas?, loc: Double, handType: HandType) {

        fun getPaint(handType: HandType) = when (handType) {
            HandType.HOUR -> hourHandPaint
            HandType.MINUTE -> minuteHandPaint
            HandType.SECOND -> paint
        }

        val angle = Math.PI * loc / 30 - Math.PI / 2
        val handRadius = when (handType) {
            HandType.HOUR -> radius - handTruncation - hourHandTruncation
            else -> radius - handTruncation
        }
        canvas?.drawLine(
            clockWidth / 2, clockHeight / 2,
            (clockWidth / 2 + cos(angle) * handRadius).toFloat(),
            (clockHeight / 2 + sin(angle) * handRadius).toFloat(),
            getPaint(handType)
        )
    }

    private fun drawHands(canvas: Canvas?) {
        val c = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR_OF_DAY)
        hour = if (hour > 12) hour - 12 else hour
        drawHand(canvas, (hour + c.get(Calendar.MINUTE).toDouble() / 60) * 5f, HandType.HOUR)
        drawHand(canvas, c.get(Calendar.MINUTE).toDouble(), HandType.MINUTE)
        drawHand(canvas, c.get(Calendar.SECOND).toDouble(), HandType.SECOND)
    }

    private fun drawNumeral(canvas: Canvas?) {
        paint.textSize = fontSize.toFloat()

        for (number in numbers) {
            val tmp = number.toString()
            paint.getTextBounds(tmp, 0, tmp.length, rect)
            val angle = Math.PI / 6 * (number - 3)
            val x = (clockWidth / 2 + cos(angle) * radius - rect.width() / 2).toInt()
            val y = (clockHeight / 2 + sin(angle) * radius + rect.height() / 2).toInt()
            canvas?.drawText(tmp, x.toFloat(), y.toFloat(), paint)
        }
    }

    private fun drawCenter(canvas: Canvas?) {
        paint.style = Paint.Style.FILL
        canvas?.drawCircle(clockWidth / 2, clockHeight / 2, 12f, paint)
    }

    private fun drawCircle(canvas: Canvas?) {
        paint.reset()
        paint.color = ContextCompat.getColor(context, android.R.color.white)
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        canvas?.drawCircle(
            clockWidth / 2,
            clockHeight / 2,
            (radius + padding - 10).toFloat(),
            paint
        )
    }

    private enum class HandType {
        HOUR, MINUTE, SECOND
    }
}