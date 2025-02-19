package content.minigame.castlewars.areas

import content.data.God
import content.minigame.castlewars.CastleWars
import content.minigame.castlewars.CastleWarsOverlay
import core.api.*
import core.game.component.Component
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.world.map.zone.ZoneBorders
import core.tools.ticksPerMinute
import org.rs.consts.Components

class CastleWarsWaitingArea :
    CastleWarsArea(),
    TickListener {
    companion object {
        val zamorakWaitingRoom: ZoneBorders = ZoneBorders(2432, 9510, 2407, 9534, 0)
        val saradominWaitingRoom: ZoneBorders = ZoneBorders(2394, 9497, 2369, 9481, 0)

        val areaBorders = arrayOf(zamorakWaitingRoom, saradominWaitingRoom)

        val waitingSaradominPlayers = mutableSetOf<Player>()
        val waitingZamorakPlayers = mutableSetOf<Player>()
    }

    override fun defineAreaBorders(): Array<ZoneBorders> {
        return areaBorders
    }

    override fun areaEnter(entity: Entity) {
        val player = entity as? Player ?: return
        super.areaEnter(player)
        registerTimer(
            player,
            spawnTimer("teleblock", (CastleWars.gameCooldownMinutes + CastleWars.gameTimeMinutes) * 60 * 2),
        )

        if (zamorakWaitingRoom.insideBorder(player.location)) {
            player.equipment.replace(Item(CastleWars.zamorakTeamHoodedCloak), 1)
            waitingZamorakPlayers.add(player)
        } else if (saradominWaitingRoom.insideBorder(player.location)) {
            player.equipment.replace(Item(CastleWars.saradominTeamHoodedCloak), 1)
            waitingSaradominPlayers.add(player)
        }

        var transformed = false
        if (player.attributes[CastleWars.portalAttribute] == CastleWars.guthixName &&
            (hasGodItem(player, God.ZAMORAK) || hasGodItem(player, God.SARADOMIN))
        ) {
            player.appearance.transformNPC(CastleWars.sheep)
            sendDialogue(
                player,
                "I pity your faith in my brothers. I shall bless you with some time in the most holy of forms; maybe its wisdom will rub off on you and you'll see the error of your ways.",
            )
            transformed = true
        }
        if (player.attributes[CastleWars.portalAttribute] == CastleWars.zamorakName &&
            hasGodItem(player, God.SARADOMIN) ||
            hasGodItem(player, God.GUTHIX)
        ) {
            player.appearance.transformNPC(CastleWars.imp)
            sendDialogue(
                player,
                "You're wearing objects of my ignorant brothers and you come to me? Such treachery must be rewarded! Enjoy some time in the most mischievous of forms.",
            )
            transformed = true
        }
        if (player.attributes[CastleWars.portalAttribute] == CastleWars.saradominName &&
            hasGodItem(player, God.ZAMORAK) ||
            hasGodItem(player, God.GUTHIX)
        ) {
            player.appearance.transformNPC(CastleWars.rabbit)
            sendDialogue(
                player,
                "You wear objects of my foolish brothers? Perhaps some time spent as the lowliest of forms will help you appreciate the gifts that I can bestow upon my followers.",
            )
            transformed = true
        }

        if (transformed) {
            player.interfaceManager.removeTabs(0, 1, 2, 3, 4, 5, 6, 11, 12)
            player.walkingQueue.isRunDisabled = true
        }

        player.removeAttribute(CastleWars.portalAttribute)

        player.interfaceManager.openOverlay(Component(Components.CASTLEWARS_STATUS_OVERLAY_57))
    }

    override fun exitArea(player: Player) {
        super.exitArea(player)
        waitingSaradominPlayers.remove(player)
        waitingZamorakPlayers.remove(player)
    }

    override fun tick() {
        var nextStart = Int.MAX_VALUE
        if (CastleWarsGameArea.ticksLeftInGame >= 0) {
            nextStart = CastleWarsGameArea.ticksLeftInGame + CastleWars.gameCooldownMinutes * ticksPerMinute
        } else if (waitingSaradominPlayers.isEmpty() || waitingZamorakPlayers.isEmpty()) {
            CastleWarsGameArea.ticksLeftInGame = -1
        } else {
            nextStart = CastleWars.gameCooldownMinutes * ticksPerMinute + CastleWarsGameArea.ticksLeftInGame
        }

        for (player in waitingSaradominPlayers + waitingZamorakPlayers) {
            CastleWarsOverlay.sendLobbyUpdate(
                player,
                waitingSaradominPlayers.isNotEmpty() &&
                    waitingZamorakPlayers.isNotEmpty() ||
                    CastleWarsGameArea.ticksLeftInGame >= 0,
                (nextStart - 1) / ticksPerMinute + 1,
            )
        }

        if (nextStart <= 0) {
            CastleWarsGameArea.startGame()
        }
    }
}
