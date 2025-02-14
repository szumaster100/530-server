package core.api

import com.moandjiezana.toml.Toml
import content.data.God
import core.ServerConfig
import core.api.item.itemDefinition
import core.api.movement.finishedMoving
import core.api.utils.GlobalKillCounter
import core.api.utils.PlayerCamera
import core.api.utils.Vector
import core.cache.def.impl.AnimationDefinition
import core.cache.def.impl.ItemDefinition
import core.cache.def.impl.SceneryDefinition
import core.cache.def.impl.VarbitDefinition
import core.game.activity.Cutscene
import core.game.component.Component
import core.game.container.impl.EquipmentContainer
import core.game.dialogue.*
import core.game.dialogue.DialogueInterpreter.getDialogueKey
import core.game.interaction.Clocks
import core.game.interaction.InteractionListeners
import core.game.interaction.QueueStrength
import core.game.interaction.QueuedScript
import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.combat.CombatSwingHandler
import core.game.node.entity.combat.ImpactHandler
import core.game.node.entity.impl.Animator
import core.game.node.entity.impl.ForceMovement
import core.game.node.entity.impl.Projectile
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.player.info.LogType
import core.game.node.entity.player.info.PlayerMonitor
import core.game.node.entity.player.link.HintIconManager
import core.game.node.entity.player.link.IronmanMode
import core.game.node.entity.player.link.TeleportManager
import core.game.node.entity.player.link.audio.Audio
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.entity.player.link.emote.Emotes
import core.game.node.entity.player.link.prayer.PrayerType
import core.game.node.entity.player.link.quest.QPCumulative
import core.game.node.entity.player.link.quest.QPReq
import core.game.node.entity.player.link.quest.QuestReq
import core.game.node.entity.skill.Skills
import core.game.node.item.GroundItemManager
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.game.node.scenery.SceneryBuilder
import core.game.system.config.ItemConfigParser
import core.game.system.config.ServerConfigParser
import core.game.system.task.Pulse
import core.game.system.timer.RSTimer
import core.game.system.timer.TimerRegistry
import core.game.world.GameWorld
import core.game.world.GameWorld.Pulser
import core.game.world.map.Direction
import core.game.world.map.Location
import core.game.world.map.RegionManager
import core.game.world.map.RegionManager.getRegionChunk
import core.game.world.map.path.Pathfinder
import core.game.world.map.zone.MapZone
import core.game.world.map.zone.ZoneBorders
import core.game.world.map.zone.ZoneBuilder
import core.game.world.repository.Repository
import core.game.world.update.flag.EntityFlag
import core.game.world.update.flag.chunk.AnimateSceneryUpdateFlag
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.ForceMoveCtx
import core.game.world.update.flag.context.Graphics
import core.net.packet.PacketRepository
import core.net.packet.context.DefaultContext
import core.net.packet.context.MusicContext
import core.net.packet.out.AudioPacket
import core.net.packet.out.MusicPacket
import core.tools.Log
import core.tools.SystemLogger
import core.tools.colorize
import core.tools.cyclesToTicks
import org.rs.consts.Items
import org.rs.consts.Sounds
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun hasLevelDyn(player: Player, skill: Int, level: Int): Boolean {
    return player.skills.getLevel(skill) >= level
}

fun hasLevelStat(player: Player, skill: Int, level: Int): Boolean {
    return player.skills.getStaticLevel(skill) >= level
}

fun amountInInventory(player: Player, id: Int): Int {
    return player.inventory.getAmount(id)
}

fun amountInBank(player: Player, id: Int, includeSecondary: Boolean = true): Int {
    return getAmountInBank(player, id) + if (includeSecondary) getAmountInBank(player, id, true) else 0
}

private fun getAmountInBank(player: Player, id: Int, secondary: Boolean = false): Int {
    val bank = if (secondary) player.bankSecondary.toArray() else player.bankPrimary.toArray()
    bank.forEach { if (it?.id == id) return it.amount }
    return 0
}

fun amountInEquipment(player: Player, id: Int): Int {
    val slot = itemDefinition(id).getConfiguration(ItemConfigParser.EQUIP_SLOT, -1)
    val equipped = player.equipment[slot] ?: return 0
    return if (equipped.id == id) equipped.amount else 0
}

fun inInventory(player: Player, id: Int, amount: Int = 1): Boolean {
    return player.inventory.contains(id, amount)
}

fun inBank(player: Player, id: Int, amount: Int = 1): Boolean {
    return amountInBank(player, id) >= amount
}

fun inEquipment(player: Player, id: Int, amount: Int = 1): Boolean {
    return amountInEquipment(player, id) >= amount
}

fun inEquipmentOrInventory(player: Player, id: Int, amount: Int = 1): Boolean {
    // Proper, but slower implementation. Use faster unless need to check amounts split between equip/inv
    // return amountInEquipment(player, id) + amountInInventory(player, id) >= amount
    return inEquipment(player, id, amount) || inInventory(player, id, amount)
}

fun allInEquipment(player: Player, vararg ids: Int): Boolean {
    return ids.all { id ->
        inEquipment(player, id)
    }
}

fun anyInEquipment(player: Player, vararg ids: Int): Boolean {
    return ids.any { id ->
        inEquipment(player, id)
    }
}

fun anyInInventory(player: Player, vararg ids: Int): Boolean {
    return ids.any { id ->
        inInventory(player, id)
    }
}

fun getItemFromEquipment(player: Player, slot: EquipmentSlot): Item? {
    return player.equipment.get(slot.ordinal)
}

class ContainerisedItem(val container: core.game.container.Container?, val itemId: Int) {
    fun remove(): Boolean {
        return this.container?.remove(this.itemId.asItem()) ?: false
    }

    fun exists(): Boolean {
        return this.container != null && this.itemId > -1
    }
}

fun hasAnItem(player: Player, vararg ids: Int): ContainerisedItem {
    for (searchSpace in arrayOf(player.inventory, player.equipment, player.bankPrimary, player.bankSecondary)) {
        for (id in ids) {
            if (searchSpace.containItems(id)) {
                return ContainerisedItem(searchSpace, id)
            }
        }
    }
    return ContainerisedItem(null, -1)
}

fun hasGodItem(player: Player, god: God): Boolean {
    god.validItems.forEach { if (inEquipment(player, it)) return true }
    return false
}

fun shouldRemoveNothings(player: Player): Boolean {
    val ring = getItemFromEquipment(player, EquipmentSlot.RING)
    return ring != null && ring.id == Items.RING_OF_WEALTH_2572
}

fun <T> removeItem(player: Player, item: T, container: Container = Container.INVENTORY): Boolean {
    item ?: return false
    val it = when (item) {
        is Item -> item
        is Int -> Item(item)
        else -> throw IllegalStateException("Invalid value passed for item")
    }

    return when (container) {
        Container.INVENTORY -> player.inventory.remove(it)
        Container.BANK -> player.bank.remove(it) || player.bankSecondary.remove(it)
        Container.EQUIPMENT -> player.equipment.remove(it)
    }
}

