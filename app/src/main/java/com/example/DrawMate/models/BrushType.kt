package com.example.DrawMate.models

import android.graphics.Paint

data class BrushType(
    val name: String,
    val iconRes: Int,
    val color: Int,
    val strokeWidth: Float,
    val strokeCap: Paint.Cap,
    val strokeJoin: Paint.Join = Paint.Join.ROUND,
    val style: Paint.Style = Paint.Style.STROKE,
    val textureRes: Int? = null,
    val category: String = "basic",
    val opacity: Int = 225
)