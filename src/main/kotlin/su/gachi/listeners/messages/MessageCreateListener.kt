package su.gachi.listeners.messages

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import su.gachi.Config

class MessageCreateListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.id != "431916398361706496") return

        if (event.message.contentRaw.startsWith("!show-roles")) {
            event.channel.sendMessageEmbeds(
                EmbedBuilder()
                    .setTitle("Выбор оповещений")
                    .setColor(Config.embedColor)
                    .build()
            ).addActionRow(
                Button.primary("a-942071332378640384", "Объявления"),
                Button.primary("a-944853033345515520", "Трейд"),
                Button.primary("a-947733327916433439", "Голосования"),
                Button.primary("a-947232132584067112", "Обновления"),
                Button.primary("a-957513161584554014", "Обновления бота")
            ).queue()
        }
    }
}