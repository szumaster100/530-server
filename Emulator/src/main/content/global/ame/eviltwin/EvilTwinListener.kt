package content.global.ame.eviltwin

import content.data.GameAttributes
import core.api.*
import core.game.component.Component
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.scenery.Scenery
import core.game.node.scenery.SceneryBuilder
import core.api.MapArea
import core.game.world.map.zone.ZoneBorders
import core.game.world.map.zone.ZoneRestriction

class EvilTwinListener : InteractionListener, MapArea {

    private val mollyId = (3892..3911).toIntArray()
    private val doorsId = 14982
    private val controlPanel = 14978

    override fun defineAreaBorders(): Array<ZoneBorders> {
        return arrayOf(getRegionBorders(EvilTwinUtils.region.id))
    }

    override fun getRestrictions(): Array<ZoneRestriction> {
        return arrayOf(ZoneRestriction.CANNON, ZoneRestriction.FOLLOWERS)
    }

    override fun defineListeners() {

        on(mollyId, IntType.NPC, "talk-to") { player, node ->
            if ((EvilTwinUtils.tries < 1 || EvilTwinUtils.success) && node.id >= 3892 && node.id <= 3911) {
                openDialogue(player, MollyDialogue(if (EvilTwinUtils.success) 2 else 1), node.id)
            }
            return@on true
        }

        on(controlPanel, IntType.SCENERY, "use") { player, _ ->
            if (EvilTwinUtils.success) {
                sendMessage(player, "You already caught the evil twin.")
                return@on true
            } else {
                player.interfaceManager.openSingleTab(
                    Component(240).setCloseEvent { player, c ->
                        SceneryBuilder.remove(EvilTwinUtils.currentCrane)
                        SceneryBuilder.add(Scenery(66, EvilTwinUtils.currentCrane?.location, 22, 0))
                        EvilTwinUtils.currentCrane = EvilTwinUtils.currentCrane!!.transform(EvilTwinUtils.currentCrane!!.id, EvilTwinUtils.currentCrane!!.rotation, EvilTwinUtils.region.baseLocation.transform(14, 12, 0))
                        SceneryBuilder.add(Scenery(14977, EvilTwinUtils.currentCrane?.location, 22, 0))
                        SceneryBuilder.add(EvilTwinUtils.currentCrane)
                        resetCamera(player)
                        true
                    }
                )
                player.packetDispatch.sendString("Tries: ${EvilTwinUtils.tries}", 240, 27)
                EvilTwinUtils.updateCraneCam(player, 14, 12)
            }
            return@on false
        }

        on(doorsId, IntType.SCENERY, "open") { player, node ->
            if (player.location.localX < 9 && !player.getAttribute(GameAttributes.RE_TWIN_DIAL, false)) {
                openDialogue(player, MollyDialogue(3), EvilTwinUtils.mollyNPC!!)
                return@on true
            }
            DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
            return@on true
        }
    }
}