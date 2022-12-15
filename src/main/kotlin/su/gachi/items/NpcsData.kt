package su.gachi.items

import org.json.JSONArray
import org.json.JSONObject

class NpcsData {
    companion object {
        val npcs = mutableMapOf<String, JSONObject>()

        init {
            val str = NpcsData::class.java.getResource("/items/npc.json")?.readText(Charsets.UTF_8)
            val raw = JSONArray(str)

            raw.forEach {
                val data = it as JSONObject
                npcs[data.getString("id")] = data
            }
        }
    }
}