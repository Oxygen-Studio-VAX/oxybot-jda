package su.gachi.audio

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import net.dv8tion.jda.api.entities.Guild
import su.gachi.core.Client


class AudioManager(val client: Client) {
    val managers = mutableMapOf<String, GuildAudioManager>()
    val playerManager = DefaultAudioPlayerManager()

    init {
        playerManager.registerSourceManager(YoutubeAudioSourceManager())
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault())
        playerManager.registerSourceManager(BandcampAudioSourceManager())
        playerManager.registerSourceManager(VimeoAudioSourceManager())
        playerManager.registerSourceManager(TwitchStreamAudioSourceManager())
        playerManager.registerSourceManager(HttpAudioSourceManager())
        playerManager.setTrackStuckThreshold(5000)
    }

    fun getGuildManager(guild: Guild, channel: String): GuildAudioManager {
        var manager = managers[guild.id]

        if (manager == null) {
            manager = GuildAudioManager(client, guild, channel)
            managers[guild.id] = manager
        }

        return manager
    }

    fun destroyManager(guild: Guild) {
        val manager = managers[guild.id]
        if (manager != null) {
            manager.link.resetPlayer()
            manager.link.destroy()
            managers.remove(guild.id)
        }
    }
}