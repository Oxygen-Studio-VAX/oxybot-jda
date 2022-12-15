package su.gachi.core

import io.github.cdimascio.dotenv.dotenv
import lavalink.client.io.jda.JdaLavalink
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory
import su.gachi.Config
import su.gachi.audio.AudioManager
import su.gachi.core.commands.CommandManager
import su.gachi.listeners.client.ReadyListener
import su.gachi.listeners.interactions.SlashCommandAutocomplete
import su.gachi.listeners.interactions.SlashCommandsListener
import su.gachi.services.DatabaseService
import su.gachi.services.LocaleService
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Client {
    val dotenv = dotenv()
    val lavalink = JdaLavalink(1)
    val shardManager = DefaultShardManagerBuilder.createDefault(dotenv["DISCORD_TOKEN"])
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES)
        .disableCache(CacheFlag.FORUM_TAGS, CacheFlag.SCHEDULED_EVENTS, CacheFlag.ACTIVITY, CacheFlag.STICKER)
        .setStatus(OnlineStatus.DO_NOT_DISTURB)
        .addEventListeners(ReadyListener(this), lavalink, SlashCommandsListener(this), SlashCommandAutocomplete())
        .setActivity(Activity.playing("loading..."))
        .setVoiceDispatchInterceptor(lavalink.voiceInterceptor)
        .build()
    val threadpool = Executors.newScheduledThreadPool(100) { r: Runnable? ->
        Thread(
            r,
            "discord-bot"
        )
    }
    val usersCount = mutableMapOf<Int, Long>()
    val localeService = LocaleService()
    val commandManager = CommandManager(this)
    val databaseService = DatabaseService(this)
    val audioManager = AudioManager(this)

    init {
        threadpool.scheduleWithFixedDelay({ countUsers() }, 10, 30, TimeUnit.SECONDS)
        threadpool.scheduleWithFixedDelay({ daycycleCategoryChanger() }, 10, 300, TimeUnit.SECONDS)

        RestAction.setDefaultSuccess { LoggerFactory.getLogger("API").debug("Success RestAction") }
        RestAction.setDefaultFailure { err -> LoggerFactory.getLogger("API").error("RestAction error: ${err.message}") }
    }

    private fun countUsers() {
        shardManager.shards.forEach { shard ->
            usersCount[shard.shardInfo.shardId] = 0
            shard.guilds.forEach { guild ->
                usersCount[shard.shardInfo.shardId] = usersCount[shard.shardInfo.shardId]!! + guild.memberCount
            }
        }
    }

    private fun daycycleCategoryChanger() {
        val category = shardManager.getCategoryById(Config.daycycleCategory) ?: return

        var emoji = "🌃"
        if (LocalDateTime.now().hour in 10..18)
            emoji = "🌄"

        if (!category.name.startsWith(emoji))
            category.manager.setName("$emoji Добро Пожаловать $emoji")
    }
}