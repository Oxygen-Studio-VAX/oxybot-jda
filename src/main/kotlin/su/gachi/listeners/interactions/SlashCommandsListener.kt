package su.gachi.listeners.interactions

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import su.gachi.core.Client

class SlashCommandsListener(private val client: Client) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        client.commandManager.handle(event)
    }
}