package content.data

internal object GameAttributes {
    const val SAVE = "/save:"
    const val JOIN_DATE = "joinDate"
    const val TUTORIAL_COMPLETE = "tutorial:complete"
    const val TUTORIAL_STAGE = "/save:tutorial:stage"

    const val PRAYER_LOCK = "/save:prayer:lock"

    const val CON = "con"
    const val PORTAL = "portal"
    const val CON_HOTSPOT = "con:hotspot"
    const val POH_PORTAL = "construction:portal"
    const val CON_OBJ = "construction:objs"
    const val CON_CLOCKMAKER_DIAL = "construction:clockbench"
    const val CON_PORTAL_DIR = "construction:direct_portal"
    const val CON_REMOVE_DECO = "construction:remove_decoration"
    const val CON_TOOLS = "construction:tools"
    const val CON_ROOM = "construction:room"
    const val CON_REMOVE = "construction:remove_dialogue"
    const val CON_LAST_TASK = "construction:servant_last_task"
    const val CON_TASK = "construction:servant_task"
    const val CON_FLATPACK_TIER = "construction:flatpack:tier"

    const val FAMILY_CREST = "/save:construction:family_crest"

    const val RE = "random"
    const val RE_PAUSE = "random:pause"
    const val RE_REWARD = "random:reward"

    const val RE_PRISON_1 = "/save:random:prison_pete_start"
    const val RE_PRISON_2 = "/save:random:prison_pete_index"
    const val RE_PRISON_3 = "/save:random:prison_pete_interactions"
    const val RE_PRISON_4 = "/save:random:prison_pete_wrong"

    const val RE_PINBALL_START = "/save:random:pinball_start"
    const val RE_PINBALL_INTER = "/save:random:pinball_interactions"
    const val RE_PINBALL_OBJ = "/save:random:pinball:objs"

    const val RE_PATTERN_INDEX = "random:pattern_index"
    const val RE_PATTERN_CORRECT = "random:pattern_correct"
    const val RE_PATTERN_OBJ = "random:pattern:objs"

    const val RE_FREAK_TASK = "/save:random:freaky_forester:task"
    const val RE_FREAK_COMPLETE = "/save:random:freaky_forester_complete"
    const val RE_FREAK_KILLS = "/save:random:freaky_forester:kills"

    const val RE_PILLORY_KEYS = "/save:random:pillory_item_1"
    const val RE_PILLORY_PADLOCK = "/save:random:pillory_item_2"
    const val RE_PILLORY_CORRECT = "/save:random:pillory_correct"
    const val RE_PILLORY_TARGET = "/save:random:pillory_target"
    const val RE_PILLORY_SCORE = "/save:random:pillory_score"

    const val RE_TWIN_START = "/save:random:5:start"
    const val RE_TWIN_DIAL = "/save:random:evil_twin_dialogue"
    const val RE_TWIN_OBJ_DIAL = "/save:random:evil_twin:objs_dialogue"
    const val RE_TWIN_OBJ_LOC_X = "/save:random:evil_twin_loc:x"
    const val RE_TWIN_OBJ_LOC_Y = "/save:random:evil_twin_loc:y"

    const val RE_BOB_START = "/save:random:evil_bob_start"
    const val RE_BOB_COMPLETE = "/save:random:evil_bob_complete"
    const val RE_BOB_ZONE = "/save:random:evil_bob_zone"
    const val RE_BOB_SCORE = "/save:random:evil_bob_score"
    const val RE_BOB_ALERT = "/save:random:evil_bob_temporary"
    const val RE_BOB_DIAL = "/save:random:evil_bob_dialogue"
    const val RE_BOB_DIAL_INDEX = "/save:random:evil_bob_index"
    const val RE_BOB_OBJ = "/save:random:evil_bob:objs"

    const val RE_MIME_EMOTE = "/save:random:mime_emote"
    const val RE_MIME_INDEX = "/save:random:mime_index"
    const val RE_MIME_CORRECT = "/save:random:mime_correct"
    const val RE_MIME_WRONG = "/save:random:mime_wrong"

    const val RE_QUIZ_REWARD = "/save:quiz:random_reward"
    const val RE_QUIZ_SCORE = "/save:quiz:score"

    const val S_LADY_ITEM = "random:sandwich_lady:item"
    const val S_LADY_ITEM_VALUE = "random:sandwich_lady:item_value"

    const val PLANT_NPC = "/save:random:plant"
    const val PLANT_NPC_VALUE = "/save:random:fruits"

    const val CERTER_REWARD = "random:certer_reward"
    const val CERTER_CORRECT = "random:certer_stage"
    const val CERTER_INDEX = "random:certer_index"

    const val DRILL_OFFSET = "/save:random:drill_offset"
    const val DRILL_TASK = "/save:random:drill_task"
    const val DRILL_COUNTER = "/save:random:drill_score"

