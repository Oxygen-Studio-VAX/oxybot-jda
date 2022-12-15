package su.gachi.commands.music

import su.gachi.core.commands.Command
import su.gachi.core.commands.CommandContext

class SkipCommand : Command() {
    init {
        name = "skip"
        description = "Skip current track"
    }

    override fun handle(ctx: CommandContext) {
        if (!ctx.client.audioManager.managers.containsKey(ctx.guild!!.id))
            return ctx.replyError(ctx.translate("commands.skip.errors.queue"))
        if (ctx.member!!.voiceState == null || !ctx.member!!.voiceState!!.inAudioChannel())
            return ctx.replyError(ctx.translate("commands.skip.errors.voice"))

        ctx.deferReply()

        val musicManager = ctx.client.audioManager.getGuildManager(ctx.guild!!, ctx.channel.id)
        if (musicManager.link.channel != null && ctx.member!!.voiceState!!.channel!!.id != musicManager.link.channel)
            return ctx.editOriginalWithError(ctx.translate("commands.skip.errors.samevoice"))

        if (musicManager.scheduler.queue.isEmpty())
            return ctx.editOriginalWithError(ctx.translate("commands.skip.errors.empty"))

        ctx.editOriginal(ctx.translate("commands.skip.success"))
        musicManager.scheduler.nextTrack()
    }


}