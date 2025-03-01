package content.global.handlers.iface

import core.api.*
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.system.communication.ClanRank
import core.game.system.communication.ClanRepository
import core.net.amsc.MSPacketRepository
import core.net.amsc.WorldCommunicator
import core.tools.Log
import core.tools.StringUtils
import org.rs.consts.Components

class ClanSetupInterface : InterfaceListener {
    private val lastOpcode = mutableMapOf<Int, Int>()
    private val currentRankIndex = mutableMapOf<Int, Int>()

    private val rankLists =
        mapOf(
            23 to
                listOf(
                    ClanRank.ANYONE,
                    ClanRank.ANY_FRIEND,
                    ClanRank.RECRUIT,
                    ClanRank.CORPORAL,
                    ClanRank.SERGEANT,
                    ClanRank.LIEUTENANT,
                    ClanRank.CAPTAIN,
                    ClanRank.GENERAL,
                    ClanRank.ONLY_ME,
                    ClanRank.NO_ONE,
                ),
            24 to
                listOf(
                    ClanRank.ANYONE,
                    ClanRank.ANY_FRIEND,
                    ClanRank.RECRUIT,
                    ClanRank.CORPORAL,
                    ClanRank.SERGEANT,
                    ClanRank.LIEUTENANT,
                    ClanRank.CAPTAIN,
                    ClanRank.GENERAL,
                    ClanRank.ONLY_ME,
                    ClanRank.NO_ONE,
                ),
            25 to
                listOf(
                    ClanRank.CORPORAL,
                    ClanRank.SERGEANT,
                    ClanRank.LIEUTENANT,
                    ClanRank.CAPTAIN,
                    ClanRank.GENERAL,
                    ClanRank.ONLY_ME,
                ),
            26 to
                listOf(
                    ClanRank.NO_ONE,
                    ClanRank.ANY_FRIEND,
                    ClanRank.RECRUIT,
                    ClanRank.CORPORAL,
                    ClanRank.SERGEANT,
                    ClanRank.LIEUTENANT,
                    ClanRank.CAPTAIN,
                    ClanRank.GENERAL,
                ),
        )

    override fun defineInterfaceListeners() {
        on(Components.CLANJOIN_589) { player, _, _, buttonID, _, _ ->
            when (buttonID) {
                9 -> {
                    if (player.interfaceManager.opened != null) {
                        sendMessage(player, "Please close the interface you have open before using 'Clan Setup'")
                    } else {
                        openInterface(player, Components.CLANSETUP_590)
                    }
                }
                14 -> player.communication.toggleLootshare(player)
            }
            return@on true
        }

        onOpen(Components.CLANSETUP_590) { player, _ ->
            val clan = ClanRepository.get(player.name, true)
            updateComponents(player, clan)
            return@onOpen true
        }

        on(Components.CLANSETUP_590) { player, _, opcode, buttonID, _, _ ->
            val clan = ClanRepository.get(player.name, true)

            when (buttonID) {
                22 -> handleClanNameChange(player, clan, opcode)
                in rankLists.keys -> updateRank(player, clan, buttonID, opcode)
                33 -> sendMessage(player, "CoinShare is not available.")
            }

            updateComponents(player, clan)
            clan.update()
            return@on true
        }
    }

    private fun handleClanNameChange(
        player: Player,
        clan: ClanRepository,
        opcode: Int,
    ) {
        if (opcode == 155) {
            sendInputDialogue(player, false, "Enter clan prefix:") { value ->
                val clanName = StringUtils.formatDisplayName(value.toString())
                if (WorldCommunicator.isEnabled()) {
                    MSPacketRepository.sendClanRename(player, clanName)
                }
                if (clan.name == "Chat disabled") {
                    sendMessage(player, "Your clan channel has now been enabled!")
                    sendMessage(player, "Join your channel by clicking 'Join Chat' and typing: ${player.username}")
                }
                clan.name = clanName
                player.communication.clanName = clanName
                clan.update()
            }
        } else if (opcode == 196) {
            clan.name = "Chat disabled"
            player.communication.clanName = ""
            if (WorldCommunicator.isEnabled() && clan.name.isNotEmpty()) {
                MSPacketRepository.sendClanRename(player, clan.name)
            }
            clan.delete()
        }
    }

    private fun updateRank(
        player: Player,
        clan: ClanRepository,
        buttonID: Int,
        opcode: Int,
    ) {
        val rankList = rankLists[buttonID] ?: return
        val lastOp = lastOpcode[buttonID]
        val newIndex =
            if (opcode == lastOp) {
                (currentRankIndex[buttonID] ?: (0 + 1)) % rankList.size
            } else {
                rankList.indexOf(getRankForButton(opcode, rankList)).takeIf { it >= 0 } ?: 0
            }

        lastOpcode[buttonID] = opcode
        currentRankIndex[buttonID] = newIndex
        val newRank = rankList.getOrNull(newIndex) ?: rankList.first()
        updateClanSettings(player, clan, buttonID, newRank)
        log(this.javaClass, Log.INFO, "${buttonID}Requirement: ${newRank.name}")
    }

    private fun getRankForButton(
        opcode: Int,
        list: List<ClanRank>,
    ): ClanRank {
        return list.find { rank -> rank.ordinal == opcode } ?: list.last()
    }

    private fun updateClanSettings(
        player: Player,
        clan: ClanRepository,
        buttonID: Int,
        rank: ClanRank,
    ) {
        when (buttonID) {
            23 -> {
                player.communication.joinRequirement = rank
                clan.setJoinRequirement(rank)
            }
            24 -> {
                player.communication.messageRequirement = rank
                clan.messageRequirement = rank
            }
            25 -> {
                player.communication.kickRequirement = rank
                clan.setKickRequirement(rank)
            }
            26 -> {
                player.communication.lootRequirement = rank
                clan.lootRequirement = rank
            }
        }
        MSPacketRepository.setClanSetting(player, buttonID - 23, rank)
    }

    private fun updateComponents(
        player: Player,
        clan: ClanRepository,
    ) {
        sendString(player, clan.name, Components.CLANSETUP_590, 22)
        sendString(player, clan.joinRequirement.info, Components.CLANSETUP_590, 23)
        sendString(player, clan.messageRequirement.info, Components.CLANSETUP_590, 24)
        sendString(player, clan.kickRequirement.info, Components.CLANSETUP_590, 25)
        sendString(player, clan.lootRequirement.info, Components.CLANSETUP_590, 26)
    }
}
