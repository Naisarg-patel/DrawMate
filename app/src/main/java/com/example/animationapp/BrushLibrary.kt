package com.example.animationapp

import android.graphics.Color
import android.graphics.Paint
import com.example.animationapp.models.BrushType

object BrushLibrary {
    val brushes = listOf(
        BrushType("Pencil", R.drawable.brush, Color.BLACK, 4f, Paint.Cap.ROUND, category = "basic"),
        BrushType("Marker", R.drawable.color_wheel, Color.BLACK, 12f, Paint.Cap.BUTT, category = "basic"),
        BrushType("Round Brush", R.drawable.brush, Color.BLACK, 10f, Paint.Cap.ROUND, category = "basic"),
        BrushType("Flat Brush", R.drawable.brush, Color.BLACK, 16f, Paint.Cap.SQUARE, category = "basic"),
        BrushType("Soft Airbrush", R.drawable.brush, Color.BLACK, 25f, Paint.Cap.ROUND, category = "basic"),
        BrushType("Hard Airbrush", R.drawable.brush, Color.BLACK, 40f, Paint.Cap.ROUND, category = "basic"),
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
        BrushType("Ink 1", R.drawable.ink1, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.ink1, category = "ink"),
        BrushType("Ink 2", R.drawable.ink2, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.ink2, category = "ink"),
        BrushType("Ink 3", R.drawable.ink3, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.ink3, category = "ink"),
        BrushType("Ink 4", R.drawable.ink4, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.ink4, category = "ink"),
        BrushType("Ink 5", R.drawable.ink5, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.ink5, category = "ink"),
        BrushType("Ink 6", R.drawable.ink6, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.ink6, category = "ink"),
        BrushType("Ink 7", R.drawable.ink7, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.ink7, category = "ink"),
        BrushType("Ink 8", R.drawable.ink8, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.ink8, category = "ink"),
        BrushType("Ink 9", R.drawable.ink9, Color.BLACK, 100f, Paint.Cap.ROUND, textureRes = R.drawable.ink9, category = "ink"),

    )
}