fun addItem(player: Player, id: Int, amount: Int = 1, container: Container = Container.INVENTORY): Boolean {
    val cont = when (container) {
        Container.INVENTORY -> player.inventory
        Container.BANK -> player.bank
        Container.EQUIPMENT -> player.equipment
    }

    return cont.add(Item(id, amount))
}

fun addItemOrBank(player: Player, id: Int, amount: Int = 1) {
    val item = Item(id, amount)
    if (!player.inventory.add(item)) {
        if (player.bankPrimary.add(item)) {
            sendMessage(player, colorize("%RThe ${item.name} has been sent to your bank."))
        } else if (player.bankSecondary.add(item)) {
            sendMessage(player, colorize("%RThe ${item.name} has been sent to your secondary bank."))
        } else {
            GroundItemManager.create(item, player)
            sendMessage(
                player,
                colorize("%RAs your inventory and bank account(s) are all full, the ${item.name} has been placed on the ground under your feet. Don't forget to grab it. (Also consider cleaning out some stuff, maybe? I mean, Jesus!)")
            )
        }
    }
}

fun replaceSlot(
    player: Player,
    slot: Int,
    item: Item,
    currentItem: Item? = null,
    container: Container = Container.INVENTORY
): Item? {
    val cont = when (container) {
        Container.INVENTORY -> player.inventory
        Container.EQUIPMENT -> player.equipment
        Container.BANK -> player.bank
    }

    if (item.id == 65535 || item.amount <= 0) {
        return cont.replace(null, slot)
    }

    if (currentItem == null) {
        return cont.replace(item, slot)
    }

    if (cont.remove(currentItem, slot, true)) {
        return cont.replace(item, slot)
    }

    PlayerMonitor.log(
        player,
        LogType.DUPE_ALERT,
        "Potential slot-replacement-based dupe attempt, slot: $slot, item: $item"
    )
    val other = when (container) {
        Container.INVENTORY -> Container.EQUIPMENT
        else -> Container.INVENTORY
    }
    if (removeItem(player, currentItem, other)) return cont.replace(item, slot)
    return null
}

fun addItemOrDrop(player: Player, id: Int, amount: Int = 1) {
    val item = Item(id, amount)
    if (amount == 1 || item.definition.isStackable()) {
        if (!player.inventory.add(item)) GroundItemManager.create(item, player)
    } else {
        val singleItem = Item(id, 1)
        for (i in 0 until amount) {
            if (!player.inventory.add(singleItem)) GroundItemManager.create(singleItem, player)
        }
    }
}

fun poofClear(npc: NPC) {
    submitWorldPulse(
        object : Pulse() {
            var counter = 0
            override fun pulse(): Boolean {
                when (counter++) {
                    2 -> {
                        npc.isInvisible = true; Graphics.send(
                            Graphics(86),
                            npc.location
                        )
                    }

                    3 -> npc.clear().also { return true }
                }
                return false
            }
        }
    )
}

fun freeSlots(player: Player): Int {
    return player.inventory.freeSlots()
}

fun getAnimation(id: Int): Animation {
    return Animation(id)
}

fun getAnimationWithPriority(id: Int, priority: Animator.Priority): Animation {
    return Animation(id, Animator.Priority.values()[priority.ordinal])
}

fun resetAnimator(player: Player) {
    player.animator.animate(Animation(-1, Animator.Priority.VERY_HIGH))
}

fun animationDuration(animation: Animation): Int {
    return animation.definition.durationTicks
}

fun animationCycles(animation: Int): Int {
    val def = AnimationDefinition.forId(animation)
    return def!!.cycles
}

fun rewardXP(player: Player, skill: Int, amount: Double) {
    player.skills.addExperience(skill, amount)
}

fun replaceScenery(toReplace: Scenery, with: Int, forTicks: Int, loc: Location? = null) {
    val newLoc = when (loc) {
        null -> toReplace.location
        else -> loc
    }
    if (forTicks == -1) {
        SceneryBuilder.replace(toReplace, toReplace.transform(with, toReplace.rotation, newLoc))
    } else {
        SceneryBuilder.replace(toReplace, toReplace.transform(with, toReplace.rotation, newLoc), forTicks)
    }
    toReplace.isActive = false
}

fun addScenery(scenery: Scenery) {
    SceneryBuilder.add(scenery)
}

fun addScenery(sceneryId: Int, location: Location, rotation: Int = 0, type: Int = 22): Scenery {
    val scenery = Scenery(sceneryId, location, type, rotation)
    SceneryBuilder.add(scenery)
    return scenery
}

fun removeScenery(scenery: Scenery) {
    SceneryBuilder.remove(scenery)
}

fun replaceScenery(toReplace: Scenery, with: Int, forTicks: Int, rotation: Direction, loc: Location? = null) {
    val newLoc = when (loc) {
        null -> toReplace.location
        else -> loc
    }
    val rot = when (rotation) {
        Direction.NORTH_WEST -> 0
        Direction.NORTH -> 1
        Direction.NORTH_EAST -> 2
        Direction.EAST -> 4
        Direction.SOUTH_EAST -> 7
        Direction.SOUTH -> 6
        Direction.SOUTH_WEST -> 5
        Direction.WEST -> 3
    }
    if (forTicks == -1) {
        SceneryBuilder.replace(toReplace, toReplace.transform(with, rot, newLoc))
    } else {
        SceneryBuilder.replace(toReplace, toReplace.transform(with, rot, newLoc), forTicks)
    }
    toReplace.isActive = false
}

fun getItemName(id: Int): String {
    return ItemDefinition.forId(id).name
}

fun hasSpaceFor(player: Player, item: Item): Boolean {
    return player.inventory.hasSpaceFor(item)
}

fun getWorldTicks(): Int {
    return GameWorld.ticks
}

fun getAudio(id: Int, volume: Int = 10, delay: Int = 1): Audio {
    return Audio(id, volume, delay)
}

fun playJingle(player: Player, jingleId: Int) {
    PacketRepository.send(MusicPacket::class.java, MusicContext(player, jingleId, true))
}

fun impact(entity: Entity, amount: Int, type: ImpactHandler.HitsplatType = ImpactHandler.HitsplatType.NORMAL) {
    entity.impactHandler.manualHit(entity, amount, type)
}

fun hasOption(node: Node, option: String): Boolean {
    return when (node) {
        is NPC -> node.definition.hasAction(option)
        is Scenery -> node.definition.hasAction(option)
        is Item -> node.definition.hasAction(option)
        else -> throw IllegalArgumentException("Expected an NPC, Scenery or an Item, got ${node.javaClass.simpleName}.")
    }
}

fun animateScenery(player: Player, obj: Scenery, animationId: Int, global: Boolean = false) {
    player.packetDispatch.sendSceneryAnimation(obj, getAnimation(animationId), global)
}

