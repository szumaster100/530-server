package content.global.handlers.iface

import org.rs.consts.Components
import content.region.misc.handlers.tutorial.CharacterDesign
import core.game.interaction.InterfaceListener

class CharacterDesignInterface : InterfaceListener {

    override fun defineInterfaceListeners() {
        on(Components.APPEARANCE_771) { player, _, _, buttonID, _, _ ->
            CharacterDesign.handleButtons(player, buttonID)
            return@on true
        }
    }
}