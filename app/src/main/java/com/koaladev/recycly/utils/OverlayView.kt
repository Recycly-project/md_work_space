package com.koaladev.recycly.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View


class OverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = 0xFFFF0000.toInt() // Warna outline merah
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val boxes = mutableListOf<Rect>()

    fun clear() {
        boxes.clear()
        invalidate()
    }

    fun drawBoundingBox(box: Rect) {
        boxes.add(box)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        boxes.forEach { box ->
            canvas.drawRect(box, paint)
        }
    }
}