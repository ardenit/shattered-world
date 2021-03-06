package com.mirage.gameview.drawers

import com.mirage.utils.datastructures.Rectangle
import com.mirage.utils.game.objects.properties.MoveDirection
import com.mirage.utils.virtualscreen.VirtualScreen

/** Visual representation of a template object. [DrawerTemplate] does not store state bound to a concrete object. */
interface DrawerTemplate {

    fun draw(virtualScreen: VirtualScreen, x: Float, y: Float, isOpaque: Boolean,
             action: String, actionTimePassedMillis: Long,
             isMoving: Boolean, movingTimePassedMillis: Long, moveDirection: MoveDirection = MoveDirection.DOWN_RIGHT)

    val hitBox: Rectangle
        get() = Rectangle()

}