fun animateScenery(obj: Scenery, animationId: Int) {
    val animation = Animation(animationId)
    animation.setObject(obj)
    getRegionChunk(obj.location).flag(
        AnimateSceneryUpdateFlag(
            animation
        )
    )
}

fun spawnProjectile(source: Entity, dest: Entity, projectileId: Int) {
    Projectile.create(source, dest, projectileId).send()
}

fun spawnProjectile(
    source: Location,
    dest: Location,
    projectile: Int,
    startHeight: Int,
    endHeight: Int,
    delay: Int,
    speed: Int,
    angle: Int
) {
    Projectile.create(
        source, dest, projectile, startHeight, endHeight, delay, speed, angle, source.getDistance(dest).toInt()
    ).send()
}

fun face(entity: Entity, toFace: Node, duration: Int = -1) {
    if (duration == -1) {
        when (toFace) {
            is Location -> entity.faceLocation(toFace)
            is Entity -> entity.face(toFace)
        }
    } else {
        when (toFace) {
            is Location -> entity.faceTemporary(toFace.asNpc(), duration)
            else -> entity.faceTemporary(toFace as Entity, duration)
        }
    }
}

fun resetFace(entity: Entity) {
    entity.face(null)
    entity.faceLocation(entity.location)
}

fun faceLocation(entity: Entity, location: Location) {
    entity.faceLocation(location)
}

fun openInterface(player: Player, id: Int) {
    player.interfaceManager.open(Component(id))
}

fun openOverlay(player: Player, id: Int) {
    player.interfaceManager.openOverlay(Component(id))
}

fun openChatbox(player: Player, id: Int) {
    player.interfaceManager.openChatbox(Component(id))
}

fun closeOverlay(player: Player) {
    player.interfaceManager.closeOverlay()
}

fun closeAllInterfaces(player: Player) {
    player.interfaceManager.close()
    player.interfaceManager.closeChatbox()
    player.dialogueInterpreter.close()
}

fun unlockEmote(player: Player, emoteId: Int) {
    player.emoteManager.unlock(Emotes.forId(emoteId))
}

fun sendMessage(player: Player, message: String) {
    player.sendMessages(*splitLines(message, 86))
}

fun sendTutorialMessage(player: Player, message: String) {
    player.dialogueInterpreter.sendBoldInput(message)
}

fun sendMessages(player: Player, vararg message: String) {
    player.packetDispatch.sendMessages(*message)
}

fun sendMessageWithDelay(player: Player, message: String, ticks: Int) {
    player.sendMessage(message, ticks)
}

fun sendChat(entity: Entity, message: String, delay: Int = -1) {
    if (delay > -1) {
        queueScript(entity, delay, QueueStrength.SOFT) {
            entity.sendChat(message)
            return@queueScript stopExecuting(entity)
        }
    } else entity.sendChat(message)
}

fun sendDialogue(player: Player, message: String) {
    player.dialogueInterpreter.sendDialogue(*splitLines(message))
}

fun sendTutorialNPCDialogue(player : Player, npc: Int, vararg message: String) {
    Component.setUnclosable(player,
        player.dialogueInterpreter.sendDialogues(npc, FaceAnim.FRIENDLY, *message)
    )
}

fun sendTutorialNPCDialogue(player : Player, npc: Int, expr: FaceAnim = FaceAnim.FRIENDLY, vararg message: String) {
    Component.setUnclosable(player,
        player.dialogueInterpreter.sendDialogues(npc, expr, *message)
    )
}

fun sendDestroyItemDialogue(player: Player, item: Int, message: String?) {
    player.dialogueInterpreter.sendDestroyItem(item, message)
}

fun sendDialogueLines(player: Player, vararg message: String) {
    player.dialogueInterpreter.sendDialogue(*message)
}

fun sendDialogueOptions(player: Player, title: String, vararg options: String) {
    player.dialogueInterpreter.sendOptions(title, *options)
}

fun sendPlainDialogue(player: Player, hideContinue: Boolean = false, vararg message: String) {
    player.dialogueInterpreter.sendPlainMessage(hideContinue, *message)
}

fun sendUnclosablePlainDialogue(player: Player, hideContinue: Boolean = false, vararg message: String) {
    Component.setUnclosable(player, player.dialogueInterpreter.sendPlainMessage(hideContinue, *message))
}

fun sendUnclosableDialogue(player: Player, vararg message: String) {
    Component.setUnclosable(player, player.dialogueInterpreter.sendDialogues(*message))
}

fun <T> animate(entity: Entity, anim: T, forced: Boolean = false) {
    val animation = when (anim) {
        is Int -> Animation(anim)
        is Animation -> anim
        else -> throw IllegalStateException("Invalid value passed for anim")
    }

    if (forced) {
        entity.animator.forceAnimation(animation)
    } else {
        entity.animator.animate(animation)
    }
}

fun sendAnimation(player: Player, animation: Int, delay: Int) {
    player.packetDispatch.sendAnimation(animation, delay)
}

@JvmOverloads
fun playAudio(
    player: Player,
    id: Int,
    delay: Int = 0,
    loops: Int = 1,
    location: Location? = null,
    radius: Int = Audio.defaultAudioRadius
) {
    PacketRepository.send(
        AudioPacket::class.java,
        DefaultContext(player, Audio(id, delay, loops, radius), location)
    )
}

@JvmOverloads
fun playGlobalAudio(
    location: Location,
    id: Int,
    delay: Int = 0,
    loops: Int = 1,
    radius: Int = Audio.defaultAudioRadius
) {
    val nearbyPlayers = RegionManager.getLocalPlayers(location, radius)
    for (player in nearbyPlayers) {
        PacketRepository.send(
            AudioPacket::class.java,
            DefaultContext(player, Audio(id, delay, loops, radius), location)
        )
    }
}

fun playHurtAudio(player: Player, delay: Int = 0) {
    val maleHurtAudio =
        intArrayOf(Sounds.HUMAN_HIT4_516, Sounds.HUMAN_HIT5_517, Sounds.HUMAN_HIT_518, Sounds.HUMAN_HIT_6_522)
    val femaleHurtAudio =
        intArrayOf(Sounds.FEMALE_HIT_506, Sounds.FEMALE_HIT_507, Sounds.FEMALE_HIT2_508, Sounds.FEMALE_HIT_2_510)
    if (player.isMale) {
        playAudio(player, maleHurtAudio.random(), delay)
    } else {
        playAudio(player, femaleHurtAudio.random(), delay)
    }
}

fun openDialogue(player: Player, dialogue: Any, vararg args: Any) {
    player.dialogueInterpreter.close()
    when (dialogue) {
        is Int -> player.dialogueInterpreter.open(dialogue, *args)
        is String -> player.dialogueInterpreter.open(getDialogueKey(dialogue), *args)
        is DialogueFile -> player.dialogueInterpreter.open(dialogue, *args)
        is SkillDialogueHandler -> dialogue.open()
        else -> log(
            ContentAPI::class.java,
            Log.ERR,
            "Invalid object type passed to openDialogue() -> ${dialogue.javaClass.simpleName}"
        )
    }
}

