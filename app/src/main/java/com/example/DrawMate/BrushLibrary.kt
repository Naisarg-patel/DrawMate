package com.example.DrawMate

import android.graphics.Color
import android.graphics.Paint
import com.example.DrawMate.models.BrushType

object BrushLibrary {
    val brushes = listOf(
        BrushType("Pencil", R.drawable.brush, Color.BLACK, 8f, Paint.Cap.ROUND, category = "basic", opacity = 225),
        BrushType("Calligraphy", R.drawable.color_wheel, Color.BLACK, 12f, Paint.Cap.SQUARE,Paint.Join.BEVEL, category = "basic", opacity = 225),
        BrushType("Watercolor", R.drawable.brush, Color.BLACK, 20f, Paint.Cap.ROUND, style = Paint.Style.FILL, category = "basic", opacity = 170),
        BrushType("Marker", R.drawable.brush, Color.BLACK, 16f, Paint.Cap.BUTT, category = "basic", opacity = 150),
        BrushType("soft brush", R.drawable.brush, Color.BLACK, 15f, Paint.Cap.ROUND, category = "basic", opacity = 100),
        BrushType("Hard brush", R.drawable.brush, Color.BLACK, 40f, Paint.Cap.ROUND, category = "basic", opacity = 255),
        BrushType(
            "half tone star",
            R.drawable.halftonestart,
            Color.BLACK,
            100f,
            Paint.Cap.ROUND,
            textureRes = R.drawable.halftonestart,
            category = "halftone"
        ),
        BrushType(
            "half tone hexagon",
            R.drawable.halftonehexagon,
            Color.BLACK,
            100f,
            Paint.Cap.ROUND,
            textureRes = R.drawable.halftonehexagon,
            category = "halftone"
        ),
        BrushType(
            "crosshatch",
            R.drawable.cross,
            Color.BLACK,
            100f,
            Paint.Cap.ROUND,
            textureRes = R.drawable.cross,
            category = "halftone"
        ),
        BrushType(
            " Half tone dot inv 1",
            R.drawable.dot1,
            Color.BLACK,
            100f,
            Paint.Cap.ROUND,
            textureRes = R.drawable.dot1,
            category = "halftone"
        ),
        BrushType(
            "half tone dot inv 2 ",
            R.drawable.dot2,
            Color.BLACK,
            15f,
            Paint.Cap.ROUND,
            textureRes = R.drawable.dot2,
            category = "halftone"
        ),
        BrushType(
            "half tone dot 1",
            R.drawable.dotpat1,
            Color.BLACK,
            100f,
            Paint.Cap.ROUND,
            textureRes = R.drawable.dotpat1,
            category = "halftone"
        ),
        BrushType(
            "half tone dot 2",
            R.drawable.dotpat2,
            Color.BLACK,
            100f,
            Paint.Cap.ROUND,
            textureRes = R.drawable.dotpat2,
            category = "halftone"
        ),
        BrushType(
            "half tone dot 3",
            R.drawable.dotpat3,
            Color.BLACK,
            100f,
            Paint.Cap.ROUND,
            textureRes = R.drawable.dotpat3,
            category = "halftone"
        ),
        BrushType("half tone dot 4",R.drawable.dotpat4, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.dotpat4, category = "halftone"),
        BrushType("half tone grid 1",R.drawable.grid, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.grid, category = "halftone"),
        BrushType("half tone grid 2",R.drawable.grid1, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.grid1, category = "halftone"),
        BrushType("half tone grid 3",R.drawable.grid3, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.grid3, category = "halftone"),

        )
}