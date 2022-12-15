package su.gachi.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import lavalink.client.player.IPlayer
import lavalink.client.player.LavalinkPlayer
import lavalink.client.player.event.PlayerEventListenerAdapter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import su.gachi.Config
import su.gachi.core.Client
import su.gachi.core.settings.GuildSettings
import java.util.concurrent.LinkedBlockingDeque


class TrackScheduler(val client: Client, val guild: Guild, val player: LavalinkPlayer, val channel: String) :
    PlayerEventListenerAdapter() {
    var queue: LinkedBlockingDeque<AudioTrack> = LinkedBlockingDeque()
    var looping = false
    var queueLooping = false
    var nowPlayingMessage: Message? = null

    fun addToQueue(track: AudioTrack) {
        queue.offer(track)

        if (player.playingTrack == null)
            nextTrack()
    }

    fun nextTrack() {
        val track = queue.poll()

        if (track != null) {
            player.playTrack(track)

            try {
                if (nowPlayingMessage != null)
                    nowPlayingMessage!!.delete().queue()
            } catch (ignore: Exception) {
            }
            try {
                val channel = guild.getTextChannelById(channel)
                if (channel != null) {
                    val settings = GuildSettings(client, guild)
                    var trackImage: String? = null

                    if (track.sourceManager.sourceName == "youtube")
                        trackImage = "https://img.youtube.com/vi/${track.identifier}/hqdefault.jpg"

                    channel.sendMessageEmbeds(
                        EmbedBuilder()
                            .setColor(Config.embedColor)
                            .setTitle(client.localeService.translate("player.np.title", settings.general.locale ?: Config.defaultLocale))
                            .setDescription("[${track.info.title}](${track.info.uri}) [<@${track.userData}>]")
                            .setThumbnail(trackImage)
                            .build()
                    ).queue { message -> nowPlayingMessage = message }
                }
            } catch (ignore: Exception) {
            }
        } else {
            client.audioManager.destroyManager(guild)
        }
    }

    fun shuffle() {
        val list = queue.shuffled() as MutableList<AudioTrack>
        val newQueue = LinkedBlockingDeque<AudioTrack>()
        var track = list.removeFirstOrNull()

        while (track != null) {
            newQueue.add(track)
            track = list.removeFirstOrNull()
        }

        queue = newQueue
    }

    override fun onTrackEnd(player: IPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) {
            if (queueLooping) {
                queue.offer(track.makeClone())
            } else if (looping) {
                queue.addFirst(track.makeClone())
            }

            nextTrack()
        }
    }
}