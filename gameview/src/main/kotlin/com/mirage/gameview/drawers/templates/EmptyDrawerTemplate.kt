package com.mirage.gameview.drawers.templates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mirage.utils.game.objects.GameObject
import com.mirage.gameview.drawers.DrawerTemplate

class EmptyDrawerTemplate : DrawerTemplate {
    override fun draw(batch: SpriteBatch, x: Float, y: Float, isOpaque: Boolean, action: String, actionTimePassedMillis: Long, isMoving: Boolean, movingTimePassedMillis: Long, moveDirection: GameObject.MoveDirection) {}

}