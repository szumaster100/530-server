package core.net.packet

import content.global.skill.slayer.SlayerManager
import core.api.interaction.getSlayerTask
import core.cache.Cache
import core.cache.def.impl.DataMap
import core.cache.def.impl.ItemDefinition
import core.cache.misc.buffer.ByteBufferUtils
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.system.task.Pulse
import core.game.world.GameWorld.Pulser
import core.game.world.update.flag.EntityFlag
import core.game.world.update.flag.context.ChatMessage
import core.net.packet.`in`.QCPacketType
import core.worker.ManagementEvents.publish
import proto.management.ClanMessage
import java.nio.ByteBuffer
import java.util.*

object QCRepository {
    private val quickChatIndex = Cache.getIndexes()[24]

    @JvmStatic
    fun sendQC(
        player: Player?,
        multiplier: Int?,
        offset: Int?,
        packetType: QCPacketType,
        selection_a_index: Int,
        selection_b_index: Int,
        forClan: Boolean,
    ) {
        val index = getIndex(offset, multiplier)
        player?.setAttribute("qc_offset", offset)
        val qcString =
            when (packetType) {
                QCPacketType.SINGLE -> getSingleQC(index, selection_a_index)
                QCPacketType.DOUBLE -> getDoubleQC(index, selection_a_index, selection_b_index)
                QCPacketType.STANDARD -> getStandardQC(player, index)
                else -> ""
            }

        if (forClan) {
            val builder = ClanMessage.newBuilder()
            builder.sender = player!!.name
            builder.clanName =
                player.communication.clan.owner
                    .lowercase(Locale.getDefault())
                    .replace(" ", "_")
            builder.message = qcString
            builder.rank = player.details.rights.ordinal
            publish(builder.build())
        } else {
            val ctx = ChatMessage(player!!, qcString, 0, qcString.length)
            ctx.isQuickChat = true
            Pulser.submit(
                object : Pulse(0, player) {
                    override fun pulse(): Boolean {
                        player.updateMasks.register(EntityFlag.Chat, ctx)
                        return true
                    }
                },
            )
        }
    }

    @JvmStatic
    fun getStandardQC(
        player: Player?,
        index: Int,
    ): String {
        var qcString = getQCString(index)

        // XP to next level
        if (qcString.contains("to get my next")) {
            val split = qcString.split(" ")
            val skillName = split[split.size - 2]
            val skill = Skills.getSkillByName(skillName.uppercase(Locale.getDefault()))
            val playerXP = player?.skills?.getExperience(skill)
            val playerLevel = player?.skills?.getStaticLevel(skill)
            val nextXP = player?.skills?.getExperienceByLevel(playerLevel?.plus(1) ?: 1)
            qcString = qcString.replace("<", "${(nextXP?.minus(playerXP ?: 0.0) ?: 0.0).toInt()}")
        }

        // My X level is
        else if (qcString.contains("level is")) {
            val skillName = qcString.split(" ")[1]
            val skill = Skills.getSkillByName(skillName.uppercase(Locale.getDefault()))
            val level = player?.skills?.getStaticLevel(skill)
            qcString = qcString.replace("<", level.toString())
        }

        // My current slayer assignment is
        else if (qcString.contains("My current Slayer assignment is")) {
            val amount = SlayerManager.getInstance(player!!).amount
            val taskName = getSlayerTask(player)?.name?.lowercase(Locale.getDefault()) ?: "None"
            if (amount ?: 0 > 0) {
                qcString = qcString.replace("complete", "$amount $taskName")
            }
        }

        return qcString
    }

