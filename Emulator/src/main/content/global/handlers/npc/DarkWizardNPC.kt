package content.global.handlers.npc

import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.combat.spell.CombatSpell
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.player.link.SpellBookManager.SpellBook
import core.game.world.map.Location
import core.plugin.Initializable
import core.tools.RandomFunction
import org.rs.consts.Items

@Initializable
class DarkWizardNPC(
    id: Int = 0,
    location: Location? = null,
) : AbstractNPC(id, location) {
    override fun construct(
        id: Int,
        location: Location,
        vararg objects: Any,
    ): AbstractNPC {
        return DarkWizardNPC(id, location)
    }

    override fun init() {
        super.init()
        properties.combatPulse.style = CombatStyle.MAGIC
        isAggressive = true
        setDefault()
    }

    override fun onImpact(
        entity: Entity,
        state: BattleState,
    ) {
        super.onImpact(entity, state)
        if (getAttribute("switched", false)) {
            removeAttribute("switched")
            setDefault()
            return
        }
        if (RandomFunction.random(6) > 4) {
            spells = spells
            setAttribute("switched", true)
        }
    }

    private fun setDefault() {
        properties.autocastSpell = SpellBook.MODERN.getSpell(if (id == 172) 6 else 4) as CombatSpell?
    }

    private var spells: IntArray
        get() {
            var index = 0
            for (i in ID.indices) {
                if (ID[i] == id) {
                    index = i
                    break
                }
            }
            return SPELLS[index]
        }
        private set(ids) {
            properties.autocastSpell = SpellBook.MODERN.getSpell(ids[RandomFunction.random(ids.size)]) as CombatSpell?
        }

    override fun getIds(): IntArray {
        return ID
    }

    companion object {
        private val ID = intArrayOf(Items.RANGING_POTION2_172, Items.RANGING_POTION1_174)
        private val SPELLS = arrayOf(intArrayOf(6, 7), intArrayOf(4, 2))
    }
}
