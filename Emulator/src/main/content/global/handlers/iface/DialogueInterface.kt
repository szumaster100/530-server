package content.global.handlers.iface

import org.rs.consts.Components
import core.api.ui.repositionChild
import core.game.interaction.InterfaceListener

class DialogueInterface : InterfaceListener {

    override fun defineInterfaceListeners() {
        onOpen(Components.DOUBLEOBJBOX_131) { player, _ ->
            repositionChild(player, Components.DOUBLEOBJBOX_131, 1, 96, 25)
            repositionChild(player, Components.DOUBLEOBJBOX_131, 3, 96, 98)
            return@onOpen true
        }
    }
}