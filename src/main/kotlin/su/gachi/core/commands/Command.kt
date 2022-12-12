package su.gachi.core.commands

import net.dv8tion.jda.api.interactions.commands.build.OptionData

abstract class Command {
    var name: String? = null
    var description: String? = null

    var options = listOf<OptionData>()

    abstract fun handle(ctx: CommandContext)
}