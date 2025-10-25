
package com.example.animationapp

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
import com.example.animationapp.models.BrushType
import yuku.ambilwarna.AmbilWarnaDialog
import android.util.Log

class MainActivity : AppCompatActivity() {


    var initialColor = Color.BLACK
    private var isBrushPanelVisible = false
    private var currentBrush: BrushType? = null
    private lateinit var drawingView: DrawingView
    private var brushname: TextView? = null
    private var brushicon: ImageView? = null
    private var brushpreview: ImageView? = null


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

        // Reference buttons from activity_main.xml
        val btnMenu: ImageButton = findViewById(R.id.btnMenu)
        val btnUndo: ImageButton = findViewById(R.id.btnUndo)
        val btnRedo: ImageButton = findViewById(R.id.btnRedo)
        val btnTools: ImageButton = findViewById(R.id.btnTools)
        val btnEraser: ImageButton = findViewById(R.id.btnEraser)
        val btnBrush: ImageButton = findViewById(R.id.Brushbtn)
        val btnColor: ImageButton = findViewById(R.id.btnColorPicker)
        val btnLayers: ImageButton = findViewById(R.id.btnLayers)

        // MENU button → show dropdown
        btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.menu, popup.menu)
            popup.forceShowIcons() // 👈 show icons on left side

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_new_sketch -> { showToast("New Sketch clicked"); true }
                    R.id.action_gallery -> { showToast("Gallery clicked"); true }
                    R.id.action_save -> { showToast("Save clicked"); true }
                    R.id.action_import -> { showToast("Import from Files clicked"); true }
                    R.id.action_export -> { showToast("Export clicked"); true }
                    R.id.action_preferences -> { showToast("Preferences clicked"); true }
                    else -> false
                }
            }
            popup.show()
        }


        btnEraser.setOnClickListener {
            drawingView.setEraser(true)
        }

        setupBrushPanel()

        btnBrush.setOnClickListener {
            toggleBrushPanel()
            drawingView.setEraser(false)
            currentBrush?.let { brush ->
                drawingView.setBrush(brush)
            }
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

        // Color Picker
        // Inside onCreate -> btnColor.setOnClickListener
        btnColor.setOnClickListener {
            // Get the current base color to show in the picker, ignoring alpha
            val pickerColor = initialColor and 0x00FFFFFF

            val dialog = AmbilWarnaDialog(this, pickerColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    // 'color' is the new RGB color from the picker (it's opaque).
                    // We need to combine it with the current alpha from the DrawingView.
                    val currentAlpha = drawingView.brushOpacity
                    val colorWithAlpha = (currentAlpha shl 24) or (color and 0x00FFFFFF)

                    // Now, update everything with the correctly combined color
                    initialColor = colorWithAlpha
                    currentBrush = currentBrush?.copy(color = colorWithAlpha)

                    // Explicitly tell DrawingView to update its color
                    drawingView.setBrushColor(colorWithAlpha)
                    drawingView.setEraser(false) // Ensure we are in drawing mode

                    // Update the brush preview in the settings panel
                    currentBrush?.let {
                        updateBrushPreview(it, brushpreview)
                    }
                }

                override fun onCancel(dialog: AmbilWarnaDialog) {
                    // No action needed
                }
            })
            dialog.show()
        }


        btnLayers.setOnClickListener {
        }

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

        container.removeAllViews()
        container.addView(brushView)
        setupBrushGrid(brushView) // function for yellow section
        setupBrushSettings(settingsView) // new function below

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                container.removeAllViews()
                if (tab.position == 0) container.addView(brushView)
                else {
                    container.addView(settingsView)
                    currentBrush?.let {
                        brushname?.text = it.name
                        brushicon?.setImageResource(it.iconRes)
                        updateBrushPreview(it, brushpreview)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupBrushGrid(view: View) {
        val grid = view.findViewById<GridLayout>(R.id.brushgrid)

        grid.removeAllViews()
        for (brush in BrushLibrary.brushes) {
            val image = ImageView(this).apply {
                setImageResource(brush.iconRes)
                layoutParams = ViewGroup.LayoutParams(120, 120)
                setPadding(16, 16, 16, 16)
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
                    Log.d("MainActivity", "Selected brush: ${selectedbrush.name}, textureRes: ${selectedbrush.textureRes}")
                }
            }
            grid.addView(image)
        }
    }
    private fun setupBrushSettings(view: View) {
        val sizeSeekBar = view.findViewById<SeekBar>(R.id.sizeSeekBar)
        val opacitySeekBar = view.findViewById<SeekBar>(R.id.opacitySeekBar)

        if (sizeSeekBar != null) {
            sizeSeekBar.max = 100 // Max size
            // Use drawingView.brushSize to set the progress
            sizeSeekBar.progress = drawingView.brushSize.toInt()

            sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (!fromUser) return
                    // Update the brushSize directly in DrawingView
                    drawingView.brushSize = progress.coerceAtLeast(1).toFloat()

                    // Also update the preview
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
            opacitySeekBar.max = 255
            // Use drawingView.brushOpacity to set the progress
            opacitySeekBar.progress = drawingView.brushOpacity

            opacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (!fromUser) return
                    // Update the brushOpacity directly in DrawingView
                    drawingView.brushOpacity = progress

                    // Update the preview by creating a new color with the new alpha
                    val baseColor = (initialColor and 0x00FFFFFF)
                    val colorWithAlpha = (progress shl 24) or baseColor
                    initialColor = colorWithAlpha

                    currentBrush = currentBrush?.copy(color = colorWithAlpha)
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
            style = brush.style
            strokeCap = brush.strokeCap
            strokeJoin = brush.strokeJoin
            isAntiAlias = true

            if (brush.textureRes != null) {
                val bitmap = BitmapFactory.decodeResource(resources, brush.textureRes)
                val shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
                this.shader = shader
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
