package core.game.node.entity.player.info.login

import content.data.GameAttributes
import content.data.GlobalStore
import content.region.kandarin.miniquest.knightwave.KnightWaveAttributes
import core.ServerConstants
import core.api.*
import core.api.quest.hasRequirement
import core.api.quest.isQuestComplete
import core.game.component.Component
import core.game.interaction.InteractionListeners
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.SpellBookManager
import core.game.node.entity.player.link.emote.Emotes
import core.game.world.GameWorld
import core.game.world.map.RegionManager
import core.game.world.repository.Repository
import core.game.world.update.UpdateSequence
import core.net.packet.PacketRepository
import core.net.packet.context.InterfaceContext
import core.net.packet.out.Interface
import core.plugin.Plugin
import core.tools.Log
import core.tools.colorize
import org.rs.consts.Components
import org.rs.consts.Quests
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.IntStream

object LoginConfiguration {
    var loginPlugins = mutableListOf<Plugin<Any>>()
    private val lobbyPane = Component(Components.TOPLEVEL_FULL_549)
    private val lobbyInterface = Component(Components.WELCOME_SCREEN_378)
    private val lobbyComponents =
        intArrayOf(
            Components.MESSAGE_OF_THE_WEEK_17,
            Components.MESSAGE_OF_THE_WEEK_18,
            Components.MESSAGE_OF_THE_WEEK_19,
            Components.MESSAGE_OF_THE_WEEK_20,
            Components.BANNER_PADLOCK_KEYS_15,
            Components.BANNER_ANTI_VIRUS_16,
            Components.BANNER_SCAMMING_21,
            Components.BANNER_SECURITY_22,
            Components.BANNER_XMAS_23,
            Components.BANNER_POH_405,
            Components.BANNER_CHATHEADS_447,
            Components.BANNER_GROUP_622,
            Components.BANNER_GROUP_ASSIST_623,
            Components.BANNER_SUMMONING_679,
            Components.BANNER_EASTER08_715,
            Components.BANNER_HALLOWEEN_800,
        )

    @JvmStatic
    fun configureLobby(player: Player) {
        player.updateSceneGraph(true)
        if (isTutorialCompleted(player) && player.isNotReconnecting()) {
            sendLobbyScreen(player)
        } else {
            configureGameWorld(player)
        }
    }

    @JvmStatic
    fun sendLobbyScreen(player: Player) {
        val selectedMessageModel = autoSelectMessageModel()
        Repository.lobbyPlayers.removeIf { it.name == player.name }
        Repository.lobbyPlayers.add(player)

        sendString(player, "Welcome to ${GameWorld.settings?.name}", lobbyInterface.id, 115)
        sendString(player, getLastLogin(player), lobbyInterface.id, 116)

        player.interfaceManager.openWindowsPane(lobbyPane)
        player.interfaceManager.opened = lobbyInterface

        PacketRepository.send(Interface::class.java, InterfaceContext(player, lobbyPane.id, 2, 378, true))
        PacketRepository.send(
            Interface::class.java,
            InterfaceContext(player, lobbyPane.id, 3, selectedMessageModel, true),
        )
        sendString(
            player,
            GameWorld.settings?.message_string ?: "",
            selectedMessageModel,
            getMessageChild(selectedMessageModel),
        )
        player.details.lastLogin = System.currentTimeMillis()
    }

    @JvmStatic
    fun configureGameWorld(player: Player) {
        sendGameConfiguration(player)

        Repository.lobbyPlayers.remove(player)
        player.isPlaying = true

        UpdateSequence.rendererPlayers.add(player)
        RegionManager.move(player)
        player.musicPlayer.init()
        player.updateAppearance()
        player.details.setJoinDate(player)
        player.playerFlags.lastSceneGraph = player.location
        player.packetDispatch.sendInterfaceConfig(226, 1, true)

        checkEmotes(player)

        setupItems(player)

        setupSpellBook(player)

        setupPrayer(player)
    }

