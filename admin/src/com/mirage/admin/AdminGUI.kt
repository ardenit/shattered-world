package com.mirage.admin

import com.mirage.server.Room
import java.awt.*
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*



class AdminGUI(val rooms: List<Room>) : JFrame() {

    fun setServerStatus(isOnline: Boolean) {
        if (isOnline) {
            serverButton.apply {
                text = "Server: Online"
                foreground = Color.GREEN
            }
            println(serverButton.preferredSize)
        }
        else {
            serverButton.apply {
                text = "Server: Offline"
                foreground = Color.RED
            }
        }
    }

    fun setServerStatusBtnListener(listener: ActionListener) = serverButton.addActionListener(listener)

    fun updateRoomsList() {
        roomsLabel.text = "Rooms: ${rooms.size}"
        roomsList.revalidate()
        roomsScrollPane.revalidate()
    }

    fun updatePlayersOnlineCount(newCount: Int) {
        playersOnlineLabel.text = "Players online: $newCount"
    }

    fun updatePlayersInRoomList() {
        playersInRoomLabel.text = "Players in room: ${selectedRoom?.getPlayersCount() ?: 0}"
        playersList.revalidate()
        playersScrollPane.revalidate()
    }

    fun printInTerminal(text: String) {
        terminalTextArea.append(text)
        terminalScrollPane.revalidate()
    }

    fun printlnInTerminal(text: String) {
        printInTerminal(text + "\n")
    }

    fun clearTerminal() {
        terminalTextArea.text = ""
    }

    fun clearInput() {
        terminalInput.text = ""
    }

    fun setTerminalInputListener(listener: (String) -> Unit) {
        terminalInput.addKeyListener(object: KeyListener {
            override fun keyTyped(e: KeyEvent?) { }

            override fun keyPressed(e: KeyEvent?) {
                if (e?.keyCode == KeyEvent.VK_ENTER) {
                    listener(terminalInput.text)
                }
            }

            override fun keyReleased(e: KeyEvent?) { }

        })
    }

    private val guiFont = Font("Comic Sans MS", Font.BOLD, 12)
    private val terminalFont = Font("Comic Sans MS", Font.PLAIN, 16)

    private var selectedRoom: Room? = null

    /**
     * Столбец 1: Статус сервера, комнаты, список комнат
     */

    private val serverButton = JButton().apply {
        text = "Server: Offline"
        font = guiFont
        foreground = Color.RED
    }

    private val roomsLabel = JLabel().apply {
        text = "Rooms: 0"
        font = guiFont
        horizontalAlignment = JLabel.CENTER
    }

    private val roomsList = JList<String>(object: AbstractListModel<String>() {
        override fun getElementAt(index: Int): String = rooms[index].toString()
        override fun getSize(): Int = rooms.size
    }).apply {

    }

    private val roomsScrollPane = JScrollPane(roomsList).apply {
        preferredSize = Dimension(160, 131)
    }

    /**
     * Столбец 2: игроки онлайн, игроки в комнате, список игроков в комнате
     */

    private val playersOnlineLabel = JLabel().apply {
        text = "Players online: 0"
        font = guiFont
        horizontalAlignment = JLabel.CENTER
    }

    private val playersInRoomLabel = JLabel().apply {
        text = "Players in room: 0"
        font = guiFont
        horizontalAlignment = JLabel.CENTER
    }

    private val playersList = JList<String>(object: AbstractListModel<String>() {
        override fun getElementAt(index: Int): String = selectedRoom?.getPlayerByIndex(index).toString()
        override fun getSize(): Int = selectedRoom?.getPlayersCount() ?: 0
    }).apply {

    }

    private val playersScrollPane = JScrollPane(playersList).apply {
        preferredSize = Dimension(160, 131)
    }

    /**
     * Столбец 3: панель с отображением карты комнаты и терминал администратора
     */

    private val viewPanel = JPanel().apply {

    }

    private val terminalTextArea = JTextArea().apply {
        isEditable = false
        font = terminalFont
        text = "Welcome to the terminal, Administrator!\n"
    }

    private var autoScroll = true

    private val terminalScrollPane = JScrollPane(terminalTextArea).apply {
        verticalScrollBar.addAdjustmentListener { e ->
            if (e.valueIsAdjusting) {
                autoScroll = e.adjustable.value + e.adjustable.visibleAmount == e.adjustable.maximum
            }
            else if (autoScroll) {
                e.adjustable.value = e.adjustable.maximum
            }
        }
    }

