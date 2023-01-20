package su.gachi.commands.items

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.json.JSONObject
import su.gachi.Config
import su.gachi.core.commands.Command
import su.gachi.core.commands.CommandContext
import su.gachi.items.NpcsData
import su.gachi.items.WeaponsItems

class WeaponsCommand : Command() {
    init {
        name = "weapons"
        description = "Information about weapons"

        options = listOf(
            OptionData(OptionType.STRING, "id", "Weapon ID (short name)", true, true)
                .setDescriptionLocalization(DiscordLocale.RUSSIAN, "ID оружия (короткое имя)")
        )

        isGuildOnly = false
    }

    override fun handle(ctx: CommandContext) {
        ctx.deferReply()

        if (ctx.getOption("id")!!.asString == "list") {
            val builder = StringBuilder()
            WeaponsItems.raw.forEach {
                val item = it as JSONObject
                builder.append("`${item.getString("id")}` = **")
                builder.append(
                    if (item.getJSONObject("name_localized").has(ctx.locale))
                        item.getJSONObject("name_localized").getString(ctx.locale)
                    else
                        item.getString("name")
                )
                builder.append("**\n")
            }

            ctx.editOriginalEmbeds(
                EmbedBuilder().setColor(Config.embedColor)
                    .setTitle(ctx.translate("commands.weapons.title"))
                    .setDescription(builder.toString())
                    .setFooter(ctx.translate("commands.weapons.footer"))
                    .build()
            )
        } else {
            val item = WeaponsItems.findByID(ctx.getOption("id")!!.asString)
                ?: return ctx.editOriginalWithError(ctx.translate("commands.weapons.error"))

            val itemName = if (item.getJSONObject("name_localized").has(ctx.locale))
                item.getJSONObject("name_localized").getString(ctx.locale)
            else
                item.getString("name")
            val itemDescription = if (item.getJSONObject("description_localized").has(ctx.locale))
                item.getJSONObject("description_localized").getString(ctx.locale)
            else
                item.getString("description")

            val pricingKeys = item.getJSONObject("pricing").keySet()
                .filter { key -> !item.getJSONObject("pricing").isNull(key) }
            val pricing = mutableMapOf<String, Any>()
            pricingKeys.forEach { key -> pricing[key] = item.getJSONObject("pricing").get(key) }
            val parametersKeys = item.getJSONObject("parameters").keySet()
                .filter { key -> !item.getJSONObject("parameters").isNull(key) }
            val parameters = mutableMapOf<String, Any>()
            parametersKeys.forEach { key -> parameters[key] = item.getJSONObject("parameters").get(key) }

            if (!item.isNull("npc")) {
                val npc = NpcsData.npcs[item.getString("npc")]
                if (npc != null) {
                    pricing["npc"] = "${if (npc.getJSONObject("name_localized").has(ctx.locale))
                        npc.getJSONObject("name_localized").getString(ctx.locale)
                    else
                        npc.getString("name")} ${npc.getString("emoji")}"
                }
            }

            ctx.editOriginalEmbeds(
                EmbedBuilder().setColor(if (item.isNull("color")) Config.embedColor else item.getInt("color"))
                    .setTitle(ctx.translate("commands.weapons.info.title", mapOf("name" to itemName)))
                    .setDescription(itemDescription)
                    .setThumbnail(item.getString("image"))
                    .addField(ctx.translate("commands.weapons.info.pricing.name"), ctx.translate("commands.weapons.info.pricing.value", pricing), true)
                    .addField(ctx.translate("commands.weapons.info.parameters.name"), ctx.translate("commands.weapons.info.parameters.value", parameters), true)
                    .build()
            )
        }
    }
}