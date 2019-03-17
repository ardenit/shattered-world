package com.mirage.connection

import com.mirage.gamelogic.LogicFacade
import com.mirage.utils.*
import com.mirage.utils.Timer
import com.mirage.utils.extensions.get
import com.mirage.utils.extensions.isMoving
import com.mirage.utils.extensions.moveDirection
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

/**
 * Реализация интерфейса Connection, работающая с локальным сервером (одиночная игра)
 */
class LocalConnection : Connection {

    private val messageListeners : MutableCollection<(msg: UpdateMessage) -> Unit> = ArrayList()

    private val logic : LogicFacade = LogicFacade()

    private var playerID: Long? = null

    override fun getPlayerID(): Long? = playerID

    override var bufferedMoveDirection : MoveDirection? = null
    override var bufferedMoving : Boolean? = null

    private val messageQueue = ArrayDeque<UpdateMessage>()
    private val queueLock = ReentrantLock()
    private var packagesCount = AtomicInteger(0)

    private val messageBufferTimer = Timer(CONNECTION_MESSAGE_BUFFER_UPDATE_INTERVAL) {
        while (!logic.msgs.isEmpty()) {
            queueLock.lock()
            logic.lockMsgQueue()
            val msg = logic.msgs.poll()
            logic.unlockMsgQueue()
            messageQueue.add(msg)
            if (msg is EndOfPackageMessage) {
                packagesCount.incrementAndGet()
            }
            queueLock.unlock()
        }

    }

    init {
        logic.addUpdateTickListener {
            bufferedMoveDirection?.let {
                logic.objects[playerID]?.moveDirection = it
            }
            bufferedMoving?.let {
                logic.objects[playerID]?.isMoving = it
            }
        }
        messageBufferTimer.start()
    }

    override fun setMoveDirection(md: MoveDirection) {
        bufferedMoveDirection = md
    }

    override fun setMoving(isMoving: Boolean) {
        bufferedMoving = isMoving
    }

    override fun startMoving(md: MoveDirection) {
        setMoveDirection(md)
        setMoving(true)
    }

    override fun stopMoving() {
        setMoving(false)
    }

    override fun addMessageListener(listener: (msg: UpdateMessage) -> Unit) {
        messageListeners.add(listener)
    }

    override fun checkNewMessages() {
        var newPackages = packagesCount.getAndSet(0)
        if (newPackages > 0) {
            queueLock.lock()
            while (newPackages > 0) {
                val msg = messageQueue.poll()
                for (msgListener in messageListeners) {
                    msgListener(msg)
                }
                if (msg is EndOfPackageMessage) --newPackages
            }
            queueLock.unlock()
        }
    }

    fun startGame() {
        logic.startGame()
        playerID = logic.addNewPlayer()
    }

    fun startLogic() = logic.startLogic()

}