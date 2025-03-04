package core.game.system.timer

import core.api.hasTimerActive
import core.api.log
import core.api.registerTimer
import core.game.node.entity.Entity
import core.tools.Log

object TimerRegistry {
    val timerMap = HashMap<String, RSTimer>()
    val autoTimers = ArrayList<RSTimer>()

    @JvmStatic
    fun registerTimer(timer: RSTimer) {
        log(this::class.java, Log.WARN, "Registering timer ${timer::class.java.simpleName}")
        if (timerMap.containsKey(timer.identifier.lowercase())) {
            log(
                this::class.java,
                Log.ERR,
                "Timer identifier ${timer.identifier} already in use by ${timerMap[timer.identifier.lowercase()]!!::class.java.simpleName}! Not loading ${timer::class.java.simpleName}!",
            )
            return
        }
        timerMap[timer.identifier.lowercase()] = timer
        if (timer.isAuto) autoTimers.add(timer)
    }

    fun getTimerInstance(
        identifier: String,
        vararg args: Any,
    ): RSTimer? {
        var t = timerMap[identifier.lowercase()]
        if (args.size > 0) {
            return t?.getTimer(*args)
        } else {
            return t?.retrieveInstance()
        }
    }

    @JvmStatic
    fun addAutoTimers(entity: Entity) {
        for (timer in autoTimers) {
            if (!hasTimerActive(entity, timer.identifier)) {
                registerTimer(entity, timer.retrieveInstance())
            }
        }
    }

    inline fun <reified T> getTimerInstance(vararg args: Any): T? {
        for ((_, inst) in timerMap) {
            if (inst is T) {
                return if (args.isNotEmpty()) {
                    inst.getTimer(*args) as? T
                } else {
                    inst.retrieveInstance() as? T
                }
            }
        }
        return null
    }
}
