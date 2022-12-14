package su.gachi.core.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import org.apache.commons.text.StringSubstitutor
import su.gachi.Config
import su.gachi.core.Client
import su.gachi.core.settings.GuildSettings

class CommandContext(val client: Client, val interactionEvent: SlashCommandInteractionEvent, val settings: GuildSettings) {
    fun reply(content: String, ephemeral: Boolean=true) {
        interactionEvent.reply(content).setEphemeral(ephemeral).queue()
    }

    fun replyError(content: String) {
        interactionEvent.replyEmbeds(
            EmbedBuilder().setColor(Config.redColor)
                .setDescription(content)
                .build()
        ).setEphemeral(true).queue()
    }

    fun editOriginalWithError(content: String) {
        interactionEvent.hook.editOriginalEmbeds(
            EmbedBuilder().setColor(Config.redColor)
            .setDescription(content)
            .build()).queue()
    }

    fun editOriginal(content: String) {
        interactionEvent.hook.editOriginal(content).queue()
    }

    fun editOriginalEmbeds(embed: MessageEmbed) {
        interactionEvent.hook.editOriginalEmbeds(embed).queue()
    }

    fun replyEmbeds(messageEmbed: MessageEmbed, ephemeral: Boolean=true) {
        interactionEvent.replyEmbeds(messageEmbed).setEphemeral(ephemeral).queue()
    }

    fun deferReply(ephemeral: Boolean=true) {
        interactionEvent.deferReply(ephemeral).queue()
    }

    fun getOption(option: String): OptionMapping? {
        return interactionEvent.getOption(option)
    }

    fun translate(phrase: String, replacements: Map<String, Any> = emptyMap()): String {
        val substitutor = StringSubstitutor(replacements, "{", "}")

        var locale = settings.general.locale
        if (locale == null) {
            locale = interactionEvent.userLocale.locale
            locale = if (Config.locales.contains(locale.split("-")[0]))
                locale.split("-")[0]
            else
                Config.defaultLocale
        }

        return substitutor.replace(client.localeService.translate(phrase, locale))
    }

    val author: User
        get() = interactionEvent.user

    val member: Member?
        get() = interactionEvent.member

    val guild: Guild?
        get() = interactionEvent.guild

    val channel: MessageChannelUnion
        get() = interactionEvent.channel
}