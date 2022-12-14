package su.gachi.commands.info

import net.dv8tion.jda.api.EmbedBuilder
import su.gachi.Config
import su.gachi.core.commands.Command
import su.gachi.core.commands.CommandContext

class InfoCommand : Command() {
    init {
        name = "info"
        description = "Show bot info"
    }

    override fun handle(ctx: CommandContext) {
        ctx.replyEmbeds(
            EmbedBuilder().setColor(Config.embedColor)
                .setTitle(ctx.translate("commands.info.title"))
                .setDescription(ctx.translate("commands.info.description"))
                .addField(ctx.translate("commands.info.version"), Config.version, true)
                .setFooter(ctx.translate("commands.info.footer"), "https://www.gachi.su/favicon.ico")
                .build()
        )
    }
}