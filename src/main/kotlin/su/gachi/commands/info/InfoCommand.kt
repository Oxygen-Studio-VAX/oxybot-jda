package su.gachi.commands.info

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import su.gachi.Config
import su.gachi.core.commands.Command
import su.gachi.core.commands.CommandContext

class InfoCommand : Command() {
    init {
        name = "info"
        description = "Show bot info"

        options = listOf(
            OptionData(OptionType.BOOLEAN, "debug", "Show debug information")
                .setDescriptionLocalization(DiscordLocale.RUSSIAN, "Показывать дебаг информацию")
        )

        isGuildOnly = false
    }

    override fun handle(ctx: CommandContext) {
        val emb = EmbedBuilder().setColor(Config.embedColor)
            .setTitle(ctx.translate("commands.info.title"))
            .setDescription(ctx.translate("commands.info.description"))
            .addField(ctx.translate("commands.info.version"), Config.version, true)
            .setFooter(ctx.translate("commands.info.footer"), "https://music.gachi.su/logo.png")

        if (ctx.getOption("debug")?.asBoolean == true)
            emb.addField("Debug info", "```\nJV${System.getProperty("java.version")}KT${KotlinVersion.CURRENT}BT${Config.version}US${ctx.author.id}GU${ctx.guild?.id ?: "DM"}T${System.currentTimeMillis()}\n```", false)

        ctx.replyEmbeds(emb.build())
    }
}