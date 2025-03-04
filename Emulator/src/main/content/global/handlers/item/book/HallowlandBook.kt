package content.global.handlers.item.book

import content.global.handlers.iface.BookInterface
import content.global.handlers.iface.BookLine
import content.global.handlers.iface.Page
import content.global.handlers.iface.PageSet
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.world.GameWorld.settings
import org.rs.consts.Items

class HallowlandBook : InteractionListener {
    /*
     * Obtainable during In Aid of the Myreque.
     */

    companion object {
        private const val TITLE = "Histories of the Hallowland"
        private val CONTENTS =
            arrayOf(
                PageSet(
                    Page(
                        BookLine("In years gone by, the", 55),
                        BookLine("land or Morytania went", 56),
                        BookLine("by another name...that", 57),
                        BookLine("of the, 'Hallowed Land'. Much", 58),
                        BookLine("of that age has now disappeared", 59),
                        BookLine("into mists of time. And", 60),
                        BookLine("as distant and forbidding", 61),
                        BookLine("as that time appears to", 62),
                        BookLine("be, sometimes like that", 63),
                        BookLine("dread land Morytania itself,", 64),
                        BookLine("we need to shoulder our", 65),
                    ),
                    Page(
                        BookLine("heavy armour and trudge", 66),
                        BookLine("wearily onwards. To", 67),
                        BookLine("understand the past gives", 68),
                        BookLine("us some glimmer of hope", 69),
                        BookLine("of understanding our time", 70),
                        BookLine("here now and perhaps a", 71),
                        BookLine("hopeful glance at a future.", 72),
                        BookLine("The strange structures of", 73),
                        BookLine("the land or Morytania is due,", 74),
                        BookLine("most likely, in no small", 75),
                        BookLine("part to the many wars that", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("have been fought through", 55),
                        BookLine("the lands of" + settings!!.name + ".", 56),
                        BookLine("But even here, there is", 57),
                        BookLine("something quite strange", 58),
                        BookLine("about the terrain. Swampy", 59),
                        BookLine("and rotten, the foul smell", 60),
                        BookLine("of death and desolation", 61),
                        BookLine("fills the air. Where does", 62),
                        BookLine("this pungent aroma come", 63),
                        BookLine("from? It is said that", 64),
                        BookLine("before the dark lord Drakan", 65),
                    ),
                    Page(
                        BookLine("arrived in Morytania,", 66),
                        BookLine("a very different land", 67),
                        BookLine("existed. A green land,", 68),
                        BookLine("pleasant and bounteous.", 69),
                        BookLine("Sufficient to please hundreds", 70),
                        BookLine("of farmers, and to supply", 71),
                        BookLine("large towns, villages", 72),
                        BookLine("and cities with food.", 73),
                        BookLine("there is little information", 74),
                        BookLine("to support this idea,", 75),
                        BookLine("but there are hints that", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("this land had not always", 55),
                        BookLine("been the darkened and", 56),
                        BookLine("dreadful place it now", 57),
                        BookLine("appears to be. There is", 58),
                        BookLine("some speculation about", 59),
                        BookLine("a time before our recorded", 60),
                        BookLine("history when there appeared", 61),
                        BookLine("to be some disputes between", 62),
                        BookLine("the gods. In this time,", 63),
                        BookLine("it is said, that Saradomin", 64),
                        BookLine("and Zamorak brought forces", 65),
                    ),
                    Page(
                        BookLine("through into the world", 66),
                        BookLine("to fight for them and", 67),
                        BookLine("claim large areas of land.", 68),
                        BookLine("Where they brought these", 69),
                        BookLine("forces from and how is", 70),
                        BookLine("still a mystery. But,", 71),
                        BookLine("we can hardly deny that", 72),
                        BookLine("many weird and un-natural", 73),
                        BookLine("creatures do exist in", 74),
                        BookLine("this land, things which", 75),
                        BookLine("even the most advanced", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("of scholars do not yet", 55),
                        BookLine("comprehend. Many of these", 56),
                        BookLine("creatures still infest", 57),
                        BookLine("Morytania and their place", 58),
                        BookLine("of origin is still not", 59),
                        BookLine("properly located. As an", 60),
                        BookLine("example of strange creatures", 61),
                        BookLine("let us focus on the Ghasts", 62),
                        BookLine("in Mort Myre swamp! Which", 63),
                        BookLine("bizarre act of nature", 64),
                        BookLine("caused these freakish", 65),
                    ),
                    Page(
                        BookLine("creatures to exist? It", 66),
                        BookLine("seems that whoever dies", 67),
                        BookLine("in that diseased and disgusting", 68),
                        BookLine("place is forever doomed", 69),
                        BookLine("to it, ever hungry and", 70),
                        BookLine("searching for victims", 71),
                        BookLine("to feed on. Blessed it", 72),
                        BookLine("is then that somehow brave", 73),
                        BookLine("new adventurers are able", 74),
                        BookLine("to reveal these foul creatures", 75),
                        BookLine("and destroy them (with", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("great aid of Guthix, who", 55),
                        BookLine("adores balance amongst", 56),
                        BookLine("all things), and at the", 57),
                        BookLine("same time release the", 58),
                        BookLine("tortured soul from its", 59),
                        BookLine("earthbound torment. It", 60),
                        BookLine("is clear that some influence", 61),
                        BookLine("is at work in Morytania", 62),
                        BookLine("and its intent is bent", 63),
                        BookLine("towards evil. Before this", 64),
                        BookLine("dark period in history,", 65),
                    ),
                    Page(
                        BookLine("Morytania is believed", 66),
                        BookLine("to have had a golden age", 67),
                        BookLine("of peace. It is hard to", 68),
                        BookLine("imagine that the Shade", 69),
                        BookLine("infested town of Mort'ton", 70),
                        BookLine("could have once been a", 71),
                        BookLine("busy, happy thriving bustling", 72),
                        BookLine("town. And even Canifis,", 73),
                        BookLine("once a busy market village", 74),
                        BookLine("supplying all manner of", 75),
                        BookLine("goods to the local population,", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("instead of a cesspit of", 55),
                        BookLine("Werewolf activity and", 56),
                        BookLine("a certain death trap for", 57),
                        BookLine("all but the hardiest of", 58),
                        BookLine("adventurers. Stories and", 59),
                        BookLine("yarns told by the inhabitants", 60),
                        BookLine("of Morytania, and those", 61),
                        BookLine("lucky few who make their", 62),
                        BookLine("way out of that dark land,", 63),
                        BookLine("give us some glimpses", 64),
                        BookLine("of what might have happened", 65),
                    ),
                    Page(
                        BookLine("there. From these peoples", 66),
                        BookLine("come many bits of story", 67),
                        BookLine("and song from which we", 68),
                        BookLine("can piece together a plausible", 69),
                        BookLine("history line, that is", 70),
                        BookLine("the true aim of this tome. Let", 71),
                        BookLine("us begin with the current", 72),
                        BookLine("overlord of Morytania,", 73),
                        BookLine("Lowerniel Vergidiyad Drakan.", 74),
                        BookLine("his reign in Morytania", 75),
                        BookLine("has been unchallenged", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("in the history of this", 55),
                        BookLine("land, as far as we can", 56),
                        BookLine("tell. But how long has", 57),
                        BookLine("this been? What do we", 58),
                        BookLine("know of this dark lord?", 59),
                        BookLine("It is clear to many who", 60),
                        BookLine("have visited Morytania", 61),
                        BookLine("that he is a creature", 62),
                        BookLine("of darkness, a Vampyre,", 63),
                        BookLine("but by which strange", 64),
                        BookLine("happenstance", 65),
                    ),
                    Page(
                        BookLine("did he come to be here? Some", 66),
                        BookLine("reports say that he came", 67),
                        BookLine("from beneath the ground", 68),
                        BookLine("of a great castle that", 69),
                        BookLine("stands in the middle of", 70),
                        BookLine("Morytania, but reports", 71),
                        BookLine("claim that a huge city", 72),
                        BookLine("lies beneath the castle,", 73),
                        BookLine("this does not seam plausible.", 74),
                        BookLine("yet others say that there", 75),
                        BookLine("are countless tunnels", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("which lead into a flaming", 55),
                        BookLine("dark inferno, which it", 56),
                        BookLine("is claimed these dark", 57),
                        BookLine("creatures come from, even", 58),
                        BookLine("more unlikely. What is", 59),
                        BookLine("interesting is that in", 60),
                        BookLine("all these great yarns,", 61),
                        BookLine("one fact remains always", 62),
                        BookLine("the same; he came into", 63),
                        BookLine("and attacked a castle", 64),
                        BookLine("which already existed. It", 65),
                    ),
                    Page(
                        BookLine("is the author's belief", 66),
                        BookLine("that the castle existed", 67),
                        BookLine("before Drakan entered", 68),
                        BookLine("Morytania and that some", 69),
                        BookLine("other power existed in", 70),
                        BookLine("the land before him. What", 71),
                        BookLine("this power was is not", 72),
                        BookLine("known, though stories", 73),
                        BookLine("again give us a glimpse", 74),
                        BookLine("at what might have been.", 75),
                        BookLine("Once such tale is of a", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("Queen from far away land", 55),
                        BookLine("of considerable power", 56),
                        BookLine("and revered by the peoples.", 57),
                        BookLine("It is also claimed that", 58),
                        BookLine("the peoples named the", 59),
                        BookLine("land 'Hallowvale' after", 60),
                        BookLine("here, though there is", 61),
                        BookLine("no true record of this. It", 62),
                        BookLine("is interesting to consider,", 63),
                        BookLine("is it not, that a time", 64),
                        BookLine("of peoples and pleasantness", 65),
                    ),
                    Page(
                        BookLine("could have existed in", 66),
                        BookLine("the land of Morytania", 67),
                        BookLine("before this. And perhaps", 68),
                        BookLine("to consider, hope and", 69),
                        BookLine("pray that at some point", 70),
                        BookLine("those times shall be returned", 71),
                        BookLine("to this dark, plagued", 72),
                        BookLine("and unhappy land. However,", 73),
                        BookLine("let us not forget that", 74),
                        BookLine("had it not been for the", 75),
                        BookLine("seven priestly warriors", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("at Silverea, that lands", 55),
                        BookLine("of Morytania might extend", 56),
                        BookLine("today as far as Falador...and", 57),
                        BookLine("Varrock may have been", 58),
                        BookLine("an even more sinister", 59),
                        BookLine("city than it appears today!", 60),
                        BookLine("And of those seven, perhaps", 61),
                        BookLine("the most intriguing could", 62),
                        BookLine("said to be Ivandis Seergaze", 63),
                        BookLine("of Lumbridge. his silvthril", 64),
                        BookLine("rod of power with blue", 65),
                    ),
                    Page(
                        BookLine("serene gem was said to", 66),
                        BookLine("have been crafted by the", 67),
                        BookLine("artisans of Saradomin", 68),
                        BookLine("himself. And though there", 69),
                        BookLine("are no accurate records,", 70),
                        BookLine("it is said that its exacting", 71),
                        BookLine("and precise dimensions", 72),
                        BookLine("and shape were the just", 73),
                        BookLine("reason for its great power.", 74),
                        BookLine("When asked about how it was", 75),
                        BookLine("made and how it came into", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("being, Ivandis had always", 55),
                        BookLine("simply commeted Of Silvthril,", 56),
                        BookLine("and exact dimensions is", 57),
                        BookLine("the item constructed.", 58),
                        BookLine("After enchantments that", 59),
                        BookLine("come from the magic of", 60),
                        BookLine("man can it be placed in", 61),
                        BookLine("our lord Saradomin's holy", 62),
                        BookLine("water, where magical blessings", 63),
                        BookLine("will 'He' commit to it.", 64),
                        BookLine("It is not clear why he should", 65),
                    ),
                    Page(
                        BookLine("hide such important details", 66),
                        BookLine("in such incoherent ramblings.", 67),
                        BookLine("Suffice to say that many", 68),
                        BookLine("artisans have tried recreating", 69),
                        BookLine("this holy relic in a bid", 70),
                        BookLine("to supply adventurous", 71),
                        BookLine("types keen on restoring", 72),
                        BookLine("balance to Morytania.", 73),
                        BookLine("But alas this could not", 74),
                        BookLine("happen because the true", 75),
                        BookLine("dimensions of the relic", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("are not accurately known.", 55),
                        BookLine("It is believed that this", 56),
                        BookLine("secret, along with the", 57),
                        BookLine("artefact, died with Ivandis", 58),
                        BookLine("and now lies sealed up", 59),
                        BookLine("inside his tomb beneath", 60),
                        BookLine("Paterdomus, never to be seen", 61),
                        BookLine("again. Another infuriatingly", 62),
                        BookLine("cryptic element which", 63),
                        BookLine("is yet to be explained", 64),
                        BookLine("is the 'Balance' which", 65),
                    ),
                    Page(
                        BookLine("Guthix is supposed to", 66),
                        BookLine("have invested in Ivandis,", 67),
                        BookLine("in the form of a potion.", 68),
                        BookLine("Once again his cryptic", 69),
                        BookLine("message does not help", 70),
                        BookLine("very much. 'Guthix's balance'", 71),
                        BookLine("needs to be fed to those", 72),
                        BookLine("minions of darkness in", 73),
                        BookLine("order for the balance", 74),
                        BookLine("to be restored. And this", 75),
                        BookLine("task is carried out only", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("when they are held in", 55),
                        BookLine("Saradomin's power. for", 56),
                        BookLine("this to occur, one should", 57),
                        BookLine("supplement a common potion", 58),
                        BookLine("of bodily restoration", 59),
                        BookLine("with the essence of garlic", 60),
                        BookLine("and silver dust, the later", 61),
                        BookLine("being gained by the use", 62),
                        BookLine("of a strange machine in", 63),
                        BookLine("the tower to the north", 64),
                        BookLine("east of Morytania and", 65),
                    ),
                    Page(
                        BookLine("in the hands of some ghostly", 66),
                        BookLine("beings. Whatever the", 67),
                        BookLine("truth of all these details,", 68),
                        BookLine("it seems clear that Drakan", 69),
                        BookLine("is intent on staying.", 70),
                        BookLine("His authority in this", 71),
                        BookLine("region has never been", 72),
                        BookLine("challenged and perhaps", 73),
                        BookLine("arguably has grown over", 74),
                        BookLine("the centuries. Reports", 75),
                        BookLine("from the area are limited", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("and vague so perhaps the", 55),
                        BookLine("truth is either far less,", 56),
                        BookLine("or far greater than we", 57),
                        BookLine("may currently comprehend.", 58),
                        BookLine("In any event, it is clear", 59),
                        BookLine("that some strategy should", 60),
                        BookLine("be forthcoming if Drakan's", 61),
                        BookLine("evil minions should once", 62),
                        BookLine("again try to spread into", 63),
                        BookLine("Misthilan. It is believed,", 64),
                        BookLine("perhaps in error, that", 65),
                    ),
                    Page(
                        BookLine("Paterdomus will stand forever", 66),
                        BookLine("as a silent guardian against", 67),
                        BookLine("the power of Morytania.", 68),
                        BookLine("Perhaps one day we will", 69),
                        BookLine("see a redress of the evil", 70),
                        BookLine("that Drakan has brought", 71),
                        BookLine("to Morytania, and we will", 72),
                        BookLine("get to witness first hand", 73),
                        BookLine("the bounteous riches and", 74),
                        BookLine("wholesome living that", 75),
                        BookLine("can be had in that land", 76),
                        BookLine("once called 'Hallowvale'.", 55),
                    ),
                ),
            )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun display(
        player: Player,
        pageNum: Int,
        buttonID: Int,
    ): Boolean {
        BookInterface.pageSetup(player, BookInterface.FANCY_BOOK_3_49, TITLE, CONTENTS)
        return true
    }

    override fun defineListeners() {
        on(Items.BATTERED_TOME_7634, IntType.ITEM, "read") { player, _ ->
            BookInterface.openBook(player, BookInterface.FANCY_BOOK_3_49, ::display)
            return@on true
        }
    }
}
