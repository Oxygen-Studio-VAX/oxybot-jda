package su.gachi.listeners.interactions

import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class NotifySelectListener : ListenerAdapter() {
    override fun onGenericSelectMenuInteraction(event: GenericSelectMenuInteractionEvent<*, *>) {
        if (event.componentId != "notifications" || event.member == null) return

        event.deferReply(true)

        val role = event.jda.getRoleById(event.values[0].toString())
            ?: return

        if (event.member!!.roles.contains(role))
            event.guild!!.addRoleToMember(event.member!!, role).queue { event.hook.editOriginal("Вы подписаны на `${role.name}`").queue() }
        else
            event.guild!!.removeRoleFromMember(event.member!!, role).queue { event.hook.editOriginal("Вы отписаны от `${role.name}`").queue() }
    }
}