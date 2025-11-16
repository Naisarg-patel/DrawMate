package com.example.DrawMate

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.DrawMate.models.BrushType

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

    private val textureCache = mutableMapOf<Int, BitmapShader>()

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
    enum class Tooltype { BRUSH,ERASER,SHAPE }
    enum class shapetype { CIRCLE, RECTANGLE, LINE }
    private var currentshape: Path? = null
    private var currentshapetype = shapetype.RECTANGLE
    private var currentTool = Tooltype.BRUSH
    private var shapeStartX = 0f
    private var shapeStartY = 0f
    private var shapePath: Path? = null
    private var bufferBitmap: Bitmap? = null
    private var bufferCanvas: Canvas? = null
    private var tempShapeBitmap: Bitmap? = null
    private var tempShapeCanvas: Canvas? = null


    var brushSize: Float = 20f
        set(value) {
            field = value.coerceIn(1f, 200f)
            updatePaint() // updatePaint handles applying the size
            invalidate()
        }

    var brushOpacity: Int
        get() = currentBrush.opacity
        set(value) {
            currentBrush = currentBrush.copy(opacity = value)
            updatePaint() // updatePaint handles applying the opacity
            invalidate()
        }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        updatePaint()
    }

    private fun getTexture(textureResId: Int): BitmapShader {
        if (textureCache.containsKey(textureResId)) {
            return textureCache[textureResId]!!
        }
        val textureBitmap = BitmapFactory.decodeResource(resources, textureResId)
        val shader = BitmapShader(textureBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        textureCache[textureResId] = shader
        return shader
    }

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
                alpha = currentBrush.opacity
                null // Clear xfermode
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        val x = event.x
        val y = event.y

        when (currentTool) {
            Tooltype.BRUSH -> handleBrushTouch(event, x, y)
            Tooltype.SHAPE -> handleShapeTouch(event, x, y)
            Tooltype.ERASER -> handleBrushTouch(event, x, y)
        }

        return true
    }

    private fun handleBrushTouch(event: MotionEvent, x: Float, y: Float): Boolean {
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            bufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bufferCanvas = Canvas(bufferBitmap!!)
            tempShapeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            tempShapeCanvas = Canvas(tempShapeBitmap!!)
        }
    }

    private fun handleShapeTouch(event: MotionEvent, x: Float, y: Float) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                shapeStartX = x
                shapeStartY = y
                currentshape = Path()
            }
            MotionEvent.ACTION_MOVE -> {
                tempShapeBitmap?.eraseColor(Color.TRANSPARENT)
                currentshape?.reset()
                when(currentshapetype) {
                    shapetype.CIRCLE -> {
                        val radius = kotlin.math.hypot(x - shapeStartX, y - shapeStartY)
                        currentshape?.addCircle(shapeStartX, shapeStartY, radius, Path.Direction.CW)
                    }
                    shapetype.RECTANGLE -> {
                        currentshape?.addRect(shapeStartX, shapeStartY, x, y, Path.Direction.CW)
                    }
                    shapetype.LINE -> {
                        currentshape?.moveTo(shapeStartX, shapeStartY)
                        currentshape?.lineTo(x, y)
                    }
                }
                tempShapeCanvas?.drawPath(currentshape!!, paintBrush)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                currentshape?.let { path ->
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
                tempShapeBitmap?.eraseColor(Color.TRANSPARENT)
                currentshape = null
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (stroke in strokeList) {
            paintBrush.apply {
                shader = null
                colorFilter = null

                if (stroke.isEraser) {
                    xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                }
                else if (stroke.textureResId != null) {
                    xfermode = null
                    shader = getTexture(stroke.textureResId)
                    colorFilter = PorterDuffColorFilter(stroke.color, PorterDuff.Mode.SRC_IN)
                    alpha = stroke.alpha
                }
                else {
                    xfermode = null
                    color = stroke.color
                    alpha = stroke.alpha
                }

                strokeWidth = stroke.strokeWidth
            }
            canvas.drawPath(stroke.path, paintBrush)
        }

        currentPath?.let {
            updatePaint()
            canvas.drawPath(it, paintBrush)
        }

        tempShapeBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        shapePath?.let {
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

    fun setTool(tool: Tooltype) {
        currentTool = tool
        isEraser = (tool == Tooltype.ERASER)
        updatePaint()
        invalidate()
    }

    fun setShape(type: shapetype) {
        currentshapetype = type
    }

    fun setBrush(brush: BrushType) {
        currentBrush = brush
        isEraser = false
        brushSize = brush.strokeWidth
        brushOpacity = brush.opacity
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
        isEraser = false
        updatePaint()
        invalidate()
    }

    fun clear() {
        strokeList.clear()
        redoList.clear()
        bufferBitmap?.eraseColor(Color.TRANSPARENT)
        tempShapeBitmap?.eraseColor(Color.TRANSPARENT)
        invalidate()
        Toast.makeText(context, "New sketch ", Toast.LENGTH_SHORT).show()
    }

    fun saveDrawing(filename: String) {
        try {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            draw(canvas)  // Draw the current view onto the bitmap

            val finalFilename = if (filename.isBlank()) "drawing_${System.currentTimeMillis()}.png" else "$filename.png"

            val contentValues = android.content.ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, finalFilename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DrawMate")
            }
            val uri: Uri? = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                Toast.makeText(context, "Drawing saved", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(context, "Failed to save drawing", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving drawing", Toast.LENGTH_SHORT).show()
        }
    }
}