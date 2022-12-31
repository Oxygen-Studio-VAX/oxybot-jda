package su.gachi.listeners.interactions

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class NotifySelectListener : ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        event.deferReply(true).queue()

        val role = event.guild!!.getRoleById(event.componentId.removePrefix("a-"))
            ?: return

        if (!event.member!!.roles.contains(role))
            event.guild!!.addRoleToMember(event.member!!, role).queue { event.hook.editOriginal("Вы подписаны на `${role.name}`").queue() }
        else
            event.guild!!.removeRoleFromMember(event.member!!, role).queue { event.hook.editOriginal("Вы отписались от `${role.name}`").queue() }
    }
}