    const val MINI_PURPLE_CAT = "/save:mini-quest:purple-cat"
    const val MINI_PURPLE_CAT_COMPLETE = "/save:mini-quest:purple-cat:complete"
    const val MINI_PURPLE_CAT_PROGRESS = "/save:mini-quest:purple-cat:score"

    const val QUEST_SWEPT_AWAY = "quest:swept-away"
    const val QUEST_SWEPT_AWAY_HETTY_ENCH = "/save:quest:swept-away:hetty-enchantment"
    const val QUEST_SWEPT_AWAY_BETTY_ENCH = "/save:quest:swept-away:betty-enchantment"
    const val QUEST_SWEPT_AWAY_LINE = "/save:quest:sweep-away:line"
    const val QUEST_SWEPT_AWAY_BETTY_WAND = "quest:swept-away:item"
    const val QUEST_SWEPT_AWAY_CREATURE_INTER = "/save:quest:sweep-away:creature_interactions"
    const val QUEST_SWEPT_AWAY_LABELS = "/save:quest:swept-away:labels"
    const val QUEST_SWEPT_AWAY_LOTTIE = "/save:quest:sweep-away:lottie-puzzle"
    const val QUEST_SWEPT_AWAY_LABELS_COMPLETE = "/save:quest:swept-away:labels:complete"

    const val TALK_ABOUT_SQ_IRKJUICE = "/save:broomstick:osman-talk"
    const val BROOM_ENCHANTMENT_TP = "/save:broomstick:teleport"

    const val PHOENIX_LAIR_VISITED = "/save:location:phoenix-lair:visit"
    const val TALK_WITH_PRIEST = "/save:quest:phoenix:talk-with-priest"

    const val QUEST_TOG_LAST_DATE = "/save:quest:tog:last_date"
    const val QUEST_TOG_LAST_XP_AMOUNT = "/save:quest:tog:xp_amount"
    const val QUEST_TOG_LAST_QP = "/save:quest:tog:last_qp"

    const val QUEST_IKOV_SELECTED_END = "/save:quest:ikov:end"
    const val QUEST_IKOV_DISABLED_TRAP = "/save:quest:ikov:disabled_trap"
    const val QUEST_IKOV_WINELDA_INTER = "/save:quest:ikov:winelda_interactions"
    const val QUEST_IKOV_BRIDGE_INTER = "/save:quest:ikov:bridge_interactions"
    const val QUEST_IKOV_ICE_CHAMBER_ACCESS = "/save:quest:ikov:ice_chamber_access"
    const val QUEST_IKOV_ICE_ARROWS = "/save:quest:ikov:ice_arrows"
    const val QUEST_IKOV_CHEST_INTER = "quest:ikov:chest_interactions"
    const val QUEST_IKOV_WARRIOR_INST = "quest:ikov:warrior_instance"

    const val QUEST_HFTD_LIGHTHOUSE_MECHANISM = "/save:hftd:lighthouse-fixed"
    const val QUEST_HFTD_UNLOCK_BRIDGE = "/save:hftd:lighthouse-bridge"
    const val QUEST_HFTD_STRANGE_WALL_DISCOVER = "/save:hftd:strange-wall"
    const val QUEST_HFTD_UNLOCK_DOOR = "/save:hftd:item-placed"

    const val QUEST_HFTD_USE_AIR_RUNE = "/save:hftd:air"
    const val QUEST_HFTD_USE_FIRE_RUNE = "/save:hftd:fire"
    const val QUEST_HFTD_USE_EARTH_RUNE = "/save:hftd:earth"
    const val QUEST_HFTD_USE_WATER_RUNE = "/save:hftd:water"
    const val QUEST_HFTD_USE_ARROW = "/save:hftd:arrow"
    const val QUEST_HFTD_USE_SWORD = "/save:hftd:sword"

    const val QUEST_BKF_DOSSIER_INTER = "/save:quest:fortress:read_dossier"
    const val WOLF_WHISTLE_STIKKLEBRIX = "/save:quest:wolf:searched-body"

    const val ACTIVITY_PENGUINS_HNS = "/save:phns:spy-on:enabled"
    const val ACTIVITY_PENGUINS_HNS_SCORE = "/save:phns:points"

    const val ITEM_AVA_DEVICE = "item:ava-device:burping"

    const val ITEM_TOY_MOUSE_RELEASE = "item:released:toy-mouse"

    const val RC_GUILD_TALISMAN = "/save:rcguild:shown_talisman"
    const val RC_GUILD_TALISMAN_TASK_START = "/save:rcguild:talisman_task"
    const val RC_GUILD_TALISMAN_TASK_COMPLETE = "/save:rcguild:omni-access"
}
