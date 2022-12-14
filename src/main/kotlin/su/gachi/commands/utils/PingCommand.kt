package su.gachi.commands.utils

import su.gachi.core.commands.Command
import su.gachi.core.commands.CommandContext

class PingCommand : Command() {
    init {
        name = "ping"
        description = "Check bot online"
    }

    override fun handle(ctx: CommandContext) {
        ctx.reply(ctx.translate("commands.ping.pong"))
    }
}