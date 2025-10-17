package com.example.animationapp

import android.graphics.Color
import android.graphics.Paint
import com.example.animationapp.models.BrushType

object BrushLibrary {
    val brushes = listOf(
        BrushType("Pencil", R.drawable.brush, Color.BLACK, 4f, Paint.Cap.ROUND),
        BrushType("Marker", R.drawable.color_wheel, Color.BLACK, 12f, Paint.Cap.BUTT),
        BrushType("Round Brush", R.drawable.brush, Color.BLACK, 10f, Paint.Cap.ROUND),
        BrushType("Flat Brush", R.drawable.brush, Color.BLACK, 16f, Paint.Cap.SQUARE),
        BrushType("Soft Airbrush", R.drawable.brush, Color.BLACK, 25f, Paint.Cap.ROUND),
    )
}
