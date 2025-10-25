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

    // --- CHANGE 1: Add a cache for compiled texture shaders for performance ---
    private val textureCache = mutableMapOf<Int, BitmapShader>()

    // --- CHANGE 2: Add texture info to the Stroke data class ---
    data class Stroke(
        val path: Path,
        val color: Int,
        val strokeWidth: Float,
        val alpha: Int,
        val isEraser: Boolean,
        val textureResId: Int? = null // To remember if a stroke was textured
    )

    private val strokeList = ArrayList<Stroke>()
    private val redoList = ArrayList<Stroke>()
    private var currentPath: Path? = null
    private var currentBrush: BrushType = BrushLibrary.brushes.first()
    private var isEraser = false

    var brushSize: Float = 20f
        set(value) {
            field = value.coerceIn(1f, 200f)
            updatePaint() // updatePaint handles applying the size
            invalidate()
        }

    var brushOpacity: Int = 255
        set(value) {
            field = value.coerceIn(0, 255)
            updatePaint() // updatePaint handles applying the opacity
            invalidate()
        }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        updatePaint()
    }

    // --- CHANGE 3: Add a helper function to create and cache shaders ---
    private fun getTexture(textureResId: Int): BitmapShader {
        if (textureCache.containsKey(textureResId)) {
            return textureCache[textureResId]!!
        }
        val textureBitmap = BitmapFactory.decodeResource(resources, textureResId)
        val shader = BitmapShader(textureBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        textureCache[textureResId] = shader
        return shader
    }

    // --- CHANGE 4: Overhaul the updatePaint() method to support textures ---
    private fun updatePaint() {
        paintBrush.apply {
            // Reset shader and color filter from previous state
            shader = null
            colorFilter = null

            strokeWidth = brushSize
            xfermode = if (isEraser) {
                // Eraser mode
                PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            } else if (currentBrush.textureRes != null) {
                // Textured brush mode
                shader = getTexture(currentBrush.textureRes!!)
                // Use a color filter to tint the texture with the brush color
                colorFilter = PorterDuffColorFilter(currentBrush.color, PorterDuff.Mode.SRC_IN)
                alpha = brushOpacity // Still respect the overall opacity
                null // Clear xfermode if it was set
            } else {
                // Solid color brush mode
                color = currentBrush.color
                alpha = brushOpacity
                null // Clear xfermode
            }
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
                    // --- CHANGE 5: Save the texture info when a stroke is finished ---
                    val stroke = Stroke(
                        Path(path),
                        currentBrush.color,
                        brushSize,
                        brushOpacity,
                        isEraser,
                        if (isEraser) null else currentBrush.textureRes
                    )
                    strokeList.add(stroke)
                }
                currentPath = null
                invalidate()
            }
        }
        return true
    }

    // --- CHANGE 6: Overhaul onDraw() to render each stroke correctly ---
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Redraw all the saved strokes from the list
        for (stroke in strokeList) {
            paintBrush.apply {
                // Reset everything first for this specific stroke
                shader = null
                colorFilter = null

                // A. Configure for an eraser stroke
                if (stroke.isEraser) {
                    xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                }
                // B. Configure for a textured stroke
                else if (stroke.textureResId != null) {
                    xfermode = null
                    shader = getTexture(stroke.textureResId)
                    colorFilter = PorterDuffColorFilter(stroke.color, PorterDuff.Mode.SRC_IN)
                    alpha = stroke.alpha
                }
                // C. Configure for a solid stroke
                else {
                    xfermode = null
                    color = stroke.color
                    alpha = stroke.alpha
                }

                // Apply common properties for all stroke types
                strokeWidth = stroke.strokeWidth
            }
            // Draw the path with the fully configured paint object
            canvas.drawPath(stroke.path, paintBrush)
        }

        // 2. Draw the live, current path the user is drawing now
        currentPath?.let {
            // updatePaint() correctly configures the brush for the current settings
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