    private val terminalInput = JTextField().apply {
        font = terminalFont

    }

    private val leftPanel = JPanel().apply {
        val defaultInsets = Insets(2, 2, 2, 2)
        val buttonsWeightX = 0.1
        val buttonsWeightY = 0.1
        val scrollPaneWeightY = 15.0
        layout = GridBagLayout()
        /**
         * Столбец 1
         */
        add(serverButton, GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            gridwidth = 1
            fill = GridBagConstraints.BOTH
            weightx = buttonsWeightX
            weighty = buttonsWeightY
            insets = defaultInsets
        })
        add(roomsLabel, GridBagConstraints().apply {
            gridx = 0
            gridy = 1
            gridwidth = 1
            fill = GridBagConstraints.BOTH
            weightx = buttonsWeightX
            weighty = buttonsWeightY
            insets = defaultInsets
        })
        add(roomsScrollPane, GridBagConstraints().apply {
            gridx = 0
            gridy = 2
            gridwidth = 1
            gridheight = 3
            fill = GridBagConstraints.BOTH
            weightx = buttonsWeightX
            weighty = scrollPaneWeightY
            insets = defaultInsets
        })

        /**
         * Столбец 2
         */
        add(playersOnlineLabel, GridBagConstraints().apply {
            gridx = 1
            gridy = 0
            gridwidth = 1
            gridheight = 1
            fill = GridBagConstraints.BOTH
            weightx = buttonsWeightX
            weighty = buttonsWeightY
            insets = defaultInsets
        })
        add(playersInRoomLabel, GridBagConstraints().apply {
            gridx = 1
            gridy = 1
            gridwidth = 1
            gridheight = 1
            fill = GridBagConstraints.BOTH
            weightx = buttonsWeightX
            weighty = buttonsWeightY
            insets = defaultInsets
        })
        add(playersScrollPane, GridBagConstraints().apply {
            gridx = 1
            gridy = 2
            gridwidth = 1
            gridheight = 3
            fill = GridBagConstraints.BOTH
            weightx = buttonsWeightX
            weighty = scrollPaneWeightY
            insets = defaultInsets
        })
    }

    private val rightPanel = JPanel().apply {
        val defaultInsets = Insets(2, 2, 2, 2)
        val viewWeightX = 4.0
        val viewHeightY = 2.55
        val terminalHeightY = 1.6
        val terminalInputHeightY = 0.1
        layout = GridBagLayout()

        /**
         * Столбец 3
         */
        add(viewPanel, GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            gridwidth = 1
            gridheight = 1
            fill = GridBagConstraints.BOTH
            weightx = viewWeightX
            weighty = viewHeightY
            insets = defaultInsets
        })

        add(terminalScrollPane, GridBagConstraints().apply {
            gridx = 0
            gridy = 1
            gridwidth = 1
            gridheight = 1
            fill = GridBagConstraints.BOTH
            weightx = viewWeightX
            weighty = terminalHeightY
            insets = defaultInsets
        })

        add(terminalInput, GridBagConstraints().apply {
            gridx = 0
            gridy = 2
            gridwidth = 1
            gridheight = 1
            fill = GridBagConstraints.BOTH
            weightx = viewWeightX
            weighty = terminalInputHeightY
            insets = defaultInsets
        })
    }

    private val rootPanel = JPanel().apply {

        layout = GridBagLayout()

        add(leftPanel, GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            gridwidth = 1
            gridheight = 1
            fill = GridBagConstraints.BOTH
            weightx = 0.1
            weighty = 1.0
        })

        add(rightPanel, GridBagConstraints().apply {
            gridx = 1
            gridy = 0
            gridwidth = 1
            gridheight = 1
            fill = GridBagConstraints.BOTH
            weightx = 1.5
            weighty = 1.0
        })
    }

    init {
        title = "Shattered World: Administrator"
        setSize(800, 600)
        val screen = Toolkit.getDefaultToolkit().screenSize
        setLocation(screen.width / 2 - 800 / 2, screen.height / 2 - 600 / 2)
        contentPane = rootPanel
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        isVisible = true
    }

}
