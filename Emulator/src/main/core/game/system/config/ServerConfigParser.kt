package core.game.system.config

import com.moandjiezana.toml.Toml
import core.ServerConfig
import core.api.log
import core.api.parseEnumEntry
import core.game.world.GameSettings
import core.game.world.GameWorld
import core.game.world.map.Location
import core.tools.Log
import core.tools.LogLevel
import core.tools.integration.mysql.Database
import java.io.File
import java.net.URL
import kotlin.system.exitProcess

object ServerConfigParser {
    var confFile: File? = null
    var tomlData: Toml? = null

    fun parse(path: String) {
        confFile = File(parsePath(path))
        parseFromFile(confFile)
    }

    fun parse(path: URL?) {
        confFile = File(path!!.toURI())
        parseFromFile(confFile)
    }

    private fun parseFromFile(confFile: File?) {
        if (!confFile!!.canonicalFile.exists()) {
            log(this::class.java, Log.ERR, "${confFile.canonicalFile} does not exist.")
            exitProcess(0)
        } else {
            try {
                tomlData = Toml().read(confFile)
                parseServerSettings()
                parseGameSettings()
                val jvmString = System.getProperty("java.version")
                if (jvmString.startsWith("1.")) {
                    ServerConfig.JAVA_VERSION = jvmString.substring(2, 3).toInt()
                } else if (!jvmString.startsWith("1.")) {
                    ServerConfig.JAVA_VERSION = jvmString.substring(0, 2).toInt()
                } else if (!jvmString.contains(".")) {
                    ServerConfig.JAVA_VERSION = jvmString.toInt()
                }
                log(this::class.java, Log.FINE, "It seems we are in a Java ${ServerConfig.JAVA_VERSION} environment.")
            } catch (e: java.lang.IllegalStateException) {
                log(this::class.java, Log.ERR, "Passed config file is not a TOML file. Path: ${confFile.canonicalPath}")
                log(this::class.java, Log.ERR, "Exception received: $e")
                exitProcess(0)
            }
        }
    }

    private fun parseGameSettings() {
        tomlData ?: return
        val data = tomlData!!

        GameWorld.settings =
            GameSettings(
                name = ServerConfig.SERVER_NAME,
                isBeta = data.getBoolean("world.debug"),
                isDevMode = data.getBoolean("world.dev"),
                isGui = data.getBoolean("world.start_gui"),
                worldId = data.getString("world.world_id").toInt(),
                countryIndex = data.getString("world.country_id").toInt(),
                activity = data.getString("world.activity"),
                isMembers = data.getBoolean("world.members"),
                isPvp = data.getBoolean("world.pvp"),
                isQuickChat = false,
                isLootshare = false,
                msAddress = data.getString("server.msip"),
                default_xp_rate = data.getDouble("world.default_xp_rate"),
                // allow_slayer_reroll = data.getBoolean("world.allow_slayer_reroll"),
                enable_default_clan = data.getBoolean("world.enable_default_clan"),
                enable_bots = data.getBoolean("world.enable_bots"),
                autostock_ge = data.getBoolean("world.autostock_ge"),
                allow_token_purchase = data.getBoolean("world.allow_token_purchase"),
                // skillcape_perks = data.getBoolean("world.skillcape_perks"),
                increased_door_time = data.getBoolean("world.increased_door_time"),
                enabled_botting = data.getBoolean("world.enable_botting"),
                max_adv_bots = data.getLong("world.max_adv_bots").toInt(),
                enable_doubling_money_scammers = data.getBoolean("world.enable_doubling_money_scammers", false),
                wild_pvp_enabled = data.getBoolean("world.wild_pvp_enabled"),
                jad_practice_enabled = data.getBoolean("world.jad_practice_enabled"),
                ge_announcement_limit = data.getLong("world.ge_announcement_limit", 500L).toInt(),
                smartpathfinder_bfs = data.getBoolean("world.smartpathfinder_bfs", false),
                enable_castle_wars = data.getBoolean("world.enable_castle_wars", false),
                message_model = data.getString("world.motw_identifier").toInt(),
                message_string = data.getString("world.motw_text").replace("@name", ServerConfig.SERVER_NAME),
            )
    }

