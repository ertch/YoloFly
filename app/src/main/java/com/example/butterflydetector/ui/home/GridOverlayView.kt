package com.example.butterflydetector.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GridOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var rows: Int = 3
    var cols: Int = 3

    private val paint = Paint().apply {
        color = 0x80FFFFFF.toInt() // semi-transparent white
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellWidth = width / cols.toFloat()
        val cellHeight = height / rows.toFloat()

        // Draw vertical lines
        for (i in 1 until cols) {
            val x = i * cellWidth
            canvas.drawLine(x, 0f, x, height.toFloat(), paint)
        }

        // Draw horizontal lines
        for (i in 1 until rows) {
            val y = i * cellHeight
            canvas.drawLine(0f, y, width.toFloat(), y, paint)
        }
    }
}
