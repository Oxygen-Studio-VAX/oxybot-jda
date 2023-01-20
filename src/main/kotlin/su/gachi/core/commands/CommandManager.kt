package su.gachi.core.commands

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction
import org.slf4j.LoggerFactory
import su.gachi.commands.info.InfoCommand
import su.gachi.commands.items.ArmorsCommand
import su.gachi.commands.items.BackpacksCommand
import su.gachi.commands.items.WeaponsCommand
import su.gachi.commands.music.PlayCommand
import su.gachi.commands.music.QueueCommand
import su.gachi.commands.music.SkipCommand
import su.gachi.commands.music.StopCommand
import su.gachi.commands.utils.PingCommand
import su.gachi.core.Client
import su.gachi.core.settings.GuildSettings

class CommandManager(val client: Client) {
    private val commands: MutableMap<String, Command> = mutableMapOf()
    val localizationFunction = ResourceBundleLocalizationFunction
        .fromBundles("Commands", DiscordLocale.RUSSIAN)
        .build()

    init {
        addCommands(
            InfoCommand(),
            PingCommand(),
            ArmorsCommand(), BackpacksCommand(), WeaponsCommand(),
            PlayCommand(), StopCommand(), SkipCommand(), QueueCommand()
        )
    }

    private fun addCommand(command: Command) {
        if (command.name == null || commands.containsKey(command.name)) return

        commands[command.name!!] = command

        println(command.toCommandData(this).toData())
    }

    private fun addCommands(vararg commands: Command) {
        for (command in commands) {
            addCommand(command)
        }
    }

    fun updateCommands() {
        if (client.dotenv["DEV"] != null) {
            LoggerFactory.getLogger(this::class.java).info("Updating commands in DEV mode")
            client.shardManager.shards.forEach { shard ->
                shard.guilds.forEach { guild -> guild.updateCommands().addCommands(commands.map { el -> el.value.toCommandData(this) }).queue() }
            }
        } else {
            LoggerFactory.getLogger(this::class.java).info("Updating commands in RELEASE mode")
            client.shardManager.shards.forEach { shard ->
                shard.updateCommands().addCommands(commands.map { el -> el.value.toCommandData(this) }).queue()
            }
        }
    }

    fun handle(interactionEvent: SlashCommandInteractionEvent) {
        if (!commands.containsKey(interactionEvent.name)) return

        val settings = GuildSettings(client, interactionEvent.guild)
        val ctx = CommandContext(client, interactionEvent, settings)

        commands[interactionEvent.name]!!.handle(ctx)
    }
}