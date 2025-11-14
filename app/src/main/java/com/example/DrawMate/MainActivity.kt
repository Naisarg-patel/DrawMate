
package com.example.DrawMate

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayout
import android.widget.GridLayout
import com.example.DrawMate.models.BrushType
import yuku.ambilwarna.AmbilWarnaDialog
import android.view.LayoutInflater
import android.widget.PopupWindow

class MainActivity : AppCompatActivity() {


    var initialColor = Color.BLACK
    private var isBrushPanelVisible = false
    private var currentBrush: BrushType? = null
    private lateinit var drawingView: DrawingView
    private var brushname: TextView? = null
    private var brushicon: ImageView? = null
    private var brushpreview: ImageView? = null
    private var sizeSeekBar: SeekBar? = null
    private var opacitySeekBar: SeekBar? = null
    private var toolpopwindow: PopupWindow? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawingView = findViewById(R.id.drawingView)
        currentBrush = BrushLibrary.brushes.first()
        drawingView.setBrush(currentBrush!!)

        val btnMenu: ImageButton = findViewById(R.id.btnMenu)
        val btnUndo: ImageButton = findViewById(R.id.btnUndo)
        val btnRedo: ImageButton = findViewById(R.id.btnRedo)
        val btnTools: ImageButton = findViewById(R.id.btnTools)
        val btnEraser: ImageButton = findViewById(R.id.btnEraser)
        val btnBrush: ImageButton = findViewById(R.id.Brushbtn)
        val btnColor: ImageButton = findViewById(R.id.btnColorPicker)
        val btnLayers: ImageButton = findViewById(R.id.btnLayers)

        btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.menu, popup.menu)
            popup.forceShowIcons() // 👈 show icons on left side

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_new_sketch -> {
                        showToast("New Sketch not implemented yet"); true
                    }

                    R.id.action_save -> {
                        showToast("Save not implemented yet"); true
                    }

                    else -> false
                }
            }
            popup.show()
        }

        btnEraser.setOnClickListener {
            drawingView.setEraser(true)
            drawingView.setTool(DrawingView.Tooltype.ERASER)
        }

        setupBrushPanel()

        btnBrush.setOnClickListener {
            toggleBrushPanel()
            drawingView.setEraser(false)
            currentBrush?.let { brush ->
                drawingView.setBrush(brush)
            }
            drawingView.setTool(DrawingView.Tooltype.BRUSH)
        }

        drawingView.onTouchStartListener = {
            if (isBrushPanelVisible) {
                hideBrushPanel()
            }
        }

        btnUndo.setOnClickListener {
            drawingView.undo()
        }

        btnRedo.setOnClickListener {
            drawingView.redo()
        }

        btnColor.setOnClickListener {
            val pickerColor = initialColor and 0x00FFFFFF

            val dialog =
                AmbilWarnaDialog(this, pickerColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                    override fun onOk(dialog: AmbilWarnaDialog, color: Int) {

                        val currentAlpha = drawingView.brushOpacity
                        val colorWithAlpha = (currentAlpha shl 24) or (color and 0x00FFFFFF)

                        initialColor = colorWithAlpha
                        currentBrush = currentBrush?.copy(color = colorWithAlpha)

                        drawingView.setBrushColor(colorWithAlpha)
                        drawingView.setEraser(false)

                        currentBrush?.let {
                            updateBrushPreview(it, brushpreview)
                        }
                    }

                    override fun onCancel(dialog: AmbilWarnaDialog) {
                    }
                })
            dialog.show()
        }


        btnLayers.setOnClickListener {
            showToast("Layers not implemented yet")
        }


        btnTools.setOnClickListener {
            if (toolpopwindow?.isShowing == true) {
                toolpopwindow?.dismiss()
            } else {
                showtoolpopup(it)
            }
        }
    }

    private fun showtoolpopup(anchorView: View){
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.tool, null)

        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        toolpopwindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        toolpopwindow?.isFocusable = true
        toolpopwindow?.elevation = 20f

        val rect = popupView.findViewById<View>(R.id.rectangles)
        val circ = popupView.findViewById<View>(R.id.circle)
        val line = popupView.findViewById<View>(R.id.line)

        rect.setOnClickListener {
            drawingView.setTool(DrawingView.Tooltype.SHAPE)
            drawingView.setShape(DrawingView.shapetype.RECTANGLE)
            toolpopwindow?.dismiss()
        }

        circ.setOnClickListener {
            drawingView.setTool(DrawingView.Tooltype.SHAPE)
            drawingView.setShape(DrawingView.shapetype.CIRCLE)
            toolpopwindow?.dismiss()
        }

        line.setOnClickListener {
            drawingView.setTool(DrawingView.Tooltype.SHAPE)
            drawingView.setShape(DrawingView.shapetype.LINE)
            toolpopwindow?.dismiss()
        }

        val xOffset = (anchorView.width - popupView.measuredWidth) / 2
        toolpopwindow?.showAsDropDown(anchorView, xOffset, 0)

    }
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun PopupMenu.forceShowIcons() {
        try {
            val fields = PopupMenu::class.java.getDeclaredFields()
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(this)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons =
                        classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleBrushPanel() {
        val panel = findViewById<View>(R.id.brushPanel)
        if (isBrushPanelVisible) hideBrushPanel() else showBrushPanel()
    }

    private fun showBrushPanel() {
        val panel = findViewById<View>(R.id.brushPanel)
        panel.visibility = View.VISIBLE
        panel.translationY = -panel.height.toFloat()
        panel.animate()
            .translationY(0f)
            .setDuration(250)
            .start()
        isBrushPanelVisible = true
    }

    fun hideBrushPanel() {
        val panel = findViewById<View>(R.id.brushPanel)
        if (isBrushPanelVisible) {
            panel.animate()
                .translationY(-panel.height.toFloat())
                .setDuration(250)
                .withEndAction { panel.visibility = View.GONE }
                .start()
            isBrushPanelVisible = false
        }
    }

    private fun setupBrushPanel() {
        val container = findViewById<FrameLayout>(R.id.brushContentContainer)
        val tabs = findViewById<TabLayout>(R.id.brushTabs)
        val inflater = layoutInflater
        val brushView = inflater.inflate(R.layout.brush_tab_brushes, container, false)
        val settingsView = inflater.inflate(R.layout.brush_tab_settings, container, false)

        brushname = findViewById(R.id.selectedBrushName)
        brushicon = findViewById(R.id.selectedBrushIcon)
        brushpreview = findViewById(R.id.brushPreview)
        brushname?.text = currentBrush?.name
        brushicon?.setImageResource(currentBrush?.iconRes ?: 0 )

        container.removeAllViews()
        container.addView(brushView)
        setupBrushGrid(brushView)
        setupBrushSettings(settingsView)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                container.removeAllViews()
                if (tab.position == 0)
                    container.addView(brushView)
                else {
                    container.addView(settingsView)
                    currentBrush?.let {
                        brushname?.text = it.name
                        brushicon?.setImageResource(it.iconRes)
                        updateBrushPreview(it, brushpreview)
                        sizeSeekBar?.progress = it.strokeWidth.toInt()
                        opacitySeekBar?.progress = it.opacity
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupBrushGrid(view: View) {
        val basicgrid = view.findViewById<GridLayout>(R.id.basicbrushgrid)
        val halftonegrid = view.findViewById<GridLayout>(R.id.halftonebrushgrid)
        val inkgrid = view.findViewById<GridLayout>(R.id.inkbrushgrid)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val spacing = (8 * displayMetrics.density).toInt() * 2
        val columnCount = 5
        val itemSize = (screenWidth / columnCount) - spacing

        val basicBrushes = BrushLibrary.brushes.filter { it.category == "basic" }
        val halftoneBrushes = BrushLibrary.brushes.filter { it.category == "halftone" }
        val inkBrushes = BrushLibrary.brushes.filter { it.category == "ink" }


        basicgrid.removeAllViews()

        fun populateGrid(grid: GridLayout, brushes: List<BrushType>) {
            for (brush in brushes) {
                val image = ImageView(this).apply {
                    setImageResource(brush.iconRes)
                    layoutParams = ViewGroup.LayoutParams(itemSize, itemSize)
                    setPadding(12, 12, 12, 12)
                    adjustViewBounds = true
                    isClickable = true
                    isFocusable = true
                    setOnClickListener {
                        val selectedbrush = brush.copy(color = initialColor)
                        currentBrush = selectedbrush
                        brushname?.text = selectedbrush.name
                        brushicon?.setImageResource(selectedbrush.iconRes)
                        updateBrushPreview(selectedbrush, brushpreview)
                        drawingView.setEraser(false)
                        drawingView.setBrush(selectedbrush)

                        opacitySeekBar?.progress = selectedbrush.opacity
                    }
                }
                grid.addView(image)
            }
        }
        populateGrid(basicgrid, basicBrushes)
        populateGrid(halftonegrid, halftoneBrushes)
        populateGrid(inkgrid, inkBrushes)
    }
    private fun setupBrushSettings(view: View) {
        val sizeSeekBar = view.findViewById<SeekBar>(R.id.sizeSeekBar)
        val opacitySeekBar = view.findViewById<SeekBar>(R.id.opacitySeekBar)

        if (sizeSeekBar != null) {
            sizeSeekBar.max = 100
            sizeSeekBar.progress = drawingView.brushSize.toInt()

            sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (!fromUser) return

                    drawingView.brushSize = progress.coerceAtLeast(1).toFloat()

                    currentBrush = currentBrush?.copy(strokeWidth = drawingView.brushSize)
                    currentBrush?.let { updateBrushPreview(it, brushpreview) }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        } else {
            Toast.makeText(this, "seekbarSize not found", Toast.LENGTH_SHORT).show()
        }

        if (opacitySeekBar != null) {
            opacitySeekBar?.max = 255

            opacitySeekBar?.progress = currentBrush?.opacity?: 255

            opacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (!fromUser) return

                    drawingView.brushOpacity = progress

                    val baseColor = (initialColor and 0x00FFFFFF)
                    val colorWithAlpha = (progress shl 24) or baseColor
                    initialColor = colorWithAlpha

                    currentBrush = currentBrush?.copy(opacity = progress, color = colorWithAlpha)
                    drawingView.setBrushColor(colorWithAlpha)
                    currentBrush?.let { updateBrushPreview(it, brushpreview) }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        } else {
            Toast.makeText(this, "seekbarOpacity not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBrushPreview(brush: BrushType, imageView: ImageView?) {
        if (imageView == null) return
        val width = 800
        val height = 200
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = brush.color
            strokeWidth = brush.strokeWidth
            alpha = brush.opacity
            style = brush.style
            strokeCap = brush.strokeCap
            strokeJoin = brush.strokeJoin
            isAntiAlias = true

            if (brush.textureRes != null) {
                val bitmap = BitmapFactory.decodeResource(resources, brush.textureRes)
                val shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                this.shader = shader
                this.colorFilter = android.graphics.PorterDuffColorFilter(brush.color, android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }

        val path = Path().apply {
            moveTo(20f, height / 2f)
            quadTo(width / 3f, height / 3f, width - 40f, height / 2f)
        }

        canvas.drawPath(path, paint)
        imageView.setImageBitmap(bitmap)
    }

    private fun updateBrushPreview(brush: BrushType) {
        val preview = findViewById<ImageView?>(R.id.brushPreview)
        updateBrushPreview(brush, preview)
    }

}