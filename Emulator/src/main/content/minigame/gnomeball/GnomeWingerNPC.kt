package content.minigame.gnomeball

import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.NPCs
import core.api.animate
import core.api.getAttribute
import core.api.setAttribute
import core.api.spawnProjectile
import core.game.global.action.EquipHandler
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.game.node.entity.player.Player
import core.game.node.item.Item
/*
private class GnomeWingerNPC : NPCBehavior(NPCs.GNOME_WINGER_633) {

    lateinit var player: Player

    override fun tick(self: NPC): Boolean {
        player = getAttribute(self, "gnomeball:pass-player", null) ?: return false

        val pass = getAttribute(self, "gnomeball:passing", false)
        var delay = getAttribute(self, "gnomeball:pass-timer", 10)

        if (pass && delay == 0) {
            pass(player, self)
            setAttribute(self, "gnomeball:pass-timer", 10)
            setAttribute(self, "gnomeball:in-pass", false)
        }

        if (pass) setAttribute(self, "gnomeball:pass-timer", (delay - 1))
        return true
    }

    fun pass(player: Player, self: NPC) {
        self.face(player)
        animate(self, Animations.BALLER_THROW_201)
        spawnProjectile(self.location, player.location, 55, 43, 40, 0, 70, 10)
        EquipHandler.handleEquip(player, Item(Items.GNOMEBALL_751))
    }
}
*/