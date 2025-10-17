package com.example.animationapp.models

import android.graphics.Paint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BrushType(
    val name: String,
    val iconRes: Int,
    val color: Int,
    val strokeWidth: Float,
    val strokeCap: Paint.Cap,
    val strokeJoin: Paint.Join = Paint.Join.ROUND,
    val style: Paint.Style = Paint.Style.STROKE
) : Parcelable {

    /** Create a modified copy with a new color */
    fun withColor(newColor: Int): BrushType = copy(color = newColor)

    /** Create a modified copy with a new size */
    fun withSize(newSize: Float): BrushType = copy(strokeWidth = newSize)

    /** Create a modified copy with a new paint style */
    fun withStyle(newStyle: Paint.Style): BrushType = copy(style = newStyle)
}
