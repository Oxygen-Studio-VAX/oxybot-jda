package su.gachi.audio

import net.dv8tion.jda.api.entities.Guild
import su.gachi.core.Client

class GuildAudioManager(val client: Client, val guild: Guild, val channel: String) {
    val link = client.lavalink.getLink(guild)
    val scheduler = TrackScheduler(client, guild, link.player, channel)

    init {
        link.player.addListener(scheduler)
    }
}