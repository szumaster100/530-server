package content.global.skill.magic

import core.api.playAudio
import core.api.playGlobalAudio
import core.api.setAttribute
import core.cache.def.impl.ItemDefinition
import core.game.interaction.Listener
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.GameWorld
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.Graphics

abstract class SpellListener(
    val bookName: String,
) : Listener {
    companion object {
        @JvmField
        val NPC = -1

        @JvmField
        val OBJECT = -2

        @JvmField
        val ITEM = -3

        @JvmField
        val PLAYER = -4

        @JvmField
        val NONE = -5

        @JvmField
        val GROUND_ITEM = -6
    }

    fun onCast(
        spellID: Int,
        type: Int,
        range: Int = 10,
        method: (player: Player, node: Node?) -> Unit,
    ) {
        SpellListeners.add(spellID, type, bookName, range, method)
    }

    fun onCast(
        spellID: Int,
        type: Int,
        vararg ids: Int,
        range: Int = 10,
        method: (player: Player, node: Node?) -> Unit,
    ) {
        SpellListeners.add(spellID, type, ids, bookName, range, method)
    }

    fun requires(
        player: Player,
        magicLevel: Int = 0,
        runes: Array<Item> = arrayOf(),
        specialEquipment: IntArray = intArrayOf(),
    ) {
        if (player.getAttribute("magic-delay", 0) > GameWorld.ticks) {
            throw IllegalStateException("You are currently delayed from casting spells.")
        }
        if (player.getAttribute("tablet-spell", false)) {
            return
        }

        if (player.skills.getLevel(Skills.MAGIC) < magicLevel) {
            player.sendMessage("You need a magic level of $magicLevel to cast this spell.")
            throw IllegalStateException("Insufficient magic level.")
        }

        for (rune in runes) {
            if (!SpellUtils.hasRune(player, rune)) {
                player.sendMessage("You don't have enough ${rune.definition.name.lowercase()}s to cast this spell.")
                throw IllegalStateException("Not enough runes.")
            }
        }

        for (item in specialEquipment) {
            if (!player.equipment.contains(item, 1)) {
                player.sendMessage("You need a ${ItemDefinition.forId(item).name} to cast this.")
                throw IllegalStateException("Missing special equipment.")
            }
        }
    }

    fun removeRunes(
        player: Player,
        removeAttr: Boolean = true,
    ) {
        player.inventory.remove(*player.getAttribute("spell:runes", ArrayList<Item>()).toTypedArray())
        if (removeAttr) {
            player.removeAttribute("spell:runes")
            player.removeAttribute("tablet-spell")
        }
    }

    fun addXP(
        player: Player,
        amount: Double,
    ) {
        if (player.getAttribute("tablet-spell", false)) return
        player.skills.addExperience(Skills.MAGIC, amount)
    }

    fun visualizeSpell(
        player: Player,
        anim: Animation,
        gfx: Graphics,
        soundID: Int = -1,
        delay: Int = 0,
        global: Boolean = true,
    ) {
        if (player.getAttribute("tablet-spell", false)) return
        player.visualize(anim, gfx)
        if (soundID != -1) {
            if (global) {
                playGlobalAudio(player.location, soundID, delay)
            } else {
                playAudio(player, soundID, delay)
            }
        }
    }

    fun visualizeSpell(
        player: Player,
        anim: Int,
        gfx: Int,
        height: Int = 0,
        soundID: Int = -1,
        delay: Int = 0,
        global: Boolean = true,
    ) {
        if (player.getAttribute("tablet-spell", false)) return
        player.visualize(Animation(anim), Graphics(gfx, height))
        if (soundID != -1) {
            if (global) {
                playGlobalAudio(player.location, soundID, delay)
            } else {
                playAudio(player, soundID, delay)
            }
        }
    }

    fun setDelay(
        player: Player,
        isTeleport: Boolean = false,
    ) {
        if (!isTeleport) {
            player.setAttribute("magic-delay", GameWorld.ticks + 3)
        } else {
            player.setAttribute("magic-delay", GameWorld.ticks + 5)
        }
    }

    fun setDelay(
        player: Player,
        delay: Int,
    ) {
        setAttribute(player, "magic-delay", GameWorld.ticks + delay)
    }

    fun interrupt(player: Player) {
        player.pulseManager.clear()
    }

    fun showMagicTab(player: Player) {
        player.interfaceManager.setViewedTab(6)
    }
}
