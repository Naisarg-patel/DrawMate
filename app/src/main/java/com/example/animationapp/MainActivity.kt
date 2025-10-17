package com.example.animationapp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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
        btnColor.setOnClickListener {
            val dialog = AmbilWarnaDialog(this, initialColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    // color is the color selected by the user.
                    initialColor = color
                    val brush = currentBrush ?: BrushLibrary.brushes.first()
                    val updatedBrush = brush.copy(color = color)
                    currentBrush = updatedBrush
                    drawingView.setEraser(false)
                    drawingView.setBrush(updatedBrush)
                    updateBrushPreview(updatedBrush, brushpreview)
                }
                override fun onCancel(dialog: AmbilWarnaDialog) {
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
                }
            }
            grid.addView(image)
        }
    }
    private fun setupBrushSettings(view: View) {
        val sizeSeekBar = view.findViewById<SeekBar>(R.id.sizeSeekBar)
        val opacitySeekBar = view.findViewById<SeekBar>(R.id.opacitySeekBar)
        val seekbarSizeHeavy = view.findViewById<SeekBar>(R.id.sizewithheavypressure)
        val seekbarSizeLight = view.findViewById<SeekBar>(R.id.sizewithlightpressure)
        val seekbarOpacityHeavy = view.findViewById<SeekBar>(R.id.opacitywithheavypressure)
        val seekbarOpacityLight = view.findViewById<SeekBar>(R.id.opacitywithlightpressure)

        if(sizeSeekBar != null) {
            sizeSeekBar?.max = 100
            sizeSeekBar?.progress = (currentBrush?.strokeWidth?.toInt() ?: 8)
            sizeSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val brush = currentBrush ?: BrushLibrary.brushes.first()
                    val newBrush = brush.copy(
                        color = initialColor, // Use the most up-to-date color
                        strokeWidth = progress.coerceAtLeast(1).toFloat()
                    )
                    currentBrush = newBrush
                    drawingView.setBrush(newBrush)
                    updateBrushPreview(newBrush, brushpreview)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
        else
            Toast.makeText(this, "seekbarSize not found", Toast.LENGTH_SHORT).show()

        if (opacitySeekBar != null) {
            opacitySeekBar?.max = 255
            val startingAlpha = (currentBrush?.color?.ushr(24) ?: 255)
            opacitySeekBar?.progress = startingAlpha
            opacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val brush = currentBrush ?: BrushLibrary.brushes.first()
                    val baseColor = (initialColor and 0x00FFFFFF)
                    val colorWithAlpha = (progress shl 24) or baseColor
                    val newBrush = brush.copy(color = colorWithAlpha)
                    currentBrush = newBrush
                    initialColor = colorWithAlpha
                    drawingView.setBrush(newBrush)
                    updateBrushPreview(newBrush, brushpreview)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
        else
            Toast.makeText(this, "seekbarOpacity not found", Toast.LENGTH_SHORT).show()


        if (seekbarSizeHeavy != null) {
            seekbarSizeHeavy.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    drawingView.maxWidth = progress.toFloat()  // e.g., 0-100
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }
        else
            Toast.makeText(this, "seekbarSizeHeavy not found", Toast.LENGTH_SHORT).show()

        if (seekbarSizeLight != null) {
            seekbarSizeLight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    drawingView.minWidth = progress.toFloat()  // e.g., 0-100
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }
        else
            Toast.makeText(this, "seekbarSizeLight not found", Toast.LENGTH_SHORT).show()

        if (seekbarOpacityHeavy != null) {
            seekbarOpacityHeavy.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    drawingView.maxAlpha = progress  // e.g., 0-255
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }
        else
            Toast.makeText(this, "seekbarOpacityHeavy not found", Toast.LENGTH_SHORT).show()

        if (seekbarOpacityLight != null) {
            seekbarOpacityLight.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    drawingView.minAlpha = progress // e.g., 0-255
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }
        else
            Toast.makeText(this, "seekbarOpacityLight not found", Toast.LENGTH_SHORT).show()
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