fun findNPC(id: Int): NPC? {
    return Repository.findNPC(id)
}

fun getScenery(x: Int, y: Int, z: Int): Scenery? {
    return RegionManager.getObject(z, x, y)
}

fun getScenery(loc: Location): Scenery? {
    return RegionManager.getObject(loc)
}

fun findNPC(refLoc: Location, id: Int): NPC? {
    return Repository.npcs.firstOrNull { it.id == id && it.location.withinDistance(refLoc) }
}

fun findLocalNPC(entity: Entity, id: Int): NPC? {
    return RegionManager.getLocalNpcs(entity).firstOrNull { it.id == id }
}

fun findLocalNPCs(location: Location, distance: Int): MutableList<NPC> {
    return RegionManager.getLocalNpcs(location, distance)
}

fun findLocalNPCs(entity: Entity, ids: IntArray): List<NPC> {
    return RegionManager.getLocalNpcs(entity).filter { it.id in ids }.toList()
}

fun findLocalNPCs(entity: Entity, ids: IntArray, distance: Int): List<NPC> {
    return RegionManager.getLocalNpcs(entity, distance).filter { it.id in ids }.toList()
}

fun getRegionBorders(regionId: Int): ZoneBorders {
    return ZoneBorders.forRegion(regionId)
}

fun <T> getAttribute(entity: Entity, attribute: String, default: T): T {
    return entity.getAttribute(attribute, default)
}

fun <T> setAttribute(entity: Entity, attribute: String, value: T) {
    entity.setAttribute(attribute, value)
}

fun removeAttribute(entity: Entity, attribute: String) {
    entity.removeAttribute(attribute.replace("/save:", ""))
}

fun removeAttributes(entity: Entity, vararg attributes: String) {
    for (attribute in attributes) removeAttribute(entity, attribute)
}

fun registerTimer(entity: Entity, timer: RSTimer?) {
    if (timer == null) return
    entity.timers.registerTimer(timer)
}

inline fun <reified T : RSTimer> getOrStartTimer(entity: Entity, vararg args: Any): T {
    val existing = getTimer<T>(entity)
    if (existing != null) return existing
    return spawnTimer<T>(*args).also { registerTimer(entity, it) }
}

fun spawnTimer(identifier: String, vararg args: Any): RSTimer? {
    return TimerRegistry.getTimerInstance(identifier, *args)
}

inline fun <reified T : RSTimer> spawnTimer(vararg args: Any): T {
    return TimerRegistry.getTimerInstance<T>(*args)!!
}

inline fun <reified T : RSTimer> hasTimerActive(entity: Entity): Boolean {
    return getTimer<T>(entity) != null
}

inline fun <reified T : RSTimer> getTimer(entity: Entity): T? {
    return entity.timers.getTimer<T>()
}

inline fun <reified T : RSTimer> removeTimer(entity: Entity) {
    entity.timers.removeTimer<T>()
}

fun hasTimerActive(entity: Entity, identifier: String): Boolean {
    return getTimer(entity, identifier) != null
}

fun getTimer(entity: Entity, identifier: String): RSTimer? {
    return entity.timers.getTimer(identifier)
}

fun removeTimer(entity: Entity, identifier: String) {
    entity.timers.removeTimer(identifier)
}

fun removeTimer(entity: Entity, timer: RSTimer) {
    entity.timers.removeTimer(timer)
}

fun lock(entity: Entity, duration: Int) {
    entity.lock(duration)
}

fun lockInteractions(entity: Entity, duration: Int) {
    entity.locks.lockInteractions(duration)
}

fun lockMovement(entity: Entity, duration: Int) {
    entity.locks.lockMovement(duration)
}

fun lockEquipment(entity: Entity, duration: Int) {
    entity.locks.equipmentLock.lock(duration)
}

fun lockTeleport(entity: Entity) {
    entity.locks.isTeleportLocked
}

fun lockTeleport(entity: Entity, ticks: Int) {
    entity.locks.lockTeleport(ticks)
}

fun unlock(entity: Entity) {
    entity.unlock()
}

fun location(x: Int, y: Int, z: Int): Location {
    return Location.create(x, y, z)
}

fun inBorders(entity: Entity, borders: ZoneBorders): Boolean {
    return borders.insideBorder(entity)
}

fun inBorders(entity: Entity, swX: Int, swY: Int, neX: Int, neY: Int): Boolean {
    return ZoneBorders(swX, swY, neX, neY).insideBorder(entity)
}

fun inZone(entity: Entity, name: String): Boolean {
    return entity.zoneMonitor.isInZone(name)
}

fun heal(entity: Entity, amount: Int) {
    entity.skills.heal(amount)
}

fun getVarp(player: Player, varpIndex: Int): Int {
    return player.varpMap[varpIndex] ?: 0
}

fun getVarbit(player: Player, def: VarbitDefinition): Int {
    val mask = def.getMask()
    val current = getVarp(player, def.varpId)
    return (current shr def.startBit) and mask
}

fun getVarbit(player: Player, varbitId: Int): Int {
    val def = VarbitDefinition.forId(varbitId)
    return getVarbit(player, def)
}

@JvmOverloads
fun setVarp(player: Player, varpIndex: Int, value: Int, save: Boolean = false) {
    player.varpMap[varpIndex] = value
    player.saveVarp[varpIndex] = save
    player.packetDispatch.sendVarp(varpIndex, value)
}

fun saveVarp(player: Player, varpIndex: Int) {
    player.saveVarp[varpIndex] = true
}

fun unsaveVarp(player: Player, varpIndex: Int) {
    player.saveVarp.remove(varpIndex)
}

@JvmOverloads
fun setVarbit(player: Player, def: VarbitDefinition, value: Int, save: Boolean = false) {
    val mask = def.getMask()
    val current = getVarp(player, def.varpId) and (mask shl def.startBit).inv()
    val newValue = (value and mask) shl def.startBit
    setVarp(player, def.varpId, current or newValue, save)
}

@JvmOverloads
fun setVarbit(player: Player, varbitId: Int, value: Int, save: Boolean = false) {
    val def = VarbitDefinition.forId(varbitId)

    if (def == null) {
        logWithStack(ContentAPI::class.java, Log.ERR, "Trying to setVarbit $varbitId, which doesn't seem to exist.")
        return
    }

    setVarbit(player, def, value, save)
}

fun setVarc(player: Player, varc: Int, value: Int) {
    player.packetDispatch.sendVarcUpdate(varc.toShort(), value)
}

fun reinitVarps(player: Player) {
    for ((index, value) in player.varpMap) {
        setVarp(player, index, value, player.saveVarp[index] ?: false)
    }
}

