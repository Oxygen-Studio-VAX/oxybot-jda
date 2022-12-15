package su.gachi.commands.items

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.json.JSONObject
import su.gachi.Config
import su.gachi.core.commands.Command
import su.gachi.core.commands.CommandContext
import su.gachi.items.ArmoryItems
import su.gachi.items.BackpacksItems
import su.gachi.items.NpcsData

class BackpacksCommand : Command() {
    init {
        name = "backpacks"
        description = "Information about backpacks"

        options = listOf(
            OptionData(OptionType.STRING, "id", "Backpack ID (short name)", true, true)
                .setDescriptionLocalization(DiscordLocale.RUSSIAN, "ID рюкзака (короткое имя)")
        )

        isGuildOnly = false
    }

    override fun handle(ctx: CommandContext) {
        ctx.deferReply()

        if (ctx.getOption("id")!!.asString == "list") {
            val builder = StringBuilder()
            BackpacksItems.raw.forEach {
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
                    .setTitle(ctx.translate("commands.backpacks.title"))
                    .setDescription(builder.toString())
                    .setFooter(ctx.translate("commands.backpacks.footer"))
                    .build()
            )
        } else {
            val item = BackpacksItems.findByID(ctx.getOption("id")!!.asString)
                ?: return ctx.editOriginalWithError(ctx.translate("commands.backpacks.error"))

            val itemName = if (item.getJSONObject("name_localized").has(ctx.locale))
                item.getJSONObject("name_localized").getString(ctx.locale)
            else
                item.getString("name")
            val itemDescription = if (item.getJSONObject("description_localized").has(ctx.locale))
                item.getJSONObject("description_localized").getString(ctx.locale)
            else
                item.getString("description")
            val itemRarity = if (item.getJSONObject("rarity_localized").has(ctx.locale))
                item.getJSONObject("rarity_localized").getString(ctx.locale)
            else
                item.getString("rarity")
            val itemDemand = if (item.getJSONObject("demand_localized").has(ctx.locale))
                item.getJSONObject("demand_localized").getString(ctx.locale)
            else
                item.getString("demand")

            val pricingKeys = item.getJSONObject("pricing").keySet()
                .filter { key -> !item.getJSONObject("pricing").isNull(key) }
            val pricing = mutableMapOf<String, Any>()
            pricingKeys.forEach { key -> pricing[key] = item.getJSONObject("pricing").get(key) }

            if (!item.isNull("npc")) {
                val npc = NpcsData.npcs[item.getString("npc")]
                if (npc != null) {
                    pricing["npc"] = "${npc.getString("emoji")} ${if (npc.getJSONObject("name_localized").has(ctx.locale))
                        npc.getJSONObject("name_localized").getString(ctx.locale)
                    else
                        npc.getString("name")}"
                }
            }

            ctx.editOriginalEmbeds(
                EmbedBuilder().setColor(if (item.isNull("color")) Config.embedColor else item.getInt("color"))
                    .setTitle(ctx.translate("commands.backpacks.info.title", mapOf("name" to itemName)))
                    .setDescription(itemDescription)
                    .setThumbnail(item.getString("image"))
                    .addField(ctx.translate("commands.backpacks.info.pricing.name"), ctx.translate("commands.backpacks.info.pricing.value", pricing), true)
                    .addField(ctx.translate("commands.backpacks.info.rarity.name"), itemRarity, true)
                    .addField(ctx.translate("commands.backpacks.info.demand.name"), itemDemand, true)
                    .build()
            )
        }
    }
}