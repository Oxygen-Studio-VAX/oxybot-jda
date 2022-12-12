package su.gachi.core

import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory
import su.gachi.services.LocaleService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Client {
    val dotenv = dotenv()
    val shardManager = DefaultShardManagerBuilder.createDefault(dotenv["DISCORD_TOKEN"])
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES)
        .disableCache(CacheFlag.FORUM_TAGS, CacheFlag.SCHEDULED_EVENTS, CacheFlag.ACTIVITY, CacheFlag.STICKER)
        .setStatus(OnlineStatus.DO_NOT_DISTURB)
        .setActivity(Activity.playing("loading..."))
        .build()
    val threadpool = Executors.newScheduledThreadPool(100) { r: Runnable? ->
        Thread(
            r,
            "discord-bot"
        )
    }
    val usersCount = mutableMapOf<Int, Long>()
    val localeService = LocaleService()

    init {
        threadpool.scheduleWithFixedDelay({ countUsers() }, 10, 30, TimeUnit.SECONDS)

        RestAction.setDefaultSuccess { LoggerFactory.getLogger("API").debug("Success RestAction") }
        RestAction.setDefaultFailure { err -> LoggerFactory.getLogger("API").error("RestAction error: ${err.message}") }
    }

    fun countUsers() {
        shardManager.shards.forEach { shard ->
            usersCount[shard.shardInfo.shardId] = 0
            shard.guilds.forEach { guild ->
                usersCount[shard.shardInfo.shardId] = usersCount[shard.shardInfo.shardId]!! + guild.memberCount
            }
        }
    }
}