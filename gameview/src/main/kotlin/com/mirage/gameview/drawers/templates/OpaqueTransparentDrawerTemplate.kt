package com.mirage.gameview.drawers.templates

import com.mirage.gameview.drawers.DrawerTemplate
import com.mirage.utils.datastructures.Point
import com.mirage.utils.game.objects.properties.MoveDirection
import com.mirage.utils.virtualscreen.VirtualScreen

/**
 * Composite из двух представлений.
 * [opaqueDrawerTemplate] используется для отрисовки непрозрачного объекта.
 * [transparentDrawerTemplate] используется для отрисовки прозрачного объекта.
 * В качестве параметра isOpaque [transparentDrawerTemplate] получает true.
 * Не рекомендуется делать объект класса [OpaqueTransparentDrawerTemplate] одним из двух параметров конструктора.
 */
class OpaqueTransparentDrawerTemplate(
        private val opaqueDrawerTemplate: DrawerTemplate,
        private val transparentDrawerTemplate: DrawerTemplate
) : DrawerTemplate {

    override fun draw(virtualScreen: VirtualScreen, x: Float, y: Float, isOpaque: Boolean, action: String, actionTimePassedMillis: Long, isMoving: Boolean, movingTimePassedMillis: Long, moveDirection: MoveDirection) {
        if (isOpaque) opaqueDrawerTemplate.draw(virtualScreen, x, y, true, action, actionTimePassedMillis, isMoving, movingTimePassedMillis, moveDirection)
        else transparentDrawerTemplate.draw(virtualScreen, x, y, true, action, actionTimePassedMillis, isMoving, movingTimePassedMillis, moveDirection)
    }

}