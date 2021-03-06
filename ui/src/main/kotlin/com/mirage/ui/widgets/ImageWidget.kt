package com.mirage.ui.widgets

import com.mirage.utils.datastructures.Point
import com.mirage.utils.datastructures.Rectangle
import com.mirage.utils.virtualscreen.VirtualScreen

class ImageWidget(
        var textureName: String,
        var sizeUpdater: (Float, Float) -> Rectangle
) : Widget {

    private var rect: Rectangle = Rectangle()

    var isVisible = true

    override fun resize(virtualWidth: Float, virtualHeight: Float) {
        rect = sizeUpdater(virtualWidth, virtualHeight)
    }

    override fun touchUp(virtualPoint: Point): Boolean {
        return (rect.contains(virtualPoint))
    }

    override fun touchDown(virtualPoint: Point): Boolean {
        return (rect.contains(virtualPoint))
    }

    override fun mouseMoved(virtualPoint: Point) {}

    override fun draw(virtualScreen: VirtualScreen) {
        if (isVisible) virtualScreen.draw(textureName, rect)
    }
}