    @JvmStatic
    fun getSingleQC(
        index: Int,
        selectionIndex: Int,
    ): String {
        var qcString = getQCString(index)

        // Messages that include items
        if ((
                qcString.contains("item") ||
                    qcString.contains("Can I buy your") ||
                    qcString.contains("What is the best world to buy") ||
                    qcString.contains("What is the best world to sell") ||
                    qcString.contains("Would you like to borrow") ||
                    qcString.contains("Could I please borrow")
            ) &&
            qcString.contains("<")
        ) {
            val itemName = ItemDefinition.forId(selectionIndex).name
            qcString = qcString.replace("<", itemName)
        }

        // Loan duration
        else if (qcString.contains("I'd like the loan duration to be")) {
            qcString = qcString.replace("<", getFromMap(1642, selectionIndex))
        }

        // Go to agility course / Try training your agility at
        else if (qcString.contains("Let's go to Agility course:") ||
            qcString.contains("Try training your Agility at:")
        ) {
            qcString = qcString.replace("<", getFromMap(1505, selectionIndex))
        }

        // Try training on
        else if (qcString.contains("Try training on")) {
            qcString = qcString.replace("<", getFromMap(1524, selectionIndex))
        }

        // Try ranging
        else if (qcString.contains("Try ranging")) {
            qcString = qcString.replace("<", getFromMap(1525, selectionIndex))
        }

        // flat-pack
        else if (qcString.contains("flat-pack")) {
            qcString = qcString.replace("<", getFromMap(1490, selectionIndex))
        }

        // Cooking
        else if (qcString.contains("I'm cooking") ||
            qcString.contains("Would you please cook me") ||
            qcString.contains("Try cooking")
        ) {
            qcString = qcString.replace("<", getFromMap(1492, selectionIndex))
        }

        // Crafting
        else if (qcString.contains("I am crafting") ||
            qcString.contains("Try crafting") ||
            qcString.contains("Would you please craft me")
        ) {
            qcString = qcString.replace("<", getFromMap(1493, selectionIndex))
        }

        // Farming
        else if (qcString.contains("I'm growing crop") || qcString.contains("Try growing crop")) {
            qcString = qcString.replace("<", getFromMap(1494, selectionIndex))
        }

        // I'm burning logs...
        else if (qcString.contains("I'm burning logs")) {
            qcString = qcString.replace("<", getFromMap(1495, selectionIndex))
        }

        // Try burning logs at
        else if (qcString.contains("Try burning logs at")) {
            qcString = qcString.replace("<", getFromMap(1544, selectionIndex))
        }

        // Fishing
        else if (qcString.contains("I'm fishing") ||
            qcString.contains("Would you please fish me") ||
            qcString.contains("Try fishing for")
        ) {
            qcString = qcString.replace("<", getFromMap(1491, selectionIndex))
        }

        // Fletching
        else if (qcString.contains("I'm fletching") ||
            qcString.contains("Try fletching") ||
            qcString.contains("Would you please fetch me")
        ) {
            qcString = qcString.replace("<", getFromMap(1496, selectionIndex))
        }

        // Herblore
        else if (qcString.contains("I'm mixing potion") ||
            qcString.contains("Would you please mix me a potion") ||
            qcString.contains("Try mixing potion")
        ) {
            qcString = qcString.replace("<", getFromMap(1526, selectionIndex))
        }

        // Herblore secondaries
        else if (qcString.contains("Where can I get the secondary ingredient")) {
            qcString = qcString.replace("<", getFromMap(1516, selectionIndex))
        }

        // Hunter
        else if (qcString.contains("Would you please hunt me") || qcString.contains("Try hunting")) {
            qcString = qcString.replace("<", getFromMap(1543, selectionIndex))
        }

        // Casting spell
        else if (qcString.contains("I'm casting spell") || qcString.contains("Would you please cast")) {
            qcString = qcString.replace("<", getFromMap(1527, selectionIndex))
        }

        // Spellbook
        else if (qcString.contains("I am on spell book")) {
            qcString = qcString.replace("<", getFromMap(1528, selectionIndex))
        }

        // Mining ore
        else if (qcString.contains("I'm mining ore")) {
            qcString = qcString.replace("<", getFromMap(1529, selectionIndex))
        }

        // Pickaxe using
        else if (qcString.contains("I'm using a pick")) {
            qcString = qcString.replace("<", getFromMap(1531, selectionIndex))
        }

        // Try mining at
        else if (qcString.contains("Try mining at")) {
            qcString = qcString.replace("<", getFromMap(1533, selectionIndex))
        }

        // Use your prayer
        else if (qcString.contains("Use your prayer")) {
            qcString = qcString.replace("<", getFromMap(1534, selectionIndex))
        }

        // i'm going to craft rune
        else if (qcString.contains("I'm going to craft rune")) {
            qcString = qcString.replace("<", getFromMap(1535, selectionIndex))
        }

        // try crafting runes at
        else if (qcString.contains("Try crafting runes at")) {
            qcString = qcString.replace("<", getFromMap(1536, selectionIndex))
        }

        // Slayer master in
        else if (qcString.contains("You should use the Slayer master in")) {
            qcString = qcString.replace("<", getFromMap(2139, selectionIndex))
        }

        // Spare slayer equipment
        else if (qcString.contains("Do you have spare Slayer equipment")) {
            qcString = qcString.replace("<", getFromMap(1538, selectionIndex))
        }

        // familiars
        else if (qcString.contains("I like the familiar") || qcString.contains("I can summon up to")) {
            qcString = qcString.replace("<", getFromMap(1539, selectionIndex))
        }

        // charm droppers
        else if (qcString.contains("Good charm droppers are")) {
            qcString = qcString.replace("<", getFromMap(1499, selectionIndex))
        }

        // Thieving
        else if (qcString.contains("Try thieving from")) {
            qcString = qcString.replace("<", getFromMap(1540, selectionIndex))
        }

        // Axe made of
        else if (qcString.contains("I'm using a woodcutting axe made")) {
            qcString = qcString.replace("<", getFromMap(1532, selectionIndex))
        }

        // Try training woodcutting at
        else if (qcString.contains("Try training Woodcutting at")) {
            qcString = qcString.replace("<", getFromMap(1500, selectionIndex))
        }

        // Nice level in
        else if (qcString.contains("Nice level in")) {
            qcString = qcString.replace("<", getFromMap(1517, selectionIndex))
        }

        return qcString
    }

