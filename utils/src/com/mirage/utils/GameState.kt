package com.mirage.utils

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import java.util.*

class GameState {
    val objects : MutableMap<Long, MapObject> = TreeMap()
    var map = TiledMap()
    var playerID : Long? = null
}