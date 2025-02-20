package core.game.event

import content.global.activity.jobs.JobType
import content.global.handlers.iface.FairyRing
import content.global.skill.magic.TeleportMethod
import core.game.component.Component
import core.game.dialogue.Dialogue
import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.link.SpellBookManager.SpellBook
import core.game.node.entity.player.link.TeleportManager.TeleportType
import core.game.node.entity.player.link.prayer.PrayerType
import core.game.node.item.Item
import core.game.world.map.Location

data class ResourceProducedEvent(
    val itemId: Int,
    val amount: Int,
    val source: Node,
    val original: Int = -1,
) : Event

data class NPCKillEvent(
    val npc: NPC,
) : Event

data class BoneBuryEvent(
    val boneId: Int,
) : Event

data class TeleportEvent(
    val type: TeleportType,
    val method: TeleportMethod,
    val source: Any,
    val location: Location,
) : Event

data class LitFireEvent(
    val logId: Int,
) : Event

data class LitLightSourceEvent(
    val litLightSourceId: Int,
) : Event

data class InteractionEvent(
    val target: Node,
    val option: String,
) : Event

data class ButtonClickEvent(
    val iface: Int,
    val buttonId: Int,
) : Event

data class DialogueOpenEvent(
    val dialogue: Dialogue,
) : Event

data class DialogueOptionSelectionEvent(
    val dialogue: Any,
    val currentStage: Int,
    val optionId: Int,
) : Event

data class DialogueCloseEvent(
    val dialogue: Dialogue,
) : Event

data class UseWithEvent(
    val used: Int,
    val with: Int,
) : Event

data class SelfDeathEvent(
    val killer: Entity,
) : Event

data class TickEvent(
    val worldTicks: Int,
) : Event

data class PickUpEvent(
    val itemId: Int,
) : Event

data class InterfaceOpenEvent(
    val component: Component,
) : Event

data class InterfaceCloseEvent(
    val component: Component,
) : Event

data class AttributeSetEvent(
    val entity: Entity,
    val attribute: String,
    val value: Any,
) : Event

data class AttributeRemoveEvent(
    val entity: Entity,
    val attribute: String,
) : Event

data class SpellCastEvent(
    val spellBook: SpellBook,
    val spellId: Int,
    val target: Node? = null,
) : Event

data class ItemAlchemizationEvent(
    val itemId: Int,
    val isHigh: Boolean,
) : Event

data class ItemEquipEvent(
    val itemId: Int,
    val slotId: Int,
) : Event

data class ItemUnequipEvent(
    val itemId: Int,
    val slotId: Int,
) : Event

data class ItemShopPurchaseEvent(
    val itemId: Int,
    val amount: Int,
    val currency: Item,
) : Event

data class ItemShopSellEvent(
    val itemId: Int,
    val amount: Int,
    val currency: Item,
) : Event

data class JobAssignmentEvent(
    val jobType: JobType,
    val employerNpc: NPC,
) : Event

data class FairyRingDialEvent(
    val fairyRing: FairyRing,
) : Event

data class VarbitUpdateEvent(
    val offset: Int,
    val value: Int,
) : Event

data class DynamicSkillLevelChangeEvent(
    val skillId: Int,
    val oldValue: Int,
    val newValue: Int,
) : Event

data class SummoningPointsRechargeEvent(
    val obelisk: Node,
) : Event

data class PrayerPointsRechargeEvent(
    val altar: Node,
) : Event

data class XPGainEvent(
    val skillId: Int,
    val amount: Double,
) : Event

data class PrayerActivatedEvent(
    val type: PrayerType,
) : Event

data class PrayerDeactivatedEvent(
    val type: PrayerType,
) : Event