    fun getFromMap(
        map: Int,
        index: Int,
    ): String {
        return DataMap.get(map).getString(index)
    }

    @JvmStatic
    fun getDoubleQC(
        index: Int,
        selection_a_index: Int,
        selection_b_index: Int,
    ): String {
        var qcString = getQCString(index)

        // Giving directions: That is _ of _
        if (qcString.contains("That is < of <")) {
            qcString = qcString.replaceFirst("<", getFromMap(1502, selection_a_index))
            qcString = qcString.replaceFirst("<", getFromMap(1504, selection_b_index))
        }

        // Smithing
        else if (qcString.contains("I am smithing") ||
            qcString.contains("Try smithing") ||
            qcString.contains("Would you please smith me")
        ) {
            qcString = qcString.replaceFirst("<", getFromMap(1530, selection_a_index))
            qcString = qcString.replaceFirst("<", getFromMap(1498, selection_b_index))
        }

        return qcString
    }

    private fun getIndex(
        offset: Int?,
        multiplier: Int?,
    ): Int {
        offset ?: return 0
        multiplier ?: return 0
        // for some reason it subtracts only from 512 if it's negative (Jagex idk) or 256 if type is 0 but not from 768 if type is 3?
        return if (offset >= 0) {
            (256 * multiplier) + offset
        } else {
            when (multiplier) {
                0 -> 256 + offset
                1 -> 512 + offset
                2 -> 768 + offset
                else -> 1024 + offset
            }
        }
    }

    private fun getQCString(index: Int): String {
        return ByteBufferUtils.getString(ByteBuffer.wrap(quickChatIndex.getFileData(1, index)))
    }
}
