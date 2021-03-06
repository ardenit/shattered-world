package com.mirage.ui.mainmenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import com.mirage.ui.widgets.*
import com.mirage.utils.datastructures.Rectangle
import com.mirage.utils.preferences.Prefs
import com.mirage.utils.virtualscreen.VirtualScreen

private const val btnWidth = 400f
private const val btnHeight = 80f
private const val profileWindowWidth = 480f
private const val profileWindowX = - btnWidth / 2f - profileWindowWidth / 2f
private const val profileBodyHeight = 440f
internal const val profileBtnCount = 4
private const val profileBtnMargin = 20f
private const val profileArrowSize = 40f
private const val profileArrowMargin = (btnHeight - profileArrowSize) / 2f
private const val profileArrowShift = profileWindowWidth / 2f - profileArrowMargin - profileArrowSize / 2f


class MainMenuUIState(val virtualScreen: VirtualScreen, val newGame: Boolean) {


    val singlePlayerBtn = Button("ui/main-menu-btn",
            "ui/main-menu-btn-highlighted",
            "ui/main-menu-btn-pressed",
            Rectangle(),
            virtualScreen.createLabel("Campaign", 30f),
            {_, virtualHeight -> Rectangle(0f, - virtualHeight / 2 + btnHeight * (if (newGame) 5 else 7) / 2, btnWidth, btnHeight)})

    val multiPlayerBtn = Button("ui/main-menu-btn",
            "ui/main-menu-btn-highlighted",
            "ui/main-menu-btn-pressed",
            Rectangle(),
            virtualScreen.createLabel("Multiplayer", 30f),
            {_, virtualHeight -> Rectangle(0f, - virtualHeight / 2 + btnHeight * 5 / 2, btnWidth, btnHeight)}).apply {
        isVisible = !newGame
    }

    val settingsBtn = Button("ui/main-menu-btn",
            "ui/main-menu-btn-highlighted",
            "ui/main-menu-btn-pressed",
            Rectangle(),
            virtualScreen.createLabel("Settings", 30f),
            {_, virtualHeight -> Rectangle(0f, - virtualHeight / 2 + btnHeight * 3 / 2, btnWidth, btnHeight)})

    val exitBtn = Button("ui/main-menu-btn",
            "ui/main-menu-btn-highlighted",
            "ui/main-menu-btn-pressed",
            Rectangle(),
            virtualScreen.createLabel("Exit", 30f),
            {_, virtualHeight -> Rectangle(0f, - virtualHeight / 2 + btnHeight / 2, btnWidth, btnHeight)})

    val profileNameArea = Button("ui/main-menu-profile-area",
            "ui/main-menu-profile-area",
            "ui/main-menu-profile-area",
            Rectangle(),
            virtualScreen.createLabel(Prefs.account.currentProfile ?: "", 30f),
            {_, virtualHeight -> Rectangle(0f, virtualHeight / 2 - btnHeight / 2, btnWidth, btnHeight)}).apply {
        isVisible = !newGame
    }

    val changeProfileBtn = Button("ui/main-menu-btn",
            "ui/main-menu-btn-highlighted",
            "ui/main-menu-btn-pressed",
            Rectangle(),
            virtualScreen.createLabel("Change profile", 30f),
            {_, virtualHeight -> Rectangle(0f, virtualHeight / 2 - btnHeight * 3 / 2, btnWidth, btnHeight)}).apply {
        isVisible = !newGame
    }

    val profileWindowHead = ImageWidget("ui/main-menu/profile-head") {
        _, h ->
        Rectangle(profileWindowX, h / 2f - btnHeight / 2f, profileWindowWidth, btnHeight)
    }.apply {
        isVisible = !newGame
    }

    val profileWindowBody = ImageWidget("ui/main-menu/profile-body") {
        _, h ->
        Rectangle(profileWindowX, h / 2f - btnHeight - profileBodyHeight / 2f, profileWindowWidth, profileBodyHeight)
    }.apply {
        isVisible = !newGame
    }

    val profileWindowButtons = Array(profileBtnCount) {
        Button("ui/main-menu-btn",
                "ui/main-menu-btn-highlighted",
                "ui/main-menu-btn-pressed",
                Rectangle(),
                virtualScreen.createLabel("", 30f),
                {_, h -> Rectangle(profileWindowX, h / 2f - btnHeight * (it + 1.5f) - profileBtnMargin * (it + 1), btnWidth, btnHeight)}
        ).apply {
            isVisible = !newGame
        }
    }

    val profileWindowLeftArrow = Button("ui/main-menu/profile-left-arrow",
            "ui/main-menu/profile-left-arrow",
            "ui/main-menu/profile-left-arrow",
            Rectangle(),
            null,
            {_, h -> Rectangle(profileWindowX - profileArrowShift, h / 2f - btnHeight / 2f, profileArrowSize, profileArrowSize)}
    )


    val profileWindowRightArrow = Button("ui/main-menu/profile-right-arrow",
            "ui/main-menu/profile-right-arrow",
            "ui/main-menu/profile-right-arrow",
            Rectangle(),
            null,
            {_, h -> Rectangle(profileWindowX + profileArrowShift, h / 2f - btnHeight / 2f, profileArrowSize, profileArrowSize)}
    )

    val profileWindowPageLabel = LabelWidget(virtualScreen.createLabel("", 30f)) {
        _, h -> Rectangle(profileWindowX, h / 2f - btnHeight / 2f, profileWindowWidth, btnHeight)
    }

    val profilePageNavigator = PageNavigator(0, 1, profileWindowLeftArrow, profileWindowRightArrow, profileWindowPageLabel).apply {
        compositeWidget.isVisible = !newGame
    }

    val profileWindow = CompositeWidget(
            *profileWindowButtons, profilePageNavigator, profileWindowHead, profileWindowBody
    ).apply { isVisible = false }


    val widgets: List<Widget> = listOf(singlePlayerBtn, multiPlayerBtn, settingsBtn, exitBtn, profileNameArea, changeProfileBtn, profileWindow)


    fun resize(virtualWidth: Float, virtualHeight: Float) {
        for (btn in widgets) {
            btn.resize(virtualWidth, virtualHeight)
        }
    }

    init {
        resize(virtualScreen.width, virtualScreen.height)
    }
}