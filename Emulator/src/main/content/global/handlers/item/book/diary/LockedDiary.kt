package content.global.handlers.item.book.diary

import content.global.handlers.iface.BookInterface
import content.global.handlers.iface.BookLine
import content.global.handlers.iface.Page
import content.global.handlers.iface.PageSet
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import org.rs.consts.Items

class LockedDiary : InteractionListener {
    /*
     * Found by searching Sandy's desk in Brimhaven during
     * the Returning Clarence miniquest after the Back to my Roots quest.
     */

    companion object {
        private const val TITLE = "Locked diary"
        private val CONTENTS =
            arrayOf(
                PageSet(
                    Page(
                        BookLine("4th Bennath, 168", 55),
                        BookLine("I'm going to start keeping", 56),
                        BookLine("a diary of my life, to", 57),
                        BookLine("leave to all those budding", 58),
                        BookLine("entrepreneurs who want", 59),
                        BookLine("to start their own business", 60),
                        BookLine("- who knows, maybe in", 61),
                        BookLine("the future someone can", 62),
                        BookLine("write a book about my", 63),
                        BookLine("success. Anyway, today", 64),
                        BookLine("I hit on a really great", 65),
                        BookLine("idea. People are always", 66),
                        BookLine("running out of sand in", 67),
                        BookLine("the sand pits, so I'm", 68),
                        BookLine("going to set up a business", 69),
                        BookLine("carting sand from some", 70),
                        BookLine("sunny beach all the way", 71),
                        BookLine("to the sandpits. This", 72),
                        BookLine("also means I get a free", 73),
                        BookLine("holiday every day. I", 74),
                        BookLine("think it's a rather good", 75),
                        BookLine("idea.", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("5th Bennath, 168", 56),
                        BookLine("Spent my day drawing", 57),
                        BookLine("out plans for my business,", 58),
                        BookLine("it looks like Karamja", 59),
                        BookLine("will be my best bet,", 60),
                        BookLine("especially since there's", 61),
                        BookLine("already a settlement", 62),
                        BookLine("there, the natives shouldn't", 63),
                        BookLine("bother me that much,", 64),
                        BookLine("and I can hire some people", 65),
                    ),
                    Page(
                        BookLine("to take sand via the", 66),
                        BookLine("boats. Had a nice fillet", 67),
                        BookLine("of bass for tea.", 68),
                        BookLine("12th Bennath, 168", 70),
                        BookLine("I haven't written here", 71),
                        BookLine("for a while. After making", 72),
                        BookLine("some enquiries about", 73),
                        BookLine("an office on Karamja", 74),
                        BookLine("for a new business, things", 75),
                        BookLine("fairly raced ahead. I", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("need to sort out some", 55),
                        BookLine("way of getting the building", 56),
                        BookLine("set up now: perhaps I", 57),
                        BookLine("can hire some of the", 58),
                        BookLine("local natives to build", 59),
                        BookLine("it for me?", 60),
                        BookLine("6th Raktuber, 168", 62),
                        BookLine("Okay, so my daily diary", 63),
                        BookLine("has gone a bit awry,", 64),
                        BookLine("but the building is finally", 65),
                    ),
                    Page(
                        BookLine("done. I hung the sign", 66),
                        BookLine("today myself. Now I need", 67),
                        BookLine("to get things running", 68),
                        BookLine("quickly - the loan I", 69),
                        BookLine("got from that man in", 70),
                        BookLine("the pub needs repaying", 71),
                        BookLine("...and I have a feeling", 72),
                        BookLine("I had better not default", 73),
                        BookLine("on it. I have to get", 74),
                        BookLine("things in order and find", 75),
                        BookLine("some customers. I wish", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("I'd done his before building", 55),
                        BookLine("the headquarters. Still,", 56),
                        BookLine("the weather is beautiful", 57),
                        BookLine("and I shall sail to Port", 58),
                        BookLine("Khazard today.", 59),
                        BookLine("15th Raktuber, 168", 61),
                        BookLine("I think I have my first", 62),
                        BookLine("customer. Good job, too,", 63),
                        BookLine("as that man who gave", 64),
                        BookLine("me the loan came around", 65),
                    ),
                    Page(
                        BookLine("yesterday with some friends", 66),
                        BookLine("of his. At first I thought", 67),
                        BookLine("they were a charity as", 68),
                        BookLine("they all had black bands", 69),
                        BookLine("on their arms... I must", 70),
                        BookLine("find some money for", 71),
                        BookLine("tomorrow.", 72),
                        BookLine("Luckily, that doesn't", 73),
                        BookLine("seem too hard as my", 74),
                        BookLine("customer in Yanille seems", 75),
                        BookLine("to be willing to pay", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("for the sand, and even", 55),
                        BookLine("threw in some labour to", 56),
                        BookLine("help transport it.", 57),
                        BookLine("Mind you I'm not sure the", 58),
                        BookLine("old guy can actually", 59),
                        BookLine("carry the sand,", 60),
                        BookLine("but we'll see.", 61),
                        BookLine("16th Raktuber, 168", 63),
                        BookLine("Got my first payment", 64),
                        BookLine("for sand today, not bad", 65),
                    ),
                    Page(
                        BookLine("if I do say so myself.", 66),
                        BookLine("Only problem is, they", 67),
                        BookLine("came round for the loan", 68),
                        BookLine("payment today. So yet", 69),
                        BookLine("again I'm left with", 70),
                        BookLine("nothing.", 71),
                        BookLine("30th Raktuber, 168", 73),
                        BookLine("I've managed to get", 74),
                        BookLine("another customer, this", 75),
                        BookLine("time on that pacifist", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("island of Entrana. Not", 55),
                        BookLine("sure what they need", 56),
                        BookLine("all the sand for, but", 57),
                        BookLine("they're paying well", 58),
                        BookLine("and I managed to hire", 59),
                        BookLine("someone to take the", 60),
                        BookLine("sand there, too. Things", 61),
                        BookLine("aren't looking too bad.", 62),
                        BookLine("Just had a word with", 63),
                        BookLine("the sand shifter from", 64),
                        BookLine("Yanille, his name is", 65),
                    ),
                    Page(
                        BookLine("Bert and he's slow.", 66),
                        BookLine("Maybe there's some way", 67),
                        BookLine("I can get him to go", 68),
                        BookLine("faster. Pity I can't", 69),
                        BookLine("fire him but he comes", 70),
                        BookLine("with the contract from", 71),
                        BookLine("Yanille. Oh, well. Had", 72),
                        BookLine("a rather nice lobster", 73),
                        BookLine("for tea, going for a", 74),
                        BookLine("stroll along the beach.", 75),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("25th Fentuary, 168", 55),
                        BookLine("I haven't written here", 56),
                        BookLine("for a while, I was", 57),
                        BookLine("too busy with paperwork.", 58),
                        BookLine("Seem to have another", 59),
                        BookLine("customer up in the", 60),
                        BookLine("Fremen Freminne Freminick", 61),
                        BookLine("Fremmy Province.", 62),
                        BookLine("Also managed to hire", 63),
                        BookLine("a pleasant chap from", 64),
                        BookLine("there who can ship", 65),
                    ),
                    Page(
                        BookLine("the sand. The loan", 66),
                        BookLine("is nearly paid off,", 67),
                        BookLine("but I need to pay", 68),
                        BookLine("the workers as at", 69),
                        BookLine("the moment they're", 70),
                        BookLine("being quite good", 71),
                        BookLine("about the whole thing.", 72),
                        BookLine("I think today I will", 73),
                        BookLine("go have dinner at", 74),
                        BookLine("the pub.", 75),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("14th Septober, 168", 55),
                        BookLine("Loan paid off. Workers", 56),
                        BookLine("getting shifty about", 57),
                        BookLine("pay. Up to my neck in", 58),
                        BookLine("paperwork and Postie", 59),
                        BookLine("Pete delivered a letter", 60),
                        BookLine("from the tax man today.", 61),
                        BookLine("Things not looking good.", 62),
                        BookLine("Going back to bed as", 63),
                        BookLine("it's raining outside.", 64),
                    ),
                    Page(
                        BookLine("15th Septober, 168", 66),
                        BookLine("The rain still keeps", 67),
                        BookLine("coming, as do the demands", 68),
                        BookLine("for money. Luckily I", 69),
                        BookLine("think I managed to balance", 70),
                        BookLine("the books, and the payments", 71),
                        BookLine("just came in for the", 72),
                        BookLine("Yanille and Entrana pits.", 73),
                        BookLine("I have enough to pay", 74),
                        BookLine("the workers now. I wish", 75),
                        BookLine("they'd work longer for", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("less! Getting a bit sick", 55),
                        BookLine("of fish for tea. Might", 56),
                        BookLine("see if I can borrow some", 57),
                        BookLine("of the bananas that seem", 58),
                        BookLine("so abundant around here.", 59),
                        BookLine("12th Ire of Phyrrys, 168", 61),
                        BookLine("Today, while staring", 62),
                        BookLine("out into the rain, I", 63),
                        BookLine("had an idea. I am going", 64),
                        BookLine("to see if I can get a", 65),
                    ),
                    Page(
                        BookLine("wizard to enchant my", 66),
                        BookLine("workers to work harder", 67),
                        BookLine("for less. A simple mind", 68),
                        BookLine("spell should work, depends", 69),
                        BookLine("on how much it will cost,", 70),
                        BookLine("though. Almost got caught", 71),
                        BookLine("stealing bananas", 72),
                        BookLine("yesterday but think", 73),
                        BookLine("I got away with it.", 74),
                        BookLine("18th Ire of Phyrrys,", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("168 Passage booked on", 55),
                        BookLine("the boat; I'm going back", 56),
                        BookLine("to Yanille to see if", 57),
                        BookLine("I can hire a wizard.", 58),
                        BookLine("Of course, it will have", 59),
                        BookLine("to be one of the more", 60),
                        BookLine("gullible ones, them being", 61),
                        BookLine("all honest and benevolent", 62),
                        BookLine("just doesn't help when", 63),
                        BookLine("a businessman needs a", 64),
                        BookLine("simple spell. Write later,", 65),
                    ),
                    Page(
                        BookLine("sun just came out so", 66),
                        BookLine("I'll go see what I can", 67),
                        BookLine("catch to eat.", 68),
                        BookLine("2nd Novtumber, 168", 70),
                        BookLine("I left the business", 71),
                        BookLine("in a good state, able", 72),
                        BookLine("to run itself until mid", 73),
                        BookLine("Novtumber. I think I'm", 74),
                        BookLine("making progress with", 75),
                        BookLine("a wizard I've met in", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("the Dragon Inn where", 55),
                        BookLine("I'm staying in Yanille.", 56),
                        BookLine("It seems he's down on", 57),
                        BookLine("his luck and a bit new", 58),
                        BookLine("to the whole wizard thing.", 59),
                        BookLine("I talked to him about", 60),
                        BookLine("the spell I want this", 61),
                        BookLine("evening. I'm hoping he'll", 62),
                        BookLine("think that this kind", 63),
                        BookLine("of thing is a regular", 64),
                        BookLine("occurrence.", 65),
                    ),
                    Page(
                        BookLine("5th Novtumber, 168", 66),
                        BookLine("I wish I was back", 67),
                        BookLine("on Karamja. It's cold", 68),
                        BookLine("here. But my gullible", 69),
                        BookLine("wizard friend, Clarence,", 70),
                        BookLine("has started on the spell", 71),
                        BookLine("I need. I'm going to", 72),
                        BookLine("try it on Bert, the Yanille", 73),
                        BookLine("sandpit worker before", 74),
                        BookLine("I roll it out to the", 75),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("whole lot of them. Basically,", 55),
                        BookLine("all it does is make him", 56),
                        BookLine("think that it's perfectly", 57),
                        BookLine("normal to be working", 58),
                        BookLine("hugely long hours for", 59),
                        BookLine("very little pay. I'm", 60),
                        BookLine("not quite sure how it", 61),
                        BookLine("works, but I need to", 62),
                        BookLine("work out a new rota for", 63),
                        BookLine("Bert. This should ease", 64),
                        BookLine("the situation with outgoings", 65),
                    ),
                    Page(
                        BookLine("and incomings, maybe", 66),
                        BookLine("I'll have enough money", 67),
                        BookLine("so I don't need to eat", 68),
                        BookLine("fish constantly.", 69),
                        BookLine("14th Novtumber, 168", 71),
                        BookLine("Back on Karamja.", 72),
                        BookLine("It's warmer here,", 73),
                        BookLine("but still quite", 74),
                        BookLine("cold as winter closes", 75),
                        BookLine("in. The spell seems to", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("be working and was well", 55),
                        BookLine("worth the amount I paid", 56),
                        BookLine("for it. I will trial", 57),
                        BookLine("it for the next three", 58),
                        BookLine("months, then see if I", 59),
                        BookLine("can get Clarence to do", 60),
                        BookLine("the same for the other", 61),
                        BookLine("workers. Trout for supper.", 62),
                        BookLine("Urg.", 63),
                        BookLine("1st Moevyng, 169", 65),
                    ),
                    Page(
                        BookLine("Winter seems to be coming", 66),
                        BookLine("to a close, thank the gods.", 67),
                        BookLine("Profits are up - in fact,", 68),
                        BookLine("they're soaring and Yanille", 69),
                        BookLine("is the most profitable", 70),
                        BookLine("sandpit, but with the", 71),
                        BookLine("oldest worker. I am going", 72),
                        BookLine("to see if I can get Clarence", 73),
                        BookLine("to do the same for the", 74),
                        BookLine("Entrana and Freminne", 75),
                        BookLine("Fremen Freminick Fremmy", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("sandpits, too.", 55),
                        BookLine("5th Moevyng, 169", 57),
                        BookLine("Received a letter", 58),
                        BookLine("from Clarence. He appears", 59),
                        BookLine("to be getting a little", 60),
                        BookLine("nervous about that spell", 61),
                        BookLine("that he did for me. I", 62),
                        BookLine("don't think I should", 63),
                        BookLine("ask him to do the other", 64),
                        BookLine("two now. Must convince", 65),
                    ),
                    Page(
                        BookLine("him that it's all perfectly", 66),
                        BookLine("normal. Can't lose those", 67),
                        BookLine("profits. I managed to", 68),
                        BookLine("get a baked potato for", 69),
                        BookLine("lunch...it was divine", 70),
                        BookLine("after so much fish. How", 71),
                        BookLine("do these people live", 72),
                        BookLine("on fish all the time?", 73),
                        BookLine("30th Moevyng, 169", 75),
                        BookLine("Have decided that", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("something needs to", 55),
                        BookLine("be done about Clarence,", 56),
                        BookLine("he's started vague threats", 57),
                        BookLine("in his letters about", 58),
                        BookLine("telling the authorities.", 59),
                        BookLine("Have invited him out", 60),
                        BookLine("here for a holiday...", 61),
                        BookLine("I'll see if I can convince", 62),
                        BookLine("him that everything is", 63),
                        BookLine("fine...but I won't lose", 64),
                        BookLine("these profits, no matter", 65),
                    ),
                    Page(
                        BookLine("what.", 66),
                        BookLine("15th Bennath, 169", 68),
                        BookLine("Pay day for the workers", 69),
                        BookLine("and pay day for me! More", 70),
                        BookLine("money! It's all working", 71),
                        BookLine("nicely. Clarence is due", 72),
                        BookLine("to arrive today, so I", 73),
                        BookLine("must pick him up at the", 74),
                        BookLine("dock. Hopefully I can", 75),
                        BookLine("find something better", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("for lunch than trout.", 55),
                        BookLine("16th Bennath, 169", 57),
                        BookLine("It's becoming", 58),
                        BookLine("apparent that Clarence", 59),
                        BookLine("really is quite the lawful", 60),
                        BookLine("one, and has been reading", 61),
                        BookLine("up on his wizard guild", 62),
                        BookLine("regulations. This could", 63),
                        BookLine("cause me some problems.", 64),
                        BookLine("I did tell him that this", 65),
                    ),
                    Page(
                        BookLine("holiday may be a long", 66),
                        BookLine("one, though, so his guild", 67),
                        BookLine("isn't expecting him back", 68),
                        BookLine("for awhile. I'll see", 69),
                        BookLine("how long I can keep him", 70),
                        BookLine("here, but if he gets", 71),
                        BookLine("difficult, I'm not sure", 72),
                        BookLine("what I'll do. Feed him", 73),
                        BookLine("more fish, I guess.", 74),
                        BookLine("30th Bennath, 169", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("I'm not sure", 55),
                        BookLine("I should have solved", 56),
                        BookLine("the problem of Clarence.", 57),
                        BookLine("I have to find someway", 58),
                        BookLine("of getting rid of the", 59),
                        BookLine("bits now. At least my", 60),
                        BookLine("profits from the Yanille", 61),
                        BookLine("sandpit are really soaring.", 62),
                        BookLine("Hmm... that gives me", 63),
                        BookLine("an idea.", 64),
                    ),
                    Page(
                        BookLine("3rd Raktuber, 169", 66),
                        BookLine("Keep having to remind", 67),
                        BookLine("myself that it's for", 68),
                        BookLine("the good of the business.", 69),
                        BookLine("Profit still rolling", 70),
                        BookLine("in. Problem is, I keep", 71),
                        BookLine("jumping out of my skin", 72),
                        BookLine("every time someone comes", 73),
                        BookLine("in the office. Still,", 74),
                        BookLine("I've nearly disposed", 75),
                        BookLine("of all the evidence now.", 76),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("Quite ironic that some", 55),
                        BookLine("of him went back to Yanille", 56),
                        BookLine("in the last shipment!", 57),
                        BookLine("Thank the gods for that", 58),
                        BookLine("huge vine growing down", 59),
                        BookLine("near Shilo. Guess what...", 60),
                        BookLine("Fish for tea.", 61),
                        BookLine("33rd Raktuber, 169", 63),
                        BookLine("Had a nosey adventurer", 64),
                        BookLine("around today. I think", 65),
                    ),
                    Page(
                        BookLine("I put them off. Hopefully", 66),
                        BookLine("I won't see the likes", 67),
                        BookLine("of them again. Put me", 68),
                        BookLine("back on edge when I was", 69),
                        BookLine("just beginning to relax.", 70),
                        BookLine("Must find another wizard", 71),
                        BookLine("to cast a similar spell", 72),
                        BookLine("on the other two workers", 73),
                        BookLine("as I want more profit", 74),
                        BookLine("in next month.", 75),
                    ),
                ),
                PageSet(
                    Page(
                        BookLine("3rd Pentember, 169", 55),
                        BookLine("I think I just got", 56),
                        BookLine("tricked. I was distracted", 57),
                        BookLine("for a moment when I had", 58),
                        BookLine("someone asking me questions", 59),
                        BookLine("and...it all seems a", 60),
                        BookLine("bit hazy...sounds like", 61),
                        BookLine("there's someone coming,", 62),
                        BookLine("I'll finish off this", 63),
                        BookLine("entry tomo", -64),
                        BookLine("- Sandy Locked diary", 65),
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
        on(Items.UNLOCKED_DIARY_11762, IntType.ITEM, "read") { player, _ ->
            BookInterface.openBook(player, BookInterface.FANCY_BOOK_3_49, ::display)
            return@on true
        }
    }
}
