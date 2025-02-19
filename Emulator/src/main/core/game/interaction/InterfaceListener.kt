package core.game.interaction

import core.api.ContentInterface
import core.game.component.Component
import core.game.node.entity.player.Player

interface InterfaceListener : ContentInterface {
    fun defineInterfaceListeners()

    fun on(
        componentID: Int,
        buttonID: Int,
        handler: (player: Player, component: Component, opcode: Int, buttonID: Int, slot: Int, itemID: Int) -> Boolean,
    ) {
        InterfaceListeners.add(componentID, buttonID, handler)
    }

    fun on(
        componentID: Int,
        handler: (player: Player, component: Component, opcode: Int, buttonID: Int, slot: Int, itemID: Int) -> Boolean,
    ) {
        InterfaceListeners.add(componentID, handler)
    }

    fun onSlotSwitch(
        componentID: Int,
        handler: (player: Player, component: Component, sourceSlot: Int, destSlot: Int) -> Boolean,
    ) {
        InterfaceListeners.onSlotSwitch(componentID, handler)
    }

    fun onOpen(
        componentID: Int,
        handler: (player: Player, component: Component) -> Boolean,
    ) {
        InterfaceListeners.addOpenListener(componentID, handler)
    }

    fun onClose(
        componentID: Int,
        handler: (player: Player, component: Component) -> Boolean,
    ) {
        InterfaceListeners.addCloseListener(componentID, handler)
    }
}
