package su.gachi.commands.music

import com.github.ygimenez.method.Pages
import com.github.ygimenez.model.InteractPage
import com.github.ygimenez.model.Page
import com.github.ygimenez.model.ThrowingFunction
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import su.gachi.Config
import su.gachi.core.commands.Command
import su.gachi.core.commands.CommandContext
import java.lang.Math.ceil
import java.lang.Math.min
import java.util.function.Predicate

class QueueCommand : Command() {
    init {
        name = "queue"
        description = "Show music queue"

        options = listOf(
            OptionData(OptionType.INTEGER, "page", "Music queue page")
                .setRequiredRange(1, 17)
                .setNameLocalization(DiscordLocale.RUSSIAN, "страница")
                .setDescriptionLocalization(DiscordLocale.RUSSIAN, "Страница музыкальной очереди")
        )
    }

    override fun handle(ctx: CommandContext) {
        if (!ctx.client.audioManager.managers.containsKey(ctx.guild!!.id))
            return ctx.replyError(ctx.translate("commands.queue.error"))
        ctx.deferReply(true)

        val musicManager = ctx.client.audioManager.getGuildManager(ctx.guild!!, ctx.channel.id)

        var page = 1
        var maxPages = ceil(musicManager.scheduler.queue.size.toDouble() / 15).toInt()

        if (ctx.getOption("page") != null)
            if (maxPages >= ctx.getOption("page")!!.asInt)
                page = ctx.getOption("page")!!.asInt

        var track = musicManager.link.player.playingTrack
        var trackImage: String? = null
        if (track.sourceManager.sourceName == "youtube")
            trackImage = "https://img.youtube.com/vi/${track.identifier}/hqdefault.jpg"

        if (musicManager.scheduler.queue.isEmpty()) {
            return ctx.editOriginalEmbeds(
                EmbedBuilder()
                    .setColor(Config.embedColor)
                    .setTitle(ctx.translate("player.np.title"))
                    .setDescription("[${track.info.title}](${track.info.uri}) [<@${track.userData}>]")
                    .setThumbnail(trackImage)
                    .build()
            )
        }

        val embed = EmbedBuilder()
            .setColor(Config.embedColor)
            .setTitle(ctx.translate("commands.queue.title"))

        val func: ThrowingFunction<Int, Page> = ThrowingFunction { i: Int ->
            maxPages = ceil(musicManager.scheduler.queue.size.toDouble() / 15).toInt()
            if (i + 1 > maxPages) null
            else {
                track = musicManager.link.player.playingTrack
                trackImage = null
                if (track.sourceManager.sourceName == "youtube")
                    trackImage = "https://img.youtube.com/vi/${track.identifier}/hqdefault.jpg"

                embed
                    .setDescription("[${track.info.title}](${track.info.uri}) [<@${track.userData}>]")
                    .setThumbnail(trackImage)
                    .setFooter(
                        ctx.translate(
                            "commands.queue.format.footer",
                            mapOf(
                                "user" to ctx.author.asTag,
                                "id" to ctx.author.id,
                                "page" to i + 1,
                                "maxPages" to maxPages
                            )
                        ), ctx.author.effectiveAvatarUrl
                    )

                embed.fields.removeAll { true }
                embed.fields
                    .addAll(
                        musicManager.scheduler.queue.toList()
                            .subList(
                                min((maxPages - 1) * 15, i * 15),
                                min((i + 1) * 15, musicManager.scheduler.queue.size)
                            )
                            .map { track ->
                                MessageEmbed.Field(
                                    "${musicManager.scheduler.queue.indexOf(track) + 1} ${track.info.title}",
                                    ctx.translate(
                                        "commands.queue.format",
                                        mapOf(
                                            "link" to track.info.uri,
                                            "author" to track.info.author,
                                            "requester" to "[<@${track.userData}>]"
                                        )
                                    ),
                                    false
                                )
                            }
                    )

                InteractPage(embed.build())
            }
        }

        val pred: Predicate<User> = Predicate { user: User -> ctx.author.id == user.id }

        ctx.channel.sendMessageEmbeds(
            func.applyThrows(page - 1).content as MessageEmbed
        ).queue({ message ->
            ctx.interactionEvent.hook.deleteOriginal().queue()
            Pages.lazyPaginate(message, func, true, false, pred)
        }, {})
    }
}