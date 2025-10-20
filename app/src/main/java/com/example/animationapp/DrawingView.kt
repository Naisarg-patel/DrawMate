package com.example.animationapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.animationapp.models.BrushType

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var onTouchStartListener: (() -> Unit)? = null

    private var lastX = 0f
    private var lastY = 0f

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                onTouchStartListener?.invoke()
                return super.onSingleTapUp(e)
            }
        })

    private val paintBrush = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    data class Stroke(
        val path: Path,
        val color: Int,
        val strokeWidth: Float,
        val alpha: Int,
        val isEraser: Boolean
    )

    private val strokeList = ArrayList<Stroke>()
    private val redoList = ArrayList<Stroke>()
    private var currentPath: Path? = null
    private var currentBrush: BrushType = BrushLibrary.brushes.first()
    private var isEraser = false

    // Manual control
    var brushSize: Float = 20f
        set(value) {
            field = value.coerceIn(1f, 200f)
            paintBrush.strokeWidth = field
            invalidate()
        }

    var brushOpacity: Int = 255
        set(value) {
            field = value.coerceIn(0, 255)
            paintBrush.alpha = field
            invalidate()
        }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        updatePaint()
    }

    private fun updatePaint() {
        // Always keep paint in sync with the latest brush settings
        paintBrush.apply {
            color = currentBrush.color
            strokeWidth = brushSize
            alpha = brushOpacity
            xfermode = if (isEraser) PorterDuffXfermode(PorterDuff.Mode.CLEAR) else null
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchStartListener?.invoke()
                currentPath = Path().apply { moveTo(x, y) }
                lastX = x
                lastY = y
                redoList.clear()
                updatePaint()
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                val midX = (lastX + x) / 2
                val midY = (lastY + y) / 2
                currentPath?.quadTo(lastX, lastY, midX, midY)
                lastX = x
                lastY = y
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                currentPath?.let { path ->
                    val stroke = Stroke(
                        Path(path),
                        paintBrush.color,
                        paintBrush.strokeWidth,
                        paintBrush.alpha,
                        isEraser
                    )
                    strokeList.add(stroke)
                }
                currentPath = null
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (stroke in strokeList) {
            paintBrush.color = stroke.color
            paintBrush.strokeWidth = stroke.strokeWidth
            paintBrush.alpha = stroke.alpha
            paintBrush.xfermode = if (stroke.isEraser)
                PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            else null
            canvas.drawPath(stroke.path, paintBrush)
        }

        currentPath?.let {
            updatePaint()
            canvas.drawPath(it, paintBrush)
        }
    }

    fun undo() {
        if (strokeList.isNotEmpty()) {
            redoList.add(strokeList.removeLast())
            invalidate()
        }
    }

    fun redo() {
        if (redoList.isNotEmpty()) {
            strokeList.add(redoList.removeLast())
            invalidate()
        }
    }

    fun setBrush(brush: BrushType) {
        currentBrush = brush
        isEraser = false
        brushSize = brush.strokeWidth
        brushOpacity = Color.alpha(brush.color)
        updatePaint()
        invalidate()
    }

    fun setEraser(flag: Boolean) {
        isEraser = flag
        updatePaint()
        invalidate()
    }

    fun setBrushColor(color: Int) {
        currentBrush = currentBrush.copy(color = color)
        updatePaint()
        invalidate()
    }
}
