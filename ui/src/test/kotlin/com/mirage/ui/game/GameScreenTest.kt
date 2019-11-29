package com.mirage.ui.game

import com.mirage.utils.DELTA_CENTER_Y
import com.mirage.utils.TILE_HEIGHT
import com.mirage.utils.TILE_WIDTH
import com.mirage.utils.TestSamples
import com.mirage.utils.game.maps.GameMap
import com.mirage.utils.game.objects.GameObject
import com.mirage.utils.game.objects.GameObjects
import com.mirage.utils.messaging.ClientMessage
import com.mirage.utils.messaging.InitialGameStateMessage
import com.mirage.utils.virtualscreen.VirtualScreen
import com.mirage.utils.virtualscreen.VirtualScreenGdxImpl
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GameScreenTest {

    @Test
    fun testSimpleStateRendering() {
        val mock = createVirtualScreenMock(TILE_WIDTH + 10f, TILE_HEIGHT + 10f)
        val oneTileMap = GameMap(1, 1, 0f, 0f, "test", 0, listOf(1), listOf(2))
        val oneObject = TestSamples.TEST_GAME_OBJECT.with(
                template = "wall",
                x = 0.5f,
                y = 0.5f
        )
        val oneObjectState = GameObjects(mapOf(0L to oneObject), 1L)
        val gameScreen = GameScreen(oneTileMap, mock)
        var lastMsg: ClientMessage? = null
        var msgCount = 0
        gameScreen.inputMessages.subscribe {
            lastMsg = it
            ++msgCount
        }
        gameScreen.handleServerMessage(InitialGameStateMessage("one-tile-test", oneObjectState, 0L, 0L))
        gameScreen.render(mock, 0L)
        verify(mock, times(1)).drawTile(eq(1), eq(0f), eq(-DELTA_CENTER_Y))
        verify(mock, times(8)).drawTile(eq(0), any(), any())
        verify(mock, times(1)).draw(eq("objects/wall"), eq(0f), eq(-DELTA_CENTER_Y))
        assertNull(lastMsg)
        assertEquals(0, msgCount)
    }


    private fun createVirtualScreenMock(width: Float, height: Float, realWidth: Float = width, realHeight: Float = height) : VirtualScreen {
        val mock: VirtualScreenGdxImpl = mock()
        whenever(mock.width) doReturn width
        whenever(mock.height) doReturn height
        whenever(mock.realWidth) doReturn realWidth
        whenever(mock.realHeight) doReturn realHeight
        whenever(mock.projectRealPointOnVirtualScreen(any())).thenCallRealMethod()
        return mock
    }
}