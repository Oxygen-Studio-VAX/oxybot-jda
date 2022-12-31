package su.gachi.listeners.guilds

import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import su.gachi.core.Client

class GuildsFlowListener(val client: Client) : ListenerAdapter() {
    override fun onGuildJoin(event: GuildJoinEvent) {
        client.shardManager.setPresence(OnlineStatus.ONLINE, Activity.watching("${event.jda.guilds.size} servers"))
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        client.shardManager.setPresence(OnlineStatus.ONLINE, Activity.watching("${event.jda.guilds.size} servers"))
    }
}