package su.gachi.core.commands

import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData

abstract class Command {
    var name: String? = null
    var description: String? = null

    var options = listOf<OptionData>()

    var isGuildOnly: Boolean = true

    abstract fun handle(ctx: CommandContext)

    fun toCommandData(commandManager: CommandManager): CommandData {
        val builder = Commands.slash(name!!, description!!)

        builder.setLocalizationFunction(commandManager.localizationFunction)
        builder.isGuildOnly = isGuildOnly
        builder.addOptions(options)

        return builder
    }
}