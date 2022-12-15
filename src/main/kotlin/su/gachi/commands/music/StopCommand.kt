package su.gachi.commands.music

import su.gachi.core.commands.Command
import su.gachi.core.commands.CommandContext

class StopCommand : Command() {
    init {
        name = "stop"
        description = "Stop player"
    }

    override fun handle(ctx: CommandContext) {
        if (!ctx.client.audioManager.managers.containsKey(ctx.guild!!.id))
            return ctx.replyError(ctx.translate("commands.stop.errors.queue"))
        if (ctx.member!!.voiceState == null || !ctx.member!!.voiceState!!.inAudioChannel())
            return ctx.replyError(ctx.translate("commands.stop.errors.voice"))

        ctx.deferReply()

        val musicManager = ctx.client.audioManager.getGuildManager(ctx.guild!!, ctx.channel.id)
        if (musicManager.link.channel != null && ctx.member!!.voiceState!!.channel!!.id != musicManager.link.channel)
            return ctx.editOriginalWithError(ctx.translate("commands.stop.errors.samevoice"))

        ctx.editOriginal(ctx.translate("commands.stop.success"))
        ctx.client.audioManager.destroyManager(ctx.guild!!)
    }
}