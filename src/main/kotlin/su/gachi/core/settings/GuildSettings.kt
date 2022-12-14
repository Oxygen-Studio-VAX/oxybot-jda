package su.gachi.core.settings

import net.dv8tion.jda.api.entities.Guild
import su.gachi.core.Client

class GuildSettings(val client: Client, val guild: Guild?) {
    val general = GeneralSettings(client, guild)
}