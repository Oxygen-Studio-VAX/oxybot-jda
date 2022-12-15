package su.gachi.items

import org.json.JSONArray
import org.json.JSONObject

class ArmoryItems {
    companion object {
        var raw: JSONArray

        init {
            val str = ArmoryItems::class.java.getResource("/items/armor.json")?.readText(Charsets.UTF_8)
            raw = JSONArray(str)
        }

        fun getAllIDs(): List<String> {
            val out = mutableListOf<String>()

            raw.forEach { obj -> out.add((obj as JSONObject).getString("id")) }

            return out
        }

        fun findByID(id: String): JSONObject? {
            val item = raw.find { (it as JSONObject).getString("id") == id }
                ?: return null

            return item as JSONObject
        }
    }
}