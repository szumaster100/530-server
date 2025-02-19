package core.game.world.update.flag

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object EntityFlags {
    var flagProviders: HashMap<Int, EFlagProvider> = HashMap()

    init {
        registerFlagProviders(PlayerFlags::class)
        registerFlagProviders(NPCFlags::class)
    }

    fun registerFlagProviders(parent: KClass<*>) {
        val clazzes = parent.sealedSubclasses
        for (clazz in clazzes) {
            val p = clazz.primaryConstructor?.call() as? EFlagProvider ?: continue
            flagProviders[getMapToken(p.revision, p.type, p.flag)] = p
        }
    }

    fun getFlagFor(
        type: EFlagType,
        flag: EntityFlag,
    ): EFlagProvider? {
        val revision = 530 // Hardcoded revision value.
        return flagProviders[getMapToken(revision, type, flag)]
    }

    @JvmStatic
    fun getOrdinal(
        type: EFlagType,
        flag: EntityFlag,
    ): Int {
        return getFlagFor(type, flag)?.ordinal ?: -1
    }

    @JvmStatic
    fun getPresenceFlag(
        type: EFlagType,
        flag: EntityFlag,
    ): Int {
        return getFlagFor(type, flag)?.presenceFlag ?: 0
    }

    private fun getMapToken(
        revision: Int,
        type: EFlagType,
        flag: EntityFlag,
    ): Int {
        var token = ((revision shl 8) + (type.ordinal shl 4) + flag.ordinal)
        return token
    }
}
