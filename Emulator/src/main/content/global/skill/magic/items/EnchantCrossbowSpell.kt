package content.global.skill.magic.items

import core.game.component.Component
import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.combat.spell.MagicSpell
import core.game.node.entity.combat.spell.SpellType
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.SpellBookManager.SpellBook
import core.plugin.Initializable
import core.plugin.Plugin
import it.unimi.dsi.fastutil.ints.Int2IntMap
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.rs.consts.Components
import org.rs.consts.Items

@Initializable
class EnchantCrossbowSpell : MagicSpell(SpellBook.MODERN, 4, 0.0, null, null, null, null) {
    init {
        SpellBook.MODERN.register(3, this)
    }

    override fun cast(
        entity: Entity,
        target: Node,
    ): Boolean {
        val player = entity as Player
        player.interfaceManager.open(Component(Components.XBOWS_ENCHANT_BOLT_432))

        val boltData: Int2IntMap = Int2IntOpenHashMap()
        boltData[17] = Items.OPAL_BOLTS_879
        boltData[21] = Items.JADE_BOLTS_9335
        boltData[25] = Items.PEARL_BOLTS_880
        boltData[28] = Items.TOPAZ_BOLTS_9336
        boltData[31] = Items.SAPPHIRE_BOLTS_9337
        boltData[34] = Items.EMERALD_BOLTS_9338
        boltData[37] = Items.RUBY_BOLTS_9339
        boltData[40] = Items.DIAMOND_BOLTS_9340
        boltData[43] = Items.DRAGON_BOLTS_9341
        boltData[46] = Items.ONYX_BOLTS_9342

        for (entry in boltData.int2IntEntrySet()) {
            player.packetDispatch.sendItemZoomOnInterface(
                entry.value,
                10,
                270,
                Components.XBOWS_ENCHANT_BOLT_432,
                entry.key,
            )
        }

        return true
    }

    override fun newInstance(arg: SpellType?): Plugin<SpellType?> {
        return this
    }
}
