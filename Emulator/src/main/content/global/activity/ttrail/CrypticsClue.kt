package content.global.activity.ttrail

enum class CrypticsClue(
    val riddle: String,
    val key: Boolean,
    val puzzle: Boolean,
) {
    WILDERNESS_SAPPHIRE(
        "46 is my number, my body is the colour of burnt orange and crawls among those with eight. Three mouths I have, yet I cannot eat. My blinking blue eye hides my grave.",
        false,
        false,
    ),
    ABBOT_LANGLEY(
        "'A bag belt only?' he asked his balding brothers",
        false,
        false,
    ),
    EAST_ARDOUGNE_CRATE(
        "A crate found in the tower of a church is your next stop.",
        false,
        false,
    ),
    OZIACH_ARMOR_SELLER(
        "A strange little man who sells armour only to those who've proven themselves to be unafraid of dragons.",
        false,
        true,
    ),
    AGGIE_HOUSE_DRAYNOR(
        "Aggie I see, Lonely and southern I feel, I am neither inside nor outside the house, yet no house would be complete without me. The treasure lies beneath me!",
        false,
        false,
    ),
    ALMERA_HOUSE(
        "A great view - watch the rapidly drying hides get splashed. Check the box you are sitting on.",
        false,
        false,
    ),
    CANIFIS_CLOTHES_SHOP(
        "A town with a different sort of night-life is your destination. Search for some crates in one of the houses.",
        false,
        false,
    ),
    ETCETERIA_EVERGREEN(
        "And so on, and so on, and so on. Walking from the land of many unimportant things leads to a choice of paths.",
        false,
        false,
    ),
    ZANARIS_FORGE(
        "After a hard slays spraying back the vegetation, why not pop off to the nearby forge and search the crates?",
        false,
        false,
    ),
    PORT_KHAZARD_ANVIL(
        "After trawling for bars, go to the nearest place and smith them and dig by the door.",
        false,
        false,
    ),
    NARDAH_FLYING_RUG(
        "As you desert this town, keep an eye out for a set of spines that could ruin nearby rugs: dig carefully around the greenery",
        false,
        false,
    ),
    WEST_ARDOUGNE_PRISON(
        "Being this far north has meant that these crates have escaped being battled over.",
        false,
        false,
    ),
    SANDSTONE_GRANITE_QUARRY(
        "Brush off the sand and dig in the quarry. There is a wheely handy barrow to the east. Don't worry, it's coal to dig there—in fact, it's all oclay.",
        false,
        false,
    ),
    PORT_PHASMATYS_TREE(
        "By the town of the dead, walk south down a rickety bridge, then dig near the slime-covered tree.",
        false,
        false,
    ),
    CITRIC_CELLAR(
        "Citric Cellar",
        false,
        true,
    ),
    EDGEVILLE_YEW_TREE(
        "Come to the evil ledge, Yew know yew want to, And try not to get stung.",
        false,
        false,
    ),
    MORTTON_CIRCLE_SHADOWS(
        "Covered in shadows, the centre of the circle is where you will find the answer.",
        false,
        false,
    ),
    POLLNIVNEACH_WELL(
        "Dig here if you aren't feeling too well after travelling through the desert. Ali heartily recommends it.",
        false,
        false,
    ),
    MUDSKIPPER_POINT(
        "Don't skip here, it's too muddy. You'll feel like a star if you dig here, though.",
        false,
        false,
    ),
    BAIXTORIAN_FALLS_HAY(
        "Hay! Stop for a bit and admire the scenery, just like the tourism promoter says.",
        false,
        false,
    ),
    BLUE_MOON_INN(
        "Find a bar with a centre fountain in its city. Go upstairs and get changed.",
        true,
        false,
    ),
    MONASTERY_MONKS(
        "Find a crate close to the monks that like to paaarty!",
        false,
        false,
    ),
    MAGE_TRAINING_BOOKCASE(
        "For any aspiring mage, I'm sure searching this bookcase will be a rewarding experience.",
        false,
        false,
    ),
    LUMBRIDGE_WINDMILL(
        "Four blades I have yet I draw no blood. But I mash and turn my victims to powder. Search in my head, search in my rafters, Where my blades are louder.",
        false,
        false,
    ),
    GENERAL_BENTNOZE(
        "Generally speaking, his nose was very bent",
        false,
        true,
    ),
    DUNSTAN_BURTHORPE(
        "Go to the village being attacked by trolls, search the drawers while you are there.",
        true,
        false,
    ),
    LIGHTHOUSE_DRAWERS(
        "Go to this building to be illuminated, and search the drawers while you're there.",
        true,
        false,
    ),
    LUMBRIDGE_CASTLE_SPINNING_WHEEL(
        "My home is grey and made of stone, A castle with a search for a meal, Hidden in some drawers I am, Across from a wooden wheel.",
        false,
        false,
    ),
    TAI_BWO_WANNAI_CRATES(
        "In a village made of bamboo look for some crates under one of the houses.",
        false,
        false,
    ),
    DUNGEON_CHEST(
        "This temple is rather sluggish. The chest just inside the entrance, however, is filled with goodies.",
        false,
        false,
    ),
    SHILO_VILLAGE_BOOKCASE(
        "This village has a problem with cartloads of the undead. Try checking the bookcase to find the answer.",
        false,
        false,
    ),
    NECROMANCER_TOWER(
        "Throat mage seeks companionship. Seek answers inside my furniture if interested.",
        false,
        false,
    ),
    FISHING_PLATFORM_CRATE(
        "Try not to step on any aquatic nasties while searching this crate.",
        false,
        false,
    ),
    ENTRANA_DRAWERS(
        "When no weapons are at hand, now is the time to reflect. In Saradomin's name, redemption draws closer...",
        false,
        false,
    ),
    YANILLE_POISON_SPIDERS(
        "When you get tired of fighting, go deep, deep down until you need an antidote",
        false,
        false,
    ),
    RIMMINGTON_STAINED_GLASS(
        "While a sea view is nice, it seems this church has not seen visitors in a while. Dig outside the rim of the window for a reward.",
        false,
        false,
    ),
    BARBARIAN_ASSAULT_SHELL(
        "You don't need to go hopping mad - or take steps - to get to this treasure: just be totally shellfish.",
        false,
        false,
    ),
    NECROMANCER_HOME_DRAWERS(
        "Throat mage seeks companionship. Seek answers inside my furniture if interested.",
        false,
        false,
    ),
    ELEMENTAL_WORKSHOP_CRATES(
        "You have all of the elements available to solve this clue. Fortunately you do not have to go so far as to stand in a draft.",
        false,
        false,
    ),
    VARROCK_CHEST(
        "You'll need to look for a town with a central fountain. Look for a locked chest in the town's chapel.",
        true,
        false,
    ),
    LUMBRIDGE_CRATES(
        "You will need to under-cook to solve this one.",
        false,
        false,
    ),
    BRAINEDEATH_ISLAND_LAKE(
        "You will need to wash the old ash off of your spade when you dig here, but the only water nearby is stagnant.",
        false,
        false,
    ),
    DIGSITE_DOUG_DEEP(
        "You'll need to have Doug Deep into the distant past to get to these sacks.",
        false,
        false,
    ),
    CAPTAIN_KLEMFOODLE(
        "You can cook food on me, but don't cook any foodles - That would be just wrong.",
        false,
        false,
    ),
    LUBUFU_BRIMHAVEN(
        "The owner of this crate has a hunch that he put more than fish inside.",
        false,
        false,
    ),
}