fun forceWalk(entity: Entity, dest: Location, type: String) {
    if (type == "clip") {
        ForceMovement(entity, dest, 10, 10).run()
        return
    }
    val pathfinder = when (type) {
        "smart" -> Pathfinder.SMART
        else -> Pathfinder.DUMB
    }
    val path = Pathfinder.find(entity, dest, true, pathfinder)
    path.walk(entity)
}

fun forceMove(
    player: Player,
    start: Location,
    dest: Location,
    startArrive: Int,
    destArrive: Int,
    dir: Direction? = null,
    anim: Int = 819,
    callback: (() -> Unit)? = null
) {
    var direction: Direction

    if (dir == null) {
        var delta = Location.getDelta(start, dest)
        var x = abs(delta.x)
        var y = abs(delta.y)

        if (x > y) direction = Direction.getDirection(delta.x, 0)
        else direction = Direction.getDirection(0, delta.y)
    } else direction = dir

    val startLoc = Location.create(start)
    val destLoc = Location.create(dest)
    var startArriveTick = getWorldTicks() + cyclesToTicks(startArrive) + 1
    var destArriveTick = startArriveTick + cyclesToTicks(destArrive)
    var maskSet = false

    delayEntity(player, (destArriveTick - getWorldTicks()) + 1)
    queueScript(player, 0, QueueStrength.SOFT) {
        if (!finishedMoving(player)) return@queueScript keepRunning(player)
        if (!maskSet) {
            var ctx = ForceMoveCtx(startLoc, destLoc, startArrive, destArrive, direction)
            player.updateMasks.register(EntityFlag.ForceMove, ctx)
            maskSet = true
        }

        var tick = getWorldTicks()
        if (tick < startArriveTick) {
            return@queueScript keepRunning(player)
        } else if (tick < destArriveTick) {
            if (animationFinished(player)) animate(player, anim)
            return@queueScript keepRunning(player)
        } else if (tick >= destArriveTick) {
            try {
                callback?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            player.properties.teleportLocation = dest
            return@queueScript stopExecuting(player)
        }
        return@queueScript stopExecuting(player)
    }
}

fun stopWalk(entity: Entity) {
    entity.walkingQueue.reset()
}

fun getChildren(scenery: Int): IntArray {
    return SceneryDefinition.forId(scenery).configObjectIds!!.filter { it != -1 }.toIntArray()
}

fun adjustCharge(node: Node, amount: Int) {
    when (node) {
        is Item -> node.charge += amount
        is Scenery -> node.charge += amount
        else -> log(
            ContentAPI::class.java,
            Log.ERR,
            "Attempt to adjust the charge of invalid type: ${node.javaClass.simpleName}"
        )
    }
}

fun getCharge(node: Node): Int {
    when (node) {
        is Item -> return node.charge
        is Scenery -> return node.charge
        else -> log(ContentAPI::class.java, Log.ERR, "Attempt to get charge of invalid type: ${node.javaClass.simpleName}").also { return -1 }
    }
}

fun setCharge(node: Node, charge: Int) {
    when (node) {
        is Item -> node.charge = charge
        is Scenery -> node.charge = charge
        else -> log(ContentAPI::class.java, Log.ERR, "Attempt to set the charge of invalid type: ${node.javaClass.simpleName}")
    }
}

fun getUsedOption(player: Player): String {
    return player.getAttribute("interact:option", "INVALID")
}

fun <A, G> visualize(entity: Entity, anim: A, gfx: G) {
    val animation = when (anim) {
        is Int -> Animation(anim)
        is Animation -> anim
        else -> throw IllegalStateException("Invalid parameter passed for animation.")
    }

    val graphics = when (gfx) {
        is Int -> Graphics(gfx)
        is Graphics -> gfx
        else -> throw IllegalStateException("Invalid parameter passed for graphics.")
    }

    entity.visualize(animation, graphics)
}

fun submitWorldPulse(pulse: Pulse) {
    Pulser.submit(pulse)
}

fun runWorldTask(task: () -> Unit): Pulse {
    val pulse = object : Pulse() {
        override fun pulse(): Boolean {
            task.invoke()
            return true
        }
    }

    submitWorldPulse(pulse)
    return pulse
}

fun teleport(
    entity: Entity,
    loc: Location,
    type: TeleportManager.TeleportType = TeleportManager.TeleportType.INSTANT
): Boolean {
    if (type == TeleportManager.TeleportType.INSTANT) {
        entity.properties.teleportLocation = loc
        return true
    } else return entity.teleporter.send(loc, type)
}

fun setTempLevel(entity: Entity, skill: Int, level: Int) {
    entity.skills.setLevel(skill, level)
}

fun getStatLevel(entity: Entity, skill: Int): Int {
    return entity.skills.getStaticLevel(skill)
}

fun getDynLevel(entity: Entity, skill: Int): Int {
    return entity.skills.getLevel(skill)
}

fun adjustLevel(entity: Entity, skill: Int, amount: Int) {
    entity.skills.setLevel(skill, entity.skills.getStaticLevel(skill) + amount)
}

fun <T> removeAll(player: Player, item: T, container: Container = Container.INVENTORY): Boolean {
    item ?: return false
    val it = when (item) {
        is Item -> item.id
        is Int -> item
        else -> throw IllegalStateException("Invalid value passed as item")
    }

    return when (container) {
        Container.EQUIPMENT -> player.equipment.remove(Item(it, amountInEquipment(player, it)))
        Container.BANK -> {
            val amountInPrimary = amountInBank(player, it, false)
            val amountInSecondary = amountInBank(player, it, true) - amountInPrimary
            player.bank.remove(Item(it, amountInPrimary)) && player.bankSecondary.remove(Item(it, amountInSecondary))
        }

        Container.INVENTORY -> player.inventory.remove(Item(it, amountInInventory(player, it)))
    }
}

fun sendString(player: Player, string: String, iface: Int, child: Int) {
    player.packetDispatch.sendString(string, iface, child)
}

fun closeInterface(player: Player) {
    player.interfaceManager.close()
}

fun closeTabInterface(player: Player) {
    player.interfaceManager.closeSingleTab()
}

fun closeChatBox(player: Player) {
    player.interfaceManager.closeChatbox()
}

fun setComponentVisibility(player: Player, iface: Int, child: Int, hide: Boolean) {
    player.packetDispatch.sendInterfaceConfig(iface, child, hide)
}

fun setTitle(player: Player, options: Int) {
    setComponentVisibility(
        player,
        if (options == 5) 234 else if (options == 4) 232 else if (options == 3) 230 else 228,
        (4 + options),
        true
    )
    setComponentVisibility(
        player,
        if (options == 5) 234 else if (options == 4) 232 else if (options == 3) 230 else 228,
        if (options == 2 || options == 4) 9 else 10,
        false
    )
    if (options > 5)
        throw java.lang.IllegalArgumentException("Expected option value between 2 and 5, got ${options::class.java.simpleName}.")
}

fun sendPlayerDialogue(player: Player, msg: String, expr: FaceAnim = FaceAnim.FRIENDLY) {
    player.dialogueInterpreter.sendDialogues(player, expr, *splitLines(msg))
}

fun sendPlayerOnInterface(player: Player, iface: Int, child: Int) {
    player.packetDispatch.sendPlayerOnInterface(iface, child)
}

fun sendNPCDialogue(player: Player, npc: Int, msg: String, expr: FaceAnim = FaceAnim.FRIENDLY) {
    player.dialogueInterpreter.sendDialogues(npc, expr, *splitLines(msg))
}

fun sendNPCDialogueWithDelay(
    player: Player,
    tick: Int,
    npc: Int,
    msg: String,
    expr: FaceAnim = FaceAnim.FRIENDLY
) {
    player.dialogueInterpreter.sendDialogues(tick, npc, expr, *splitLines(msg))
}

fun sendNPCDialogueLines(player: Player, npc: Int, expr: FaceAnim, hideContinue: Boolean, vararg msgs: String) {
    val dialogueComponent = player.dialogueInterpreter.sendDialogues(npc, expr, *msgs)
    player.packetDispatch.sendInterfaceConfig(dialogueComponent.id, msgs.size + 4, hideContinue)
}

fun sendAnimationOnInterface(player: Player, anim: Int, iface: Int, child: Int) {
    player.packetDispatch.sendAnimationInterface(anim, iface, child)
}

fun registerLogoutListener(player: Player, key: String, handler: (p: Player) -> Unit) {
    player.logoutListeners[key] = handler
}

fun clearLogoutListener(player: Player, key: String) {
    player.logoutListeners.remove(key)
}

fun clearInventory(player: Player) {
    player.inventory.clear()
}

fun clearEquipment(player: Player) {
    player.equipment.clear()
}

fun clearInventoryAndEquipment(player: Player) {
    player.equipment.clear()
    player.inventory.clear()
}

fun sendItemOnInterface(player: Player, iface: Int, child: Int, item: Int, amount: Int = 1) {
    player.packetDispatch.sendItemOnInterface(item, amount, iface, child)
}

fun sendModelOnInterface(player: Player, iface: Int, child: Int, model: Int, zoom: Int = 1) {
    player.packetDispatch.sendModelOnInterface(model, iface, child, zoom)
}

fun sendAngleOnInterface(player: Player, iface: Int, child: Int, zoom: Int, pitch: Int, yaw: Int) {
    player.packetDispatch.sendAngleOnInterface(iface, child, zoom, pitch, yaw)
}

fun sendItemDialogue(player: Player, item: Any, message: String) {
    val dialogueItem = when (item) {
        is Item -> item
        is Int -> Item(item)
        else -> {
            throw java.lang.IllegalArgumentException("Expected an Item or an Int, got ${item::class.java.simpleName}.")
        }
    }
    player.dialogueInterpreter.sendItemMessage(dialogueItem, *splitLines(message))
}

fun sendDoubleItemDialogue(player: Player, item1: Any, item2: Any, message: String) {
    when (item1) {
        is Item -> player.dialogueInterpreter.sendDoubleItemMessage(item1, item2 as Item, message)
        is Int -> player.dialogueInterpreter.sendDoubleItemMessage(item1, item2 as Int, message)
    }
}

fun sendInputDialogue(player: Player, numeric: Boolean, prompt: String, handler: (value: Any) -> Unit) {
    if (numeric) sendInputDialogue(player, InputType.NUMERIC, prompt, handler)
    else sendInputDialogue(player, InputType.STRING_SHORT, prompt, handler)
}

fun sendInputDialogue(player: Player, type: InputType, prompt: String, handler: (value: Any) -> Unit) {
    when (type) {
        InputType.AMOUNT -> {
            setAttribute(player, "parseamount", true)
            player.dialogueInterpreter.sendInput(true, prompt)
        }

        InputType.NUMERIC, InputType.STRING_SHORT -> player.dialogueInterpreter.sendInput(
            type != InputType.NUMERIC,
            prompt
        )

        InputType.STRING_LONG -> player.dialogueInterpreter.sendLongInput(prompt)
        InputType.MESSAGE -> player.dialogueInterpreter.sendMessageInput(prompt)
    }

    setAttribute(player, "runscript", handler)
    setAttribute(player, "input-type", type)
}

fun flee(entity: Entity, from: Entity) {
    lock(entity, 5)
    face(entity, from, 5)

    val diffX = entity.location.x - from.location.x
    val diffY = entity.location.y - from.location.y

    forceWalk(entity, entity.location.transform(diffX, diffY, 0), "DUMB")
}

fun submitIndividualPulse(entity: Entity, pulse: Pulse) {
    entity.pulseManager.run(pulse)
}

fun runTask(entity: Entity, delay: Int = 0, repeatTimes: Int = 1, task: () -> Unit) {
    var cycles = repeatTimes
    entity.pulseManager.run(
        object : Pulse(delay) {
            override fun pulse(): Boolean {
                task.invoke()
                return --cycles <= 0
            }
        }
    )
}

fun sendItemZoomOnInterface(player: Player, iface: Int, child: Int, item: Int, zoom: Int = 230) {
    player.packetDispatch.sendItemZoomOnInterface(item, zoom, iface, child)
}

fun sceneryDefinition(id: Int): SceneryDefinition {
    return SceneryDefinition.forId(id)
}

fun registerMapZone(zone: MapZone, borders: ZoneBorders) {
    ZoneBuilder.configure(zone)
    zone.register(borders)
}

fun animateInterface(player: Player, iface: Int, child: Int, anim: Int) {
    player.packetDispatch.sendAnimationInterface(anim, iface, child)
}

fun addClimbDest(ladderLoc: Location, dest: Location) {
    core.game.global.action.SpecialLadder.add(ladderLoc, dest)
}

fun sendNews(message: String) {
    Repository.sendNews(message, 12, "CC6600")
}

fun <G> sendGraphics(gfx: G, location: Location) {
    when (gfx) {
        is Int -> Graphics.send(
            Graphics(
                gfx
            ),
            location
        )

        is Graphics -> Graphics.send(gfx, location)
    }
}

fun runcs2(player: Player, scriptId: Int, vararg arguments: Any) {
    var typeString = StringBuilder()
    var finalArgs = Array<Any?>(arguments.size) { null }
    try {
        for (i in 0 until arguments.size) {
            val arg = arguments[i]
            if (arg is Int) typeString.append("i")
            else if (arg is String) typeString.append("s")
            else throw IllegalArgumentException("Argument at index $i ($arg) is not an acceptable type! Only string and int are accepted.")
            finalArgs[arguments.size - i - 1] = arg
        }
        player.packetDispatch.sendRunScript(scriptId, typeString.toString(), *finalArgs)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@JvmOverloads
fun sendItemSelect(
    player: Player,
    vararg options: String,
    keepAlive: Boolean = false,
    callback: (slot: Int, optionIndex: Int) -> Unit
) {
    player.interfaceManager.openSingleTab(Component(12))
    val scriptArgs = arrayOf((12 shl 16) + 18, 93, 4, 7, 0, -1, "", "", "", "", "", "", "", "", "")
    for (i in 0 until min(9, options.size)) scriptArgs[6 + i] = options[i]
    runcs2(player, 150, *scriptArgs)
    val settings = IfaceSettingsBuilder().enableOptions(0 until 9).build()
    player.packetDispatch.sendIfaceSettings(settings, 18, 12, 0, 28)
    setAttribute(player, "itemselect-callback", callback)
    setAttribute(player, "itemselect-keepalive", keepAlive)
}

fun announceIfRare(player: Player, item: Item) {
    if (item.definition.getConfiguration(ItemConfigParser.RARE_ITEM, false)) {
        // sendNews("${player.username} has just received: ${item.amount} x ${item.name}.");
        GlobalKillCounter.incrementRareDrop(player, item)
    }
}

fun hasRequirement(player: Player, req: QuestReq, message: Boolean = true): Boolean {
    var (isMet, unmetReqs) = req.evaluate(player)
    val messageList = ArrayList<String>()

    var totalSoftQp = 0
    var totalHardQp = 0

    for (req in unmetReqs) {
        when (req) {
            is QPCumulative -> totalSoftQp += req.amount
            is QPReq -> if (req.amount > totalHardQp) totalHardQp = req.amount
        }
    }

    var neededQp = min(max(totalSoftQp, totalHardQp), player.questRepository.availablePoints)

    isMet = isMet && neededQp <= player.questRepository.points

    if (isMet) return true

    if (unmetReqs.size == 2 && unmetReqs[0] is QuestReq) {
        messageList.add("You must complete ${(unmetReqs[0] as QuestReq).questReq.questName} to use this.")
    } else {
        messageList.add("You need the pre-reqs for ${req.questReq.questName} to access this.")
        messageList.add("Please check the quest journal for more information.")
    }

    if (message) for (message in messageList) sendMessage(player, message)

    return false
}

fun getMasteredSkillNames(player: Player): List<String> {
    val hasMastered = player.getSkills().masteredSkills > 0
    val masteredSkills = ArrayList<String>()

    if (hasMastered) {
        for ((skillId, skillName) in Skills.SKILL_NAME.withIndex()) {
            if (hasLevelStat(player, skillId, 99)) {
                masteredSkills.add(skillName)
            }
        }
    }
    return masteredSkills
}

fun dumpContainer(player: Player, container: core.game.container.Container): Int {
    val bank = player.bank
    var dumpedCount = 0

    run beginDepositing@{
        container.toArray().filterNotNull().forEach { item ->
            if (!bank.hasSpaceFor(item)) {
                val message =
                    if (item.id == Items.COINS_995) "You have ran out of Bank Space. Please make more room." else "You have no more space in your bank."
                sendMessage(player, message)
                return@beginDepositing
            }

            if (!bank.canAdd(item)) {
                sendMessage(player, "A magical force prevents you from banking the ${item.name}.")
                return@forEach
            } else {
                if (container is EquipmentContainer) {
                    if (!InteractionListeners.run(item.id, player, item, false)) {
                        sendMessage(player, "A magical force prevents you from removing your ${item.name}.")
                        return@forEach
                    }
                }

                container.remove(item)
                bank.add(unnote(item), true)
                dumpedCount++
            }
        }
    }

    container.update()
    bank.update()

    return dumpedCount
}

fun dumpBeastOfBurden(player: Player) {
    val famMan = player.familiarManager

    if (!famMan.hasFamiliar()) {
        sendMessage(player, "You don't have a familiar.")
        return
    }

    if (famMan.familiar !is content.global.skill.summoning.familiar.BurdenBeast) {
        sendMessage(player, "Your familiar is not a Beast of Burden.")
        return
    }

    val beast: content.global.skill.summoning.familiar.BurdenBeast =
        (famMan.familiar as content.global.skill.summoning.familiar.BurdenBeast)

    if (beast.container.isEmpty) {
        sendMessage(player, "Your familiar's inventory is empty.")
        return
    }

    val itemCount = beast.container.itemCount()
    val dumpedCount = dumpContainer(player, beast.container)

    when {
        dumpedCount == itemCount -> sendMessage(player, "Your familiar's inventory was deposited into your bank.")
        dumpedCount > 0 -> {
            val remainPhrase = when {
                (itemCount - dumpedCount == 1) -> "item remains"
                else -> "items remain"
            }

            sendMessage(player, "${itemCount - dumpedCount} $remainPhrase in your familiar's inventory.")
        }
    }
}

fun getFamiliarBoost(player: Player, skill: Int): Int {
    return player.familiarManager.getBoost(skill)
}

fun note(item: Item): Item {
    if (!item.definition.isUnnoted) return item

    if (item.definition.noteId < 0) return item

    return Item(item.definition.noteId, item.amount, item.charge)
}

fun unnote(item: Item): Item {
    if (item.definition.isUnnoted) return item

    return Item(item.noteChange, item.amount, item.charge)
}

fun hasSealOfPassage(player: Player): Boolean {
    return inEquipmentOrInventory(player, Items.SEAL_OF_PASSAGE_9083)
}

fun hasHouse(player: Player): Boolean {
    return player.houseManager.hasHouse()
}

fun Player.getCutscene(): Cutscene? {
    return getAttribute<Cutscene?>(this, Cutscene.ATTRIBUTE_CUTSCENE, null)
}

fun Player.getCutsceneStage(): Int {
    return getAttribute(this, Cutscene.ATTRIBUTE_CUTSCENE_STAGE, 0)
}

fun getServerConfig(): Toml {
    return ServerConfigParser.tomlData ?: Toml()
}

fun getPathableRandomLocalCoordinate(target: Entity, radius: Int, center: Location, maxAttempts: Int = 3): Location {
    var maxRadius = Vector.deriveWithEqualComponents(ServerConfig.MAX_PATHFIND_DISTANCE.toDouble()).x - 1
    var effectiveRadius = min(radius, maxRadius.toInt())
    val swCorner = center.transform(-effectiveRadius, -effectiveRadius, center.z)
    val neCorner = center.transform(effectiveRadius, effectiveRadius, center.z)
    val borders = ZoneBorders(swCorner.x, swCorner.y, neCorner.x, neCorner.y, center.z)

    var attempts = maxAttempts
    var success: Boolean
    while (attempts-- > 0) {
        val dest = borders.randomLoc
        val path = Pathfinder.find(center, dest, target.size())
        success = path.isSuccessful && !path.isMoveNear
        if (success) return dest
    }

    return target.location
}

fun getPathableCardinal(target: Entity, center: Location): Location {
    var tiles = center.cardinalTiles

    for (tile in tiles) {
        val path = Pathfinder.find(center, tile, target.size())
        if (path.isSuccessful && !path.isMoveNear) return tile
    }

    return center
}

fun hasIronmanRestriction(player: Player, restriction: IronmanMode): Boolean {
    return player.ironmanManager.isIronman && player.ironmanManager.mode.ordinal >= restriction.ordinal
}

fun registerHintIcon(player: Player, location: Location, height: Int) {
    setAttribute(
        player,
        "hinticon",
        HintIconManager.registerHintIcon(player, location, 1, -1, player.hintIconManager.freeSlot(), height, 3)
    )
}

fun registerHintIcon(player: Player, node: Node) {
    if (getAttribute(player, "hinticon", null) != null) return
    setAttribute(player, "hinticon", HintIconManager.registerHintIcon(player, node))
}

fun clearHintIcon(player: Player) {
    val slot = getAttribute(player, "hinticon", -1)
    removeAttribute(player, "hinticon")
    HintIconManager.removeHintIcon(player, slot)
}

fun hasHandsFree(player: Player): Boolean {
    val equipment = player.equipment
    return equipment[EquipmentContainer.SLOT_HANDS] == null &&
            equipment[EquipmentContainer.SLOT_SHIELD] == null &&
            equipment[EquipmentContainer.SLOT_WEAPON] == null
}

fun equipSlot(item: Int): EquipmentSlot? {
    return EquipmentSlot.values().getOrNull(itemDefinition(item).getConfiguration(ItemConfigParser.EQUIP_SLOT, -1))
}

fun isPlayer(node: Node): Boolean {
    return (node is Player)
}

fun addDialogueAction(player: Player, action: DialogueAction) {
    player.dialogueInterpreter.addAction(action)
}

fun log(origin: Class<*>, type: Log, message: String) {
    SystemLogger.processLogEntry(origin, type, message)
}

fun logWithStack(origin: Class<*>, type: Log, message: String) {
    try {
        throw Exception(message)
    } catch (e: Exception) {
        log(origin, type, "${exceptionToString(e)}")
    }
}

fun exceptionToString(e: Exception): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    e.printStackTrace(pw)
    return sw.toString()
}

fun delayScript(entity: Entity, ticks: Int): Boolean {
    entity.scripts.getActiveScript()?.let { it.nextExecution = GameWorld.ticks + ticks }
    return false
}

fun delayEntity(entity: Entity, ticks: Int) {
    entity.scripts.delay = GameWorld.ticks + ticks
    lock(entity, ticks)
}

fun apRange(entity: Entity, apRange: Int) {
    entity.scripts.apRange = apRange
    entity.scripts.apRangeCalled = true
}

fun hasLineOfSight(entity: Entity, target: Node): Boolean {
    return CombatSwingHandler.isProjectileClipped(entity, target, false)
}

fun animationFinished(entity: Entity): Boolean {
    return entity.clocks[Clocks.ANIMATION_END] < GameWorld.ticks
}

fun clearScripts(entity: Entity): Boolean {
    entity.scripts.reset()
    return true
}

fun restartScript(entity: Entity): Boolean {
    if (entity.scripts.getActiveScript()?.persist != true) {
        log(
            entity.scripts.getActiveScript()!!::class.java,
            Log.ERR,
            "Tried to call restartScript on a non-persistent script! Either use stopExecuting() or make the script persistent."
        )
        return clearScripts(entity)
    }
    return true
}

fun keepRunning(entity: Entity): Boolean {
    entity.scripts.getActiveScript()?.nextExecution = getWorldTicks() + 1
    return false
}

fun stopExecuting(entity: Entity): Boolean {
    if (entity.scripts.getActiveScript()?.persist == true) {
        log(
            entity.scripts.getActiveScript()!!::class.java,
            Log.ERR,
            "Tried to call stopExecuting() on a persistent script! To halt execution of a persistent script, you MUST call clearScripts()!"
        )
        return clearScripts(entity)
    }
    return true
}

fun queueScript(
    entity: Entity,
    delay: Int = 1,
    strength: QueueStrength = QueueStrength.WEAK,
    persist: Boolean = false,
    script: (stage: Int) -> Boolean
) {
    val s = QueuedScript(script, strength, persist)
    s.nextExecution = getWorldTicks() + delay
    entity.scripts.addToQueue(s, strength)
}

fun stun(entity: Entity, ticks: Int) {
    stun(entity, ticks, true)
}

fun stun(entity: Entity, ticks: Int, sendMessage: Boolean) {
    entity.walkingQueue.reset()
    entity.pulseManager.clear()
    entity.locks.lockMovement(ticks)
    entity.clocks[Clocks.STUN] = getWorldTicks() + ticks
    entity.graphics(Graphics(80, 96))
    if (entity is Player) {
        playAudio(entity.asPlayer(), Sounds.STUNNED_2727)
        entity.animate(Animation(424, Animator.Priority.VERY_HIGH))
        if (sendMessage) {
            sendMessage(entity, "You have been stunned!")
        }
    }
}

fun setCurrentScriptState(entity: Entity, state: Int) {
    val script = entity.scripts.getActiveScript()
    if (script == null) {
        log(ContentAPI::class.java, Log.WARN, "Tried to set a script state when no script was being ran!")
        if (GameWorld.settings?.isDevMode != true) return
        throw IllegalStateException("Script execution mistake - check stack trace and above warning log!")
    }
    script.state =
        state - 1
}

fun modPrayerPoints(player: Player, amount: Double) {
    if (amount > 0) player.skills.incrementPrayerPoints(amount)
    else if (amount < 0) player.skills.decrementPrayerPoints(amount.absoluteValue)
    else return
}

fun isDiaryComplete(player: Player, type: DiaryType, level: Int): Boolean {
    return player.achievementDiaryManager.getDiary(type)!!.isComplete(level)
}

fun hasDiaryTaskComplete(player: Player, type: DiaryType, level: Int, index: Int): Boolean {
    return player.achievementDiaryManager.hasCompletedTask(type, level, index)
}

fun withinDistance(entity: Entity, other: Location, distance: Int? = null): Boolean {
    if (distance != null)
        entity.location.withinDistance(other, distance)
    else
        entity.location.withinDistance(other)

    return true
}

fun finishDiaryTask(player: Player, type: DiaryType, level: Int, task: Int) {
    player.achievementDiaryManager.finishTask(player, type, level, task)
}

fun isPrayerActive(player: Player, type: PrayerType): Boolean {
    return player.prayer.active.contains(type)
}

fun removeTabs(player: Player, vararg tabs: Int) {
    return player.interfaceManager.removeTabs(*tabs)
}

fun resetCamera(player: Player) {
    try {
        PlayerCamera(player).reset()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun refreshInventory(player: Player) {
    player.inventory.refresh()
}

private class ContentAPI