package content.global.skill.smithing.smelting

import core.api.sendInputDialogue
import core.api.submitIndividualPulse
import core.game.interaction.InterfaceListener
import org.rs.consts.Components

class SmeltingInterface : InterfaceListener {
    override fun defineInterfaceListeners() {
        on(Components.SMELTING_311) { player, _, _, buttonID, _, _ ->
            val barType = BarButton.forId(buttonID) ?: return@on true
            if (barType.amount == -1) {
                player.interfaceManager.closeChatbox()
                sendInputDialogue(player, true, "Enter the amount:") { value: Any ->
                    if (value is String) {
                        submitIndividualPulse(player, SmeltingPulse(player, null, barType.bar, value.toInt()))
                    } else {
                        submitIndividualPulse(player, SmeltingPulse(player, null, barType.bar, value as Int))
                    }
                }
            } else {
                player.pulseManager.run(SmeltingPulse(player, null, barType.bar, barType.amount))
            }
            return@on true
        }
    }

    enum class BarButton(
        val button: Int,
        val bar: Bar,
        val amount: Int,
    ) {
        BRONZE_1(16, Bar.BRONZE, 1),
        BRONZE_5(15, Bar.BRONZE, 5),
        BRONZE_10(14, Bar.BRONZE, 10),
        BRONZE_X(13, Bar.BRONZE, -1),
        BLURITE_1(20, Bar.BLURITE, 1),
        BLURITE_5(19, Bar.BLURITE, 5),
        BLURITE_10(18, Bar.BLURITE, 10),
        BLURITE_X(17, Bar.BLURITE, -1),
        IRON_1(24, Bar.IRON, 1),
        IRON_5(23, Bar.IRON, 5),
        IRON_10(22, Bar.IRON, 10),
        IRON_X(21, Bar.IRON, -1),
        SILVER_1(28, Bar.SILVER, 1),
        SILVER_5(27, Bar.SILVER, 5),
        SILVER_10(26, Bar.SILVER, 10),
        SILVER_X(25, Bar.SILVER, -1),
        STEEL_1(32, Bar.STEEL, 1),
        STEEL_5(31, Bar.STEEL, 5),
        STEEL_10(30, Bar.STEEL, 10),
        STEEL_X(29, Bar.STEEL, -1),
        GOLD_1(36, Bar.GOLD, 1),
        GOLD_5(35, Bar.GOLD, 5),
        GOLD_10(34, Bar.GOLD, 10),
        GOLD_X(33, Bar.GOLD, -1),
        MITHRIL_1(40, Bar.MITHRIL, 1),
        MITHRIL_5(39, Bar.MITHRIL, 5),
        MITHRIL_10(38, Bar.MITHRIL, 10),
        MITHRIL_X(37, Bar.MITHRIL, -1),
        ADAMANT_1(44, Bar.ADAMANT, 1),
        ADAMANT_5(43, Bar.ADAMANT, 5),
        ADAMANT_10(42, Bar.ADAMANT, 10),
        ADAMANT_X(41, Bar.ADAMANT, -1),
        RUNE_1(48, Bar.RUNITE, 1),
        RUNE_5(47, Bar.RUNITE, 5),
        RUNE_10(46, Bar.RUNITE, 10),
        RUNE_X(45, Bar.RUNITE, -1),
        ;

        companion object {
            @JvmStatic
            fun forId(id: Int): BarButton? {
                for (button in BarButton.values()) {
                    if (button.button == id) {
                        return button
                    }
                }
                return null
            }
        }
    }
}
