package content.region.desert.quest.golem

import content.global.handlers.iface.BookInterface
import content.global.handlers.iface.BookLine
import content.global.handlers.iface.Page
import content.global.handlers.iface.PageSet
import core.api.setAttribute
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import org.rs.consts.Items

class VarmensNotes : InteractionListener {
    companion object {
        private val TITLE = "The Ruins of Uzer"
        val CONTENTS =
            arrayOf(
                PageSet(
                    Page(
                        BookLine("Septober 19:", 55),
                        BookLine("The nomads were right:", 56),
                        BookLine("there is a city here,", 57),
                        BookLine("probably buried for", 58),
                        BookLine("millenia and revealed by", 59),
                        BookLine("the random motions of", 60),
                        BookLine("the sand. The", 61),
                        BookLine("architecture is impressive", 62),
                        BookLine("even in ruin, and must", 63),
                        BookLine("once have been amazing.", 64),
                        BookLine("One puzzling factor is the", 65),
                    ),
                    Page(
                        BookLine("pottery -- there are", 66),
                        BookLine("fragments all over the", 67),
                        BookLine("ruins, surely too much", 68),
                        BookLine("for a city even of this", 69),
                        BookLine("size. We have set up", 70),
                        BookLine("camp and will do a proper", 71),
                        BookLine("survey tomorrow.", 72),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("Septober 20:", 55),
                        BookLine("The meaning of the", 56),
                        BookLine("pottery was explained", 57),
                        BookLine("today in a most", 58),
                        BookLine("surprising manner. We", 59),
                        BookLine("found a mostly-intact clay", 60),
                        BookLine("statue buried up to its", 61),
                        BookLine("waist in sand, and as", 62),
                        BookLine("soon as we dug it out, it", 63),
                        BookLine("started to walk around! It", 64),
                        BookLine("is a clay golem, built by", 65),
                    ),
                    Page(
                        BookLine("the city's inhabitants and", 66),
                        BookLine("dormant all this time. Its", 67),
                        BookLine("head is badly damaged", 68),
                        BookLine("and it is", 69),
                        BookLine("uncommunicative, but its", 70),
                        BookLine("existence tells us that the", 71),
                        BookLine("city's inhabitants were", 72),
                        BookLine("expert magical craftsmen.", 73),
                        BookLine("The huge kilns in some", 74),
                        BookLine("of the buildings indicate", 75),
                        BookLine("that at some point before", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("its destruction the whole", 55),
                        BookLine("city was converted to the", 56),
                        BookLine("manufacture of these", 57),
                        BookLine("golems.", 58),
                        BookLine("", 59),
                        BookLine("We have also examined", 60),
                        BookLine("the carvings on the large", 61),
                        BookLine("building in the centre.", 62),
                        BookLine("These are symbols", 63),
                        BookLine("depicting several of the", 64),
                        BookLine("ancient gods, including", 65),
                    ),
                    Page(
                        BookLine("Saradomin, Zamorak, and", 66),
                        BookLine("Armadyl, but there is", 67),
                        BookLine("another prominent symbol", 68),
                        BookLine("that I cannot identify. As", 69),
                        BookLine("it seems we will need to", 70),
                        BookLine("be here for longer than I", 71),
                        BookLine("had thought, I have sent", 72),
                        BookLine("to Elissa for books on", 73),
                        BookLine("golems and religious", 74),
                        BookLine("symbols.", 75),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("Septober 21:", 55),
                        BookLine("As we examine the ruins", 56),
                        BookLine("one thing becomes", 57),
                        BookLine("increasingly clear: most", 58),
                        BookLine("of the damage was not", 59),
                        BookLine("due to weathering. The", 60),
                        BookLine("buildings were destroyed", 61),
                        BookLine("by force, as if torn down", 62),
                        BookLine("by giant hands.", 63),
                    ),
                    Page(
                        BookLine("Septober 22:", 66),
                        BookLine("A breakthrough! We have", 67),
                        BookLine("found the staircase into", 68),
                        BookLine("the lower levels of the", 69),
                        BookLine("temple. This part has", 70),
                        BookLine("been untouched by the", 71),
                        BookLine("elements, and the", 72),
                        BookLine("carvings here are more", 73),
                        BookLine("intact, especially four", 74),
                        BookLine("beautiful statuettes in", 75),
                        BookLine("alcoves framing the large", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("door. I have removed one", 55),
                        BookLine("of them. The door will", 56),
                        BookLine("not open. I am glad I", 57),
                        BookLine("sent for a book on", 58),
                        BookLine("symbols, as the", 59),
                        BookLine("unidentified symbol is", 60),
                        BookLine("even more prominent", 61),
                        BookLine("here, especially on the", 62),
                        BookLine("door.", 63),
                    ),
                    Page(
                        BookLine("Septober 23:", 66),
                        BookLine("Our messenger returned", 67),
                        BookLine("with the books I asked for", 68),
                        BookLine("and a letter from Elissa.", 69),
                        BookLine("It is unfortunate that the", 70),
                        BookLine("museum will not be able", 71),
                        BookLine("to finance a full-scale", 72),
                        BookLine("excavation here as well as", 73),
                        BookLine("the one closer to Varrock,", 74),
                        BookLine("although I am of course", 75),
                        BookLine("pleased that the other city", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("has been uncovered. But", 55),
                        BookLine("with the books I am able", 56),
                        BookLine("to piece together more of", 57),
                        BookLine("the story of this city.", 58),
                        BookLine("", 59),
                        BookLine("The unidentified symbol", 60),
                        BookLine("in the ruins is that of the", 61),
                        BookLine("demon Thammaron, who", 62),
                        BookLine("was Zamorak's chief", 63),
                        BookLine("lieutenant during the", 64),
                        BookLine("godwars of the Third", 65),
                    ),
                    Page(
                        BookLine("Age. With that", 66),
                        BookLine("information I can say", 67),
                        BookLine("with confidence that these", 68),
                        BookLine("are the ruins of Uzer, an", 69),
                        BookLine("advanced human", 70),
                        BookLine("civilization said to have", 71),
                        BookLine("been destroyed towards", 72),
                        BookLine("the end of the Third Age", 73),
                        BookLine("(roughly 2,500 years", 74),
                        BookLine("ago). It was allied with", 75),
                        BookLine("Saradomin and enjoyed", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("his protection, as well as", 55),
                        BookLine("that of its own mages and", 56),
                        BookLine("warriors. Thammaron was", 57),
                        BookLine("able to open a portal from", 58),
                        BookLine("his own domain straight", 59),
                        BookLine("into the heart of the city,", 60),
                        BookLine("bypassing its defences.", 61),
                        BookLine("With Saradomin's help the", 62),
                        BookLine("army of Uzer was able to", 63),
                        BookLine("drive Thammaron back,", 64),
                        BookLine("but the record ends at", 65),
                    ),
                    Page(
                        BookLine("that point and it has", 66),
                        BookLine("always been assumed that", 67),
                        BookLine("a later attack, either by", 68),
                        BookLine("Thammaron or by", 69),
                        BookLine("Zamorak's other forces,", 70),
                        BookLine("finished the city off.", 71),
                        BookLine("", 72),
                        BookLine("Examining the door", 73),
                        BookLine("again, I now see that it is", 74),
                        BookLine("exactly the sort of door", 75),
                        BookLine("that could be used to seal", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("Thammaron's portal. I am", 55),
                        BookLine("suddently glad I was not", 56),
                        BookLine("able to open it! I surmise", 57),
                        BookLine("that the army of golems", 58),
                        BookLine("was created in order to", 59),
                        BookLine("fight the demon, since", 60),
                        BookLine("Uzer's army had been", 61),
                        BookLine("wiped out and", 62),
                        BookLine("Saradomin's forces were", 63),
                        BookLine("increasingly stretched.", 64),
                        BookLine("However, this approach", 65),
                    ),
                    Page(
                        BookLine("evidently failed, since the", 66),
                        BookLine("city was eventually", 67),
                        BookLine("destroyed.", 68),
                        BookLine("", 69),
                        BookLine("The art of the", 70),
                        BookLine("construction of golems", 71),
                        BookLine("has been lost since the", 72),
                        BookLine("Third Age, and, although", 73),
                        BookLine("they are sometimes", 74),
                        BookLine("discovered lying dormant", 75),
                        BookLine("in the ground, no", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("concerted effort has been", 55),
                        BookLine("made to regain it, thanks", 56),
                        BookLine("largely to the modern", 57),
                        BookLine("Saradomist Church's view", 58),
                        BookLine("of them as unnatural.", 59),
                        BookLine("This view is without", 60),
                        BookLine("foundation, as golems are", 61),
                        BookLine("neither good nor evil but", 62),
                        BookLine("follow instructions they", 63),
                        BookLine("are given to the letter", 64),
                        BookLine("and without imagination,", 65),
                    ),
                    Page(
                        BookLine("indeed experiencing", 66),
                        BookLine("extreme discomfort for as", 67),
                        BookLine("long as a task assigned to", 68),
                        BookLine("them remains incomplete.", 69),
                        BookLine("Some golems were", 70),
                        BookLine("constructed to obey", 71),
                        BookLine("verbal instructions, but", 72),
                        BookLine("the main method of", 73),
                        BookLine("instruction was to place", 74),
                        BookLine("magical words into the", 75),
                        BookLine("golem's skull cavity.", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("These were written on", 55),
                        BookLine("papyrus using a naturally", 56),
                        BookLine("occurring source of ink,", 57),
                        BookLine("and their magical power", 58),
                        BookLine("derived from the use of a", 59),
                        BookLine("phoenix tail feather as a", 60),
                        BookLine("pen. These would be used", 61),
                        BookLine("for long-term or", 62),
                        BookLine("important tasks, and", 63),
                        BookLine("would override any verbal", 64),
                        BookLine("instructions.", 65),
                    ),
                ),
            )

        @Suppress("UNUSED_PARAMETER")
        private fun display(
            player: Player,
            pageNum: Int,
            buttonID: Int,
        ): Boolean {
            BookInterface.pageSetup(player, BookInterface.FANCY_BOOK_3_49, TITLE, CONTENTS)
            return true
        }
    }

    override fun defineListeners() {
        on(Items.VARMENS_NOTES_4616, IntType.ITEM, "read") { player, _ ->
            setAttribute(player, "/save:the-golem:varmen-notes-read", true)
            BookInterface.openBook(player, BookInterface.FANCY_BOOK_3_49, Companion::display)
            return@on true
        }
    }
}
