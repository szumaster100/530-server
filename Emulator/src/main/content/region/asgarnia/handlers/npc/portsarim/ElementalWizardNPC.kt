package content.region.asgarnia.handlers.npc.portsarim

import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.combat.spell.CombatSpell
import core.game.node.entity.combat.spell.MagicSpell
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.SpellBookManager.SpellBook
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.entity.skill.Skills
import core.game.world.map.Location
import core.plugin.Initializable
import core.tools.RandomFunction
import org.rs.consts.NPCs

@Initializable
class ElementalWizardNPC : AbstractNPC {
    constructor(id: Int, location: Location?) : super(id, location, true) {
        properties.combatPulse.style = CombatStyle.MAGIC
    }

    constructor() : super(0, null)

    override fun construct(
        id: Int,
        location: Location,
        vararg objects: Any,
    ): AbstractNPC {
        return ElementalWizardNPC(id, location)
    }

    override fun onImpact(
        entity: Entity,
        state: BattleState,
    ) {
        if (state.spell != null && isSpellType(state.spell)) {
            state.estimatedHit = 0
            state.maximumHit = 0
            sendChat("Gratias tibi ago")
            getSkills().heal(getSkills().getStaticLevel(Skills.HITPOINTS))
            val attacker = state.attacker
            if (attacker is Player &&
                !attacker
                    .asPlayer()
                    .achievementDiaryManager
                    .getDiary(DiaryType.FALADOR)!!
                    .isComplete(0, 8)
            ) {
                attacker
                    .asPlayer()!!
                    .achievementDiaryManager
                    .getDiary(DiaryType.FALADOR)!!
                    .updateTask(attacker.asPlayer(), 0, 8, true)
            }
        }
        if (getAttribute("switch", false)) {
            setBaseSpell()
            removeAttribute("switch")
        }
        if (RandomFunction.random(6) > 4) {
            setSpell()
        }
        super.onImpact(entity, state)
    }

    private fun setSpell() {
        val spells = SPELLS[spellIndex]
        properties.autocastSpell = SpellBook.MODERN.getSpell(spells[RandomFunction.random(spells.size)]) as CombatSpell?
        setAttribute("switch", true)
    }

    private fun setBaseSpell() {
        properties.autocastSpell =
            SpellBook.MODERN.getSpell(SPELLS[spellIndex][0]) as CombatSpell?
    }

    private fun isSpellType(spell: MagicSpell): Boolean {
        when (spellIndex) {
            0 -> return spell.javaClass.simpleName.startsWith("Fire")
            1 -> return spell.javaClass.simpleName.startsWith("Water")
            2 -> return spell.javaClass.simpleName.startsWith("Earth")
            3 -> return spell.javaClass.simpleName.startsWith("Air")
        }
        return false
    }

    private val spellIndex: Int
        get() {
            for (i in ids.indices) {
                if (ids[i] == id) {
                    return i
                }
            }
            return 0
        }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.FIRE_WIZARD_2709, NPCs.WATER_WIZARD_2710, NPCs.EARTH_WIZARD_2711, NPCs.AIR_WIZARD_2712)
    }

    companion object {
        private val SPELLS = arrayOf(intArrayOf(8, 7), intArrayOf(4, 7), intArrayOf(6, 7), intArrayOf(1, 7))
    }
}
