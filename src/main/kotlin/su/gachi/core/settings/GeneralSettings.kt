package su.gachi.core.settings

import com.mongodb.client.model.Filters
import net.dv8tion.jda.api.entities.Guild
import org.json.JSONObject
import su.gachi.core.Client

class GeneralSettings(val client: Client, val guild: Guild?) {
    val raw = JSONObject()

    init {
        if (guild != null) {
            val doc = client.databaseService.getCollection("guild_settings")
                .find(Filters.eq("guild_id", guild.id)).first()

            if (doc != null) {
                raw.put("locale", doc.getString("locale"))
            }
        }
    }

    val locale: String?
        get() = if (raw.has("locale")) raw.getString("locale") else null
}