package content.region.desert.quest.deserttreasure.dialogue

import content.region.desert.quest.deserttreasure.DesertTreasure
import core.api.openDialogue
import core.api.quest.finishQuest
import core.api.quest.getQuestStage
import core.api.sendMessage
import core.game.dialogue.Dialogue
import core.game.dialogue.DialogueBuilder
import core.game.dialogue.DialogueBuilderFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.SpellBookManager
import core.plugin.Initializable
import org.rs.consts.NPCs

@Initializable
class AzzanadraDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        openDialogue(player!!, AzzanadraDialogueFile(), npc)
        return false
    }

    override fun newInstance(player: Player?): Dialogue {
        return AzzanadraDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.SCARABS_1970, NPCs.AZZANADRA_1971)
    }
}

class AzzanadraDialogueFile : DialogueBuilderFile() {
    override fun create(b: DialogueBuilder) {
        b
            .onQuestStages(DesertTreasure.questName, 10)
            .npcl(FaceAnim.OLD_DEFAULT, "I knew they could not trap me here for long!")
            .npcl(FaceAnim.OLD_DEFAULT, "Well done, soldier, tell me, how goes the battle?")
            .playerl(FaceAnim.THINKING, "Battle?")
            .npcl(FaceAnim.OLD_DEFAULT, "You do not know of the battle?")
            .npcl(FaceAnim.OLD_DEFAULT, "More time must have passed than I had thought...")
            .npcl(
                FaceAnim.OLD_DEFAULT,
                "Tell me, what news of great Paddewwa? Do the shining spires of Lassar still stand? And what of glorious Annakarl? The fortress is still intact?",
            ).player("Uh...", "Sorry, I've never heard of them...")
            .npcl(FaceAnim.OLD_SAD, "No!")
            .npcl(FaceAnim.OLD_SAD, "My lord... What has become of you? I cannot hear your voice in my mind anymore!")
            .npcl(
                FaceAnim.OLD_DEFAULT,
                "My thanks to you brave warrior for your help in freeing me from this accursed tomb, but it seems I have much to do to make amends.",
            ).npcl(
                FaceAnim.OLD_DEFAULT,
                "If the shining cities no longer stand, then it means that we must have failed my lord...",
            ).npcl(
                FaceAnim.OLD_DEFAULT,
                "How long have I been trapped here? Master... Have you truly been dispatched from this world?",
            ).npcl(FaceAnim.OLD_DEFAULT, "Warrior, for your efforts in freeing me, I offer you the gift of knowledge.")
            .npcl(
                FaceAnim.OLD_DEFAULT,
                "I bestow upon you the ancient magicks, taught me by my Lord before his disappearance, may you use them well in battle for our people!",
            ).npcl(
                FaceAnim.OLD_DEFAULT,
                "They will replace the knowledge you previously had, but you may switch between them by praying at the altar in this room at any time.",
            ).npcl(
                FaceAnim.OLD_DEFAULT,
                "I trust that we shall meet again adventurer, I offer you the blessings of myself and my master in all of your endeavours!",
            ).npcl(
                FaceAnim.OLD_DEFAULT,
                "Now, I must leave you, for there must be some trace of my master's power left somewhere. Feel free to use the portal I shall create to return here easily in the future!",
            ).endWith { _, player ->
                if (getQuestStage(player, DesertTreasure.questName) == 10) {
                    sendMessage(player, "A strange wisdom has filled your mind...")
                    finishQuest(player, DesertTreasure.questName)
                    player.spellBookManager.setSpellBook(SpellBookManager.SpellBook.ANCIENT)
                    player.spellBookManager.update(player)
                }
            }
    }
}