    @JvmStatic
    fun sendGameConfiguration(player: Player) {
        player.interfaceManager.openWindowsPane(Component(if (player.interfaceManager.isResizable) 746 else 548))
        player.interfaceManager.openChatbox(Components.CHATDEFAULT_137)
        player.interfaceManager.openDefaultTabs()

        welcome(player)
        config(player)

        loginPlugins.forEach { plugin ->
            try {
                plugin.newInstance(player)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        player.appearance.sync()
    }

    @JvmStatic
    fun welcome(player: Player) {
        if (player.isArtificial) return

        player.packetDispatch.sendMessage("Welcome to ${ServerConstants.SERVER_NAME}.")

        GlobalStore.check(player)

        if (player.details.isMuted) {
            player.packetDispatch.sendMessage("You are muted.")
            player.packetDispatch.sendMessage("To prevent further mutes, please read the rules.")
        }
    }

    @JvmStatic
    fun config(player: Player) {
        if (!player.isArtificial) {
            log(LoginConfiguration::class.java, Log.INFO, "Configuring player ${player.username}")
        }
        player.interfaceManager.openDefaultTabs()
        player.interfaceManager.openInfoBars()
        player.inventory.refresh()
        player.equipment.refresh()
        player.skills.refresh()
        player.skills.configure()
        player.settings.update()
        player.interaction.setDefault()
        player.packetDispatch.sendRunEnergy()
        player.familiarManager.login()

        sendString(
            player,
            "Friends List - ${ServerConstants.SERVER_NAME} ${GameWorld.settings?.worldId}",
            Components.FRIENDS2_550,
            3,
        )
        sendString(
            player,
            "When you have finished playing ${ServerConstants.SERVER_NAME}, always use the button below to logout safely.",
            Components.LOGOUT_182,
            0,
        )

        player.questRepository.syncronizeTab(player)
        player.interfaceManager.close()
        player.emoteManager.refresh()

        if (!player.isArtificial) {
            log(LoginConfiguration::class.java, Log.INFO, "Finished configuring player ${player.username}")
        }
    }

    fun getMessageChild(interfaceId: Int): Int {
        return when (interfaceId) {
            Components.BANNER_GROUP_622 -> 8
            Components.BANNER_ANTI_VIRUS_16 -> 6
            Components.MESSAGE_OF_THE_WEEK_20,
            Components.BANNER_GROUP_ASSIST_623,
            -> 5

            Components.BANNER_PADLOCK_KEYS_15,
            Components.MESSAGE_OF_THE_WEEK_18,
            Components.MESSAGE_OF_THE_WEEK_19,
            Components.BANNER_SCAMMING_21,
            Components.BANNER_SECURITY_22,
            Components.BANNER_CHATHEADS_447,
            Components.BANNER_POH_405,
            -> 4

            Components.MESSAGE_OF_THE_WEEK_17,
            Components.BANNER_XMAS_23, Components.BANNER_HALLOWEEN_800,
            -> 3

            Components.BANNER_EASTER08_715 -> 2
            Components.BANNER_SUMMONING_679 -> 1
            else -> 0
        }
    }

    private fun autoSelectMessageModel(): Int {
        return if (IntStream.of(lobbyComponents.size).anyMatch { it == GameWorld.settings?.message_model }) {
            GameWorld.settings?.message_model ?: lobbyComponents.random()
        } else {
            lobbyComponents.random()
        }
    }

    @JvmStatic
    fun getLastLogin(player: Player): String {
        var lastIp =
            player.details.accountInfo.lastUsedIp
                .ifEmpty { player.details.ipAddress }
        player.details.accountInfo.lastUsedIp = player.details.ipAddress
        val timeAgo = calculateTimeAgo(player.details.lastLogin)
        return "You last logged in $timeAgo from: $lastIp"
    }

    private fun calculateTimeAgo(lastLoginTime: Long): String {
        val lastLogin = Date(lastLoginTime)
        val current = Date()
        val diff = current.time - lastLogin.time
        val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
        return when {
            days < 1 -> "earlier today"
            days == 1 -> "yesterday"
            else -> "$days days ago"
        }
    }

    private fun isTutorialCompleted(player: Player): Boolean {
        return player.getAttribute(GameAttributes.TUTORIAL_COMPLETE, false)
    }

    private fun Player.isNotReconnecting(): Boolean {
        return getAttribute("login_type", LoginType.NORMAL_LOGIN) != LoginType.RECONNECT_TYPE
    }

    private fun checkEmotes(player: Player) {
        if (player.globalData.getPlayerTestStage() == 3 && !player.emoteManager.isUnlocked(Emotes.SAFETY_FIRST)) {
            player.emoteManager.unlock(Emotes.SAFETY_FIRST)
        }
    }

    private fun setupItems(player: Player) {
        player.equipment.toArray().forEach { item ->
            if (item != null) {
                player.equipment.remove(item)
                if (!InteractionListeners.run(item.id, player, item, true) ||
                    !player.equipment.add(item, true, false)
                ) {
                    player.sendMessage(
                        colorize(
                            "%RYou had items equipped in the wrong slots. They were moved out into your inventory.",
                        ),
                    )
                    addItemOrBank(player, item.id, item.amount)
                }
            }
        }
    }

    private fun setupSpellBook(player: Player) {
        val currentSpellBook = SpellBookManager.SpellBook.forInterface(player.spellBookManager.spellBook)
        if (currentSpellBook == SpellBookManager.SpellBook.ANCIENT &&
            !isQuestComplete(player, Quests.DESERT_TREASURE)
        ) {
            player.sendMessage(colorize("%RAs you can no longer use Ancient Magic, you have been set back to Modern."))
            player.spellBookManager.spellBook = 0
        } else if (currentSpellBook == SpellBookManager.SpellBook.LUNAR &&
            !hasRequirement(player, Quests.LUNAR_DIPLOMACY)
        ) {
            player.sendMessage(colorize("%RAs you can no longer use Lunar Magic, you have been set back to Modern."))
            player.spellBookManager.spellBook = 0
        }
        player.spellBookManager.update(player)
    }

    private fun setupPrayer(player: Player) {
        if (getAttribute(player, KnightWaveAttributes.KW_COMPLETE, false)) {
            setVarbit(player, 3909, 8, false)
        } else {
            setVarbit(player, 3909, 0, false)
        }
    }
}
