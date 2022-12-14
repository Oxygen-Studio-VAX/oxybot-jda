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
import kotlin.text.StringBuilder


class ArmorsCommand : Command() {
    init {
        name = "armors"
        description = "Information about armors"

        options = listOf(
            OptionData(OptionType.STRING, "id", "Armor ID (short name)", true, true)
                .setDescriptionLocalization(DiscordLocale.RUSSIAN, "ID бронезащиты (короткое имя)")
        )
    }

    override fun handle(ctx: CommandContext) {
        ctx.deferReply()

        if (ctx.getOption("id")!!.asString == "list") {
            val builder = StringBuilder()
            ArmoryItems.raw.forEach {
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
                    .setTitle(ctx.translate("commands.armors.title"))
                    .setDescription(builder.toString())
                    .setFooter(ctx.translate("commands.armors.footer"))
                    .build()
            )
        } else {
            val item = ArmoryItems.findByID(ctx.getOption("id")!!.asString)
                ?: return ctx.editOriginalWithError(ctx.translate("commands.armors.error"))

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

            ctx.editOriginalEmbeds(
                EmbedBuilder().setColor(Config.embedColor)
                    .setTitle(ctx.translate("commands.armors.info.title", mapOf("name" to itemName)))
                    .setDescription(itemDescription)
                    .setThumbnail(item.getString("image"))
                    .addField(ctx.translate("commands.armors.info.pricing.name"), ctx.translate("commands.armors.info.pricing.value", pricing), true)
                    .addField(ctx.translate("commands.armors.info.parameters.name"), ctx.translate("commands.armors.info.parameters.value", parameters), true)
                    .addField(ctx.translate("commands.armors.info.found.name"), ctx.translate("commands.armors.undefined"), true)
                    .build()
            )
        }
    }
}