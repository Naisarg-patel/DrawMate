package com.example.animationapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.animationapp.models.BrushType
import kotlin.math.hypot

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var onTouchStartListener: (() -> Unit)? = null

    private val paintBrush = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    data class Segment(
        val fromX: Float,
        val fromY: Float,
        val toX: Float,
        val toY: Float,
        val width: Float,
        val alpha: Int
    )
    data class Stroke(
        val segments: List<Segment>,
        val color: Int,
        val isEraser: Boolean
    )

    private val currentSegments = mutableListOf<Segment>()
    private val strokeList = ArrayList<Stroke>()
    private val redoList = ArrayList<Stroke>()
    private var currentBrush: BrushType = BrushLibrary.brushes.first()
    private var isEraser = false
    private var lastX = 0f
    private var lastY = 0f
    private var lastTime = 0L
    private var smoothedWidth = currentBrush.strokeWidth
    private var smoothedAlpha: Int = 25

    var maxWidth: Float = 50f
        set(value) {
            field = value.coerceAtLeast(0f)  // Custom logic to ensure non-negative value
        }
    var minWidth: Float = 1f
        set(value) {
            field = value.coerceAtLeast(0f)  // Custom logic
        }
    var maxAlpha: Int = 255
        set(value) {
            field = value.coerceIn(0, 255)  // Custom logic to keep within 0-255
        }
    var minAlpha: Int = 50
        set(value) {
            field = value.coerceIn(0, 255)  // Custom logic
        }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchStartListener?.invoke()
                currentSegments.clear()
                redoList.clear()
                lastX = x
                lastY = y
                lastTime = System.currentTimeMillis()
                smoothedWidth = currentBrush.strokeWidth
                smoothedAlpha = maxAlpha
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = x - lastX
                val dy = y - lastY
                val distance = hypot(dx, dy)
                val currentTime = System.currentTimeMillis()
                val dt = (currentTime - lastTime).coerceAtLeast(1)

                // Speed = distance / time
                val speed = distance / dt.toFloat()
                val speedFactor = speed.coerceIn(0f, 1f)  // 0: slow, 1: fast

                // Simulate pressure: slower = thicker, faster = thinner
                val targetWidth = minWidth + (maxWidth - minWidth) * (1 - speedFactor)
                smoothedWidth = (smoothedWidth * 0.7f + targetWidth * 0.3f)

                val targetAlphaFloat = minAlpha + (maxAlpha - minAlpha) * (1 - speedFactor)
                smoothedAlpha = (smoothedAlpha * 0.7f + targetAlphaFloat * 0.3f).toInt().coerceIn(0, 255)

                currentSegments.add(Segment(lastX, lastY, x, y, smoothedWidth, smoothedAlpha))

                lastX = x
                lastY = y
                lastTime = currentTime
            }

            MotionEvent.ACTION_UP -> {
                if (currentSegments.isNotEmpty()) {
                    val stroke = Stroke(currentSegments.toList(), currentBrush.color, isEraser)
                    strokeList.add(stroke)
                    currentSegments.clear()
                }
            }
        }

        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (stroke in strokeList) {
            for (segment in stroke.segments) {
                paintBrush.color = stroke.color
                paintBrush.alpha = segment.alpha
                paintBrush.strokeWidth = segment.width
                paintBrush.xfermode = if (stroke.isEraser) PorterDuffXfermode(PorterDuff.Mode.CLEAR) else null

                // Draw the segment with smoothing: Divide into sub-segments
                drawSmoothedLine(canvas, segment.fromX, segment.fromY, segment.toX, segment.toY, segment.width, segment.alpha)
            }
        }
        for (segment in currentSegments) {
            paintBrush.color = currentBrush.color
            paintBrush.alpha = segment.alpha
            paintBrush.strokeWidth = segment.width
            paintBrush.xfermode = if (isEraser) PorterDuffXfermode(PorterDuff.Mode.CLEAR) else null
            drawSmoothedLine(canvas, segment.fromX, segment.fromY, segment.toX, segment.toY, segment.width, segment.alpha)
        }
    }

    private fun drawSmoothedLine(canvas: Canvas, startX: Float, startY: Float, endX: Float, endY: Float, width: Float, alpha: Int) {
        val segments = 30  // Number of sub-segments for smoothing; increase for more smoothness
        val dx = endX - startX
        val dy = endY - startY
        val steps = segments.toFloat()

        for (i in 1..segments) {
            val t = i / steps
            val x1 = startX + (i - 1) * (dx / steps)
            val y1 = startY + (i - 1) * (dy / steps)
            val x2 = startX + i * (dx / steps)
            val y2 = startY + i * (dy / steps)

            paintBrush.strokeWidth = width  // Keep width consistent for this segment
            paintBrush.alpha = alpha
            canvas.drawLine(x1, y1, x2, y2, paintBrush)
        }
    }

    fun undo() {
        if (strokeList.isNotEmpty()) {
            redoList.add(strokeList.removeAt(strokeList.size - 1))
            invalidate()
        }
    }

    fun redo() {
        if (redoList.isNotEmpty()) {
            strokeList.add(redoList.removeAt(redoList.size - 1))
            invalidate()
        }
    }

    fun setBrush(brush: BrushType) {
        currentBrush = brush
        isEraser = false
        invalidate()
    }

    fun setEraser(flag: Boolean) {
        isEraser = flag
        invalidate()
    }

}
