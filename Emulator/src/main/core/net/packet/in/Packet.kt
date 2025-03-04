package core.net.packet.`in`

import core.game.node.entity.player.Player

sealed class Packet {
    data class ItemAction(
        val player: Player,
        val optIndex: Int,
        val itemId: Int,
        val slot: Int,
        val iface: Int,
        val child: Int,
    ) : Packet()

    data class NpcAction(
        val player: Player,
        val optIndex: Int,
        val npcIndex: Int,
    ) : Packet()

    data class SceneryAction(
        val player: Player,
        val optIndex: Int,
        val id: Int,
        val x: Int,
        val y: Int,
    ) : Packet()

    data class PlayerAction(
        val player: Player,
        val optIndex: Int,
        val otherIndex: Int,
    ) : Packet()

    data class GroundItemAction(
        val player: Player,
        val optIndex: Int,
        val id: Int,
        val x: Int,
        val y: Int,
    ) : Packet()

    data class ItemExamine(
        val player: Player,
        val id: Int,
    ) : Packet()

    data class SceneryExamine(
        val player: Player,
        val id: Int,
    ) : Packet()

    data class NpcExamine(
        val player: Player,
        val id: Int,
    ) : Packet()

    data class UseWithNpc(
        val player: Player,
        val itemId: Int,
        val npcIndex: Int,
        val iface: Int,
        val slot: Int,
    ) : Packet()

    data class UseWithPlayer(
        val player: Player,
        val itemId: Int,
        val otherIndex: Int,
        val iface: Int,
        val slot: Int,
    ) : Packet()

    data class UseWithItem(
        val player: Player,
        val usedId: Int,
        val usedWithId: Int,
        val usedSlot: Int,
        val usedWithSlot: Int,
        val usedIface: Int,
        val usedWithIface: Int,
        val usedChild: Int,
        val usedWithChild: Int,
    ) : Packet()

    data class UseWithScenery(
        val player: Player,
        val itemId: Int,
        val slot: Int,
        val sceneryId: Int,
        val x: Int,
        val y: Int,
    ) : Packet()

    data class UseWithGroundItem(
        val player: Player,
        val usedId: Int,
        val withId: Int,
        val iface: Int,
        val child: Int,
        val slot: Int,
        val x: Int,
        val y: Int,
    ) : Packet()

    data class IfAction(
        val player: Player,
        val opcode: Int,
        val optIndex: Int,
        val iface: Int,
        val child: Int,
        val slot: Int,
        val itemId: Int = -1,
    ) : Packet()

    data class ContinueOption(
        val player: Player,
        val iface: Int,
        val child: Int,
        val slot: Int,
        val opcode: Int,
    ) : Packet()

    data class CloseIface(
        val player: Player,
    ) : Packet()

    data class JoinClan(
        val player: Player,
        val clanName: String,
    ) : Packet()

    data class SetClanRank(
        val player: Player,
        val username: String,
        val rank: Int,
    ) : Packet()

    data class KickFromClan(
        val player: Player,
        val username: String,
    ) : Packet()

    data class AddFriend(
        val player: Player,
        val username: String,
    ) : Packet()

    data class RemoveFriend(
        val player: Player,
        val username: String,
    ) : Packet()

    data class AddIgnore(
        val player: Player,
        val username: String,
    ) : Packet()

    data class RemoveIgnore(
        val player: Player,
        val username: String,
    ) : Packet()

    data class TrackingFocus(
        val player: Player,
        val isFocused: Boolean,
    ) : Packet()

    data class TrackingCameraPos(
        val player: Player,
        val x: Int,
        val y: Int,
    ) : Packet()

    data class TrackingDisplayUpdate(
        val player: Player,
        val windowMode: Int,
        val screenWidth: Int,
        val screenHeight: Int,
        val displayMode: Int,
    ) : Packet()

    data class TrackingAfkTimeout(
        val player: Player,
    ) : Packet()

    data class TrackingMouseClick(
        val player: Player,
        val x: Int,
        val y: Int,
        val isRightClick: Boolean,
        val delay: Int,
    ) : Packet()

    data class ComponentPlayerAction(
        val player: Player,
        val otherIndex: Int,
        val iface: Int,
        val child: Int,
    ) : Packet()

    data class ComponentItemAction(
        val player: Player,
        val iface: Int,
        val child: Int,
        val itemId: Int,
        val slot: Int,
    ) : Packet()

    data class ComponentNpcAction(
        val player: Player,
        val iface: Int,
        val child: Int,
        val npcIndex: Int,
    ) : Packet()

    data class ComponentSceneryAction(
        val player: Player,
        val iface: Int,
        val child: Int,
        val sceneryId: Int,
        val x: Int,
        val y: Int,
    ) : Packet()

    data class ComponentGroundItemAction(
        val player: Player,
        val iface: Int,
        val child: Int,
        val slot: Int,
        val itemId: Int,
        val x: Int,
        val y: Int,
    ) : Packet()

    data class SlotSwitchMultiComponent(
        val player: Player,
        val sourceIface: Int,
        val sourceChild: Int,
        val sourceSlot: Int,
        val destIface: Int,
        val destChild: Int,
        val destSlot: Int,
    ) : Packet()

    data class SlotSwitchSingleComponent(
        val player: Player,
        val iface: Int,
        val child: Int,
        val sourceSlot: Int,
        val destSlot: Int,
        val isInsert: Boolean,
    ) : Packet()

    data class PacketCountUpdate(
        val player: Player,
        val count: Int,
    ) : Packet()

    data class PrivateMessage(
        val player: Player,
        val username: String,
        val message: String,
    ) : Packet()

    data class ChatMessage(
        val player: Player,
        val effects: Int,
        val message: String,
    ) : Packet()

    data class ChatSetting(
        val player: Player,
        val public: Int,
        val private: Int,
        val trade: Int,
    ) : Packet()

    data class Command(
        val player: Player,
        val commandLine: String,
    ) : Packet()

    data class GESetOfferItem(
        val player: Player,
        val itemId: Int,
    ) : Packet()

    data class TrackFinished(
        val player: Player,
        val trackId: Int,
    ) : Packet()

    data class ReportAbuse(
        val player: Player,
        val target: String,
        val rule: Int,
        val modMute: Boolean,
    ) : Packet()

    data class WorldspaceWalk(
        val player: Player,
        val destX: Int,
        val destY: Int,
        val isRun: Boolean,
    ) : Packet()

    data class MinimapWalk(
        val player: Player,
        val destX: Int,
        val destY: Int,
        val clickedX: Int,
        val clickedY: Int,
        val rotation: Int,
        val isRun: Boolean,
    ) : Packet()

    data class InteractWalk(
        val player: Player,
        val destX: Int,
        val destY: Int,
        val isRun: Boolean,
    ) : Packet()

    data class Ping(
        val player: Player,
    ) : Packet()

    data class QuickChat(
        val player: Player,
        val indexA: Int,
        val indexB: Int,
        val forClan: Boolean,
        val multiplier: Int,
        val offset: Int,
        val type: QCPacketType,
    ) : Packet()

    data class InputPromptResponse(
        val player: Player,
        val response: Any,
    ) : Packet()

    data class PlayerPrefsUpdate(
        val player: Player,
        val prefs: Int,
    ) : Packet()

    class NoProcess : Packet()

    class UnhandledOp : Packet()

    data class DecodingError(
        val message: String,
    ) : Packet()
}
