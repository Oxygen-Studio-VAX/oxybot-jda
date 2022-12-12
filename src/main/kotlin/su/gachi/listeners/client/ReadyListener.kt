package su.gachi.listeners.client

import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import su.gachi.core.Client

class ReadyListener(val client: Client) : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {

    }
}