    private fun parseServerSettings() {
        tomlData ?: return
        val data = tomlData!!

        ServerConfig.DATA_PATH = data.getString("paths.data_path")
        ServerConfig.WRITE_LOGS = data.getBoolean("server.write_logs")
        ServerConfig.DATABASE_NAME = data.getString("database.database_name")
        ServerConfig.DATABASE_USER = data.getString("database.database_username")
        ServerConfig.DATABASE_PASS = data.getString("database.database_password")
        ServerConfig.DATABASE_ADDRESS = data.getString("database.database_address")
        ServerConfig.DATABASE_PORT = data.getString("database.database_port")
        ServerConfig.DATABASE =
            Database(
                ServerConfig.DATABASE_ADDRESS + ":" + ServerConfig.DATABASE_PORT,
                ServerConfig.DATABASE_NAME,
                ServerConfig.DATABASE_USER,
                ServerConfig.DATABASE_PASS,
            )
        ServerConfig.CACHE_PATH = data.getPath("paths.cache_path")
        ServerConfig.CONFIG_PATH = data.getPath("paths.configs_path")
        ServerConfig.PLAYER_SAVE_PATH = data.getPath("paths.save_path")
        ServerConfig.STORE_PATH = data.getPath("paths.store_path")
        ServerConfig.RDT_DATA_PATH = data.getPath("paths.rare_drop_table_path")
        ServerConfig.OBJECT_PARSER_PATH = data.getPath("paths.object_parser_path")
        ServerConfig.LOGS_PATH = data.getPath("paths.logs_path")
        ServerConfig.SERVER_NAME = data.getPath("world.name")
        ServerConfig.BOT_DATA_PATH = data.getPath("paths.bot_data")
        ServerConfig.MS_SECRET_KEY = data.getString("server.secret_key")
        ServerConfig.HOME_LOCATION = parseLocation(data.getString("world.home_location"))
        ServerConfig.START_LOCATION = parseLocation(data.getString("world.new_player_location"))
        ServerConfig.DAILY_RESTART = data.getBoolean("world.daily_restart")
        ServerConfig.LOG_CUTSCENE = data.getBoolean("world.verbose_cutscene", false)
        ServerConfig.GRAND_EXCHANGE_DATA_PATH = data.getPath("paths.eco_data")
        ServerConfig.CELEDT_DATA_PATH = data.getPath("paths.cele_drop_table_path")
        ServerConfig.USDT_DATA_PATH = data.getPath("paths.uncommon_seed_drop_table_path")
        ServerConfig.HDT_DATA_PATH = data.getPath("paths.herb_drop_table_path")
        ServerConfig.GDT_DATA_PATH = data.getPath("paths.gem_drop_table_path")
        ServerConfig.RSDT_DATA_PATH = data.getPath("paths.rare_seed_drop_table_path")
        ServerConfig.ASDT_DATA_PATH = data.getPath("paths.allotment_seed_drop_table_path")
        ServerConfig.SERVER_GE_NAME = data.getString("world.name_ge") ?: ServerConfig.SERVER_NAME
        ServerConfig.BOTS_INFLUENCE_PRICE_INDEX = data.getBoolean("world.bots_influence_ge_price", true)
        ServerConfig.PRELOAD_MAP = data.getBoolean("server.preload_map", false)
        ServerConfig.REVENANT_POPULATION = data.getLong("world.revenant_population", 30L).toInt()
        ServerConfig.BANK_BOOTH_QUICK_OPEN = data.getBoolean("world.bank_booth_quick_open", false)
        ServerConfig.BANK_BOOTH_NOTE_ENABLED = data.getBoolean("world.bank_booth_note_enabled", true)
        ServerConfig.BANK_BOOTH_NOTE_UIM = data.getBoolean("world.bank_booth_note_uim", true)
        ServerConfig.DISCORD_GE_WEBHOOK = data.getString("integrations.discord_ge_webhook", "")
        ServerConfig.WATCHDOG_ENABLED = data.getBoolean("server.watchdog_enabled", true)
        ServerConfig.I_AM_A_CHEATER = data.getBoolean("world.i_want_to_cheat", false)
        ServerConfig.USE_AUTH = data.getBoolean("server.use_auth", true)
        ServerConfig.PERSIST_ACCOUNTS = data.getBoolean("server.persist_accounts", true)
        ServerConfig.DAILY_ACCOUNT_LIMIT = data.getLong("server.daily_accounts_per_ip", 3L).toInt()
        ServerConfig.DISCORD_MOD_WEBHOOK = data.getString("integrations.discord_moderation_webhook", "")
        ServerConfig.NOAUTH_DEFAULT_ADMIN = data.getBoolean("server.noauth_default_admin", false)
        // Configuration.DRAGON_AXE_USE_OSRS_SPEC = data.getBoolean("world.dragon_axe_use_osrs_spec", false)
        ServerConfig.DISCORD_OPENRSC_HOOK = data.getString("integrations.openrsc_integration_webhook", "")
        ServerConfig.ENABLE_GLOBALCHAT = data.getBoolean("world.enable_globalchat", true)
        ServerConfig.MAX_PATHFIND_DISTANCE = data.getLong("server.max_pathfind_dist", 25L).toInt()
        ServerConfig.IRONMAN_ICONS = data.getBoolean("world.ironman_icons", false)
        ServerConfig.PLAYER_STOCK_CLEAR_INTERVAL = data.getLong("world.playerstock_clear_mins", 180L).toInt()
        ServerConfig.PLAYER_STOCK_RECIRCULATE = data.getBoolean("world.playerstock_bot_offers", true)
        ServerConfig.BOTSTOCK_LIMIT = data.getLong("world.botstock_limit", 5000L).toInt()
        ServerConfig.BETTER_AGILITY_PYRAMID_GP = data.getBoolean("world.better_agility_pyramid_gp", true)
        ServerConfig.GRAFANA_PATH = data.getPath("integrations.grafana_log_path")
        ServerConfig.GRAFANA_LOGGING = data.getBoolean("integrations.grafana_logging", false)
        ServerConfig.GRAFANA_TTL_DAYS = data.getLong("integrations.grafana_log_ttl_days", 7L).toInt()
        ServerConfig.BETTER_DFS = data.getBoolean("world.better_dfs", true)
        ServerConfig.HOLIDAY_EVENT_RANDOMS = data.getBoolean("world.holiday_event_randoms", true)
        ServerConfig.FORCE_HALLOWEEN_EVENTS = data.getBoolean("world.force_halloween_randoms", false)
        ServerConfig.FORCE_CHRISTMAS_EVENTS = data.getBoolean("world.force_christmas_randoms", false)
        ServerConfig.FORCE_EASTER_EVENTS = data.getBoolean("world.force_easter_randoms", false)
        ServerConfig.RUNECRAFTING_FORMULA_REVISION = data.getLong("world.runecrafting_formula_revision", 581).toInt()
        ServerConfig.CONNECTIVITY_CHECK_URL = data.getString("server.connectivity_check_url", "https://google.com")
        ServerConfig.CONNECTIVITY_TIMEOUT = data.getLong("server.connectivity_timeout", 500L).toInt()

        val logLevel = data.getString("server.log_level", "VERBOSE").uppercase()
        ServerConfig.LOG_LEVEL = parseEnumEntry<LogLevel>(logLevel) ?: LogLevel.VERBOSE
    }

    private fun Toml.getPath(key: String): String {
        try {
            return parsePath(getString(key, "@data/").replace("@data", ServerConfig.DATA_PATH!!))
        } catch (e: Exception) {
            log(this::class.java, Log.ERR, "Error parsing key: $key")
            exitProcess(0)
        }
    }

    fun parseLocation(locString: String): Location {
        val locTokens = locString.split(",").map { it.toInt() }
        return Location(locTokens[0], locTokens[1], locTokens[2])
    }

    fun parsePath(pathString: String): String {
        var pathTokens: List<String>? = null
        if (pathString.contains("/")) {
            pathTokens = pathString.split("/")
        } else if (pathString.contains("\\")) {
            pathTokens = pathString.split("\\")
        }

        pathTokens ?: return pathString
        var pathProduct = ""
        for (token in pathTokens) {
            if (token != "") {
                pathProduct += "$token${File.separator}"
            }
        }

        return pathProduct
    }
}
