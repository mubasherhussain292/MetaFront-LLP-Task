package com.side.effects.metafronttask.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.side.effects.metafronttask.R
import java.lang.Float.min
import java.lang.Math.max


class DayPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 1
    private val max = 30

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        strokeWidth = 8f
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.fiolet)
        strokeWidth = 8f
    }
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.fiolet)
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    private val regularDot: Drawable? = ContextCompat.getDrawable(context, R.drawable.between_dots)
    private val fifthDot: Drawable? = ContextCompat.getDrawable(context, R.drawable.fifth_dot)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val trackStartX = paddingLeft.toFloat()
        val trackEndX = (width - paddingRight).toFloat()
        val trackY = height / 3f
        val dotY = trackY + 30f
        val labelY = dotY + 40f

        val intervalCount = max + 1
        val dotSpacing = (trackEndX - trackStartX) / (intervalCount - 1)

        canvas.drawLine(trackStartX, trackY, trackEndX, trackY, trackPaint)

        for (i in 0 until intervalCount) {
            val dotX = trackStartX + i * dotSpacing
            val dotDrawable = if ((i + 1) % 5 == 0) fifthDot else regularDot // Every fifth dot uses a different drawable

            dotDrawable?.let {
                val dotSize = 16
                it.setBounds(
                    (dotX - dotSize / 2).toInt(),
                    (dotY - dotSize / 2).toInt(),
                    (dotX + dotSize / 2).toInt(),
                    (dotY + dotSize / 2).toInt()
                )
                it.draw(canvas)
            }

            if ((i + 1) % 5 == 0) {
                canvas.drawText((i + 1).toString(), dotX, labelY, labelPaint)
            }
        }

        val progressX = trackStartX + (trackEndX - trackStartX) * (progress.toFloat() / max)
        canvas.drawLine(trackStartX, trackY, progressX, trackY, progressPaint)
        canvas.drawCircle(progressX, trackY, 24f, thumbPaint)

        canvas.drawText(progress.toString(), progressX, trackY + 12f, textPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Calculate the width of the track (excluding padding)
                val trackWidth = width - paddingLeft - paddingRight

                // Get the X position of the touch event and clamp it within the track range
                val touchX = min(trackWidth.toFloat(), max(0f, event.x - paddingLeft))

                // Update the progress based on the touch position
                progress = (((touchX / trackWidth) * max).toInt())

                // Request a redraw with the updated progress
                invalidate()

                // Return true to indicate we handled the touch event
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}