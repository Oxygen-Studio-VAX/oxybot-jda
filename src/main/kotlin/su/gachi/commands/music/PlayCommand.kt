package su.gachi.commands.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import su.gachi.Config
import su.gachi.core.commands.Command
import su.gachi.core.commands.CommandContext
import java.net.URL
import java.text.SimpleDateFormat

class PlayCommand : Command() {
    init {
        name = "play"
        description = "Play song from YouTube or other source"

        options = listOf(
            OptionData(OptionType.STRING, "query", "Play query (Track title or URL)", true)
                .setNameLocalization(DiscordLocale.RUSSIAN, "запрос")
                .setDescriptionLocalization(DiscordLocale.RUSSIAN, "Запрос проигрывания (Название или ссылка)")
        )
    }

    override fun handle(ctx: CommandContext) {
        if (ctx.member!!.voiceState == null || !ctx.member!!.voiceState!!.inAudioChannel())
            return ctx.replyError(ctx.translate("commands.play.errors.voice"))

        var query = ctx.getOption("query")!!.asString
        if (!isUrl(query))
            query = "ytsearch:$query"

        ctx.deferReply()

        val musicManager = ctx.client.audioManager.getGuildManager(ctx.guild!!, ctx.channel.id)
        if (musicManager.link.channel != null && ctx.member!!.voiceState!!.channel!!.id != musicManager.link.channel)
            return ctx.editOriginalWithError(ctx.translate("commands.play.errors.samevoice"))

        if (ctx.client.lavalink.nodes.none { node -> node.isAvailable })
            return ctx.editOriginalWithError(ctx.translate("commands.play.errors.nodes"))

        musicManager.link.connect(ctx.member!!.voiceState!!.channel!!)
        ctx.client.audioManager.playerManager.loadItemOrdered(musicManager, query, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                if (musicManager.scheduler.queue.size >= 250)
                    return ctx.editOriginalWithError(ctx.translate("player.limit", mapOf("limit" to 250)))

                track.userData = ctx.author.id
                musicManager.scheduler.addToQueue(track)

                ctx.editOriginalEmbeds(
                    EmbedBuilder().setColor(Config.embedColor)
                        .setTitle(ctx.translate("commands.play.loaded.track.title"))
                        .setDescription("[${track.info.title}](${track.info.uri})")
                        .addField(
                            ctx.translate("commands.play.loaded.author"),
                            track.info.author,
                            true
                        )
                        .addField(
                            ctx.translate("commands.play.loaded.duration"),
                            if (track.info.isStream) ctx.translate("commands.play.loaded.stream") else SimpleDateFormat(
                                "HH:mm:ss"
                            ).format(track.duration),
                            true
                        )
                        .build()
                )
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                if (musicManager.scheduler.queue.size >= 250)
                    return ctx.editOriginalWithError(ctx.translate("player.limit", mapOf("limit" to 250)))

                if (playlist.isSearchResult) {
                    val track = playlist.tracks[0]
                    track.userData = ctx.author.id
                    musicManager.scheduler.addToQueue(track)

                    return ctx.editOriginalEmbeds(
                        EmbedBuilder().setColor(Config.embedColor)
                            .setTitle(ctx.translate("commands.play.loaded.track.title"))
                            .setDescription("[${track.info.title}](${track.info.uri})")
                            .addField(
                                ctx.translate("commands.play.loaded.author"),
                                track.info.author,
                                true
                            )
                            .addField(
                                ctx.translate("commands.play.loaded.duration"),
                                if (track.info.isStream) ctx.translate("commands.play.loaded.stream") else SimpleDateFormat(
                                    "HH:mm:ss"
                                ).format(track.duration),
                                true
                            )
                            .build()
                    )
                }

                if (musicManager.scheduler.queue.size + playlist.tracks.size > 250) {
                    while (musicManager.scheduler.queue.size + playlist.tracks.size > 250)
                        playlist.tracks.removeLastOrNull() ?: break
                }

                for (track in playlist.tracks) {
                    track.userData = ctx.author.id
                    musicManager.scheduler.addToQueue(track)
                }

                ctx.editOriginalEmbeds(
                    EmbedBuilder().setColor(Config.embedColor)
                        .setTitle(ctx.translate("commands.play.loaded.playlist.title"))
                        .setDescription(
                            ctx.translate(
                                "commands.play.loaded.playlist.format",
                                mapOf("name" to playlist.name, "size" to playlist.tracks.size)
                            )
                        )
                        .build()
                )
            }

            override fun noMatches() {
                ctx.editOriginalWithError(ctx.translate("commands.play.errors.nomatches"))
            }

            override fun loadFailed(exception: FriendlyException) {
                ctx.editOriginalWithError(ctx.translate("commands.play.errors.loadfail"))
            }
        })
    }

    private fun isUrl(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (ignore: Exception) {
            false
        }
    }
}