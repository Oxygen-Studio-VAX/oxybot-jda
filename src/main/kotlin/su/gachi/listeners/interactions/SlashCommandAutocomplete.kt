package su.gachi.listeners.interactions

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command
import su.gachi.items.ArmoryItems
import su.gachi.items.BackpacksItems

class SlashCommandAutocomplete : ListenerAdapter() {
    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        if (event.name == "armors" && event.focusedOption.name == "id") {
            val choices = ArmoryItems.getAllIDs()
                .filter { item -> item.startsWith(event.focusedOption.value) }
                .map { item -> Command.Choice(item, item) }
                .toMutableList()
            choices.add(Command.Choice("list", "list"))

            event.replyChoices(choices).queue()
        } else if (event.name == "backpacks" && event.focusedOption.name == "id") {
            val choices = BackpacksItems.getAllIDs()
                .filter { item -> item.startsWith(event.focusedOption.value) }
                .map { item -> Command.Choice(item, item) }
                .toMutableList()
            choices.add(Command.Choice("list", "list"))

            event.replyChoices(choices).queue()
        }
    }
}