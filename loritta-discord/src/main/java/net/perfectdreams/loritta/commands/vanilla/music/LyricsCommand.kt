package net.perfectdreams.loritta.commands.vanilla.music

import com.mrpowergamerbr.loritta.utils.Constants
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.utils.MarkdownUtil
import net.perfectdreams.loritta.api.commands.Command
import net.perfectdreams.loritta.api.commands.CommandCategory
import net.perfectdreams.loritta.api.commands.CommandContext
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.platform.discord.LorittaDiscord
import net.perfectdreams.loritta.platform.discord.commands.DiscordAbstractCommandBase
import net.perfectdreams.loritta.utils.lyrics.LyricsUtil

class LyricsCommand(loritta: LorittaDiscord): DiscordAbstractCommandBase(
        loritta,
        listOf("lyrics", "letra", "letras"),
        CommandCategory.MUSIC) {
    override fun command() = create {
        localizedDescription("commands.music.lyrics.description")

        examples {
            + "she - Atomic"
            + "she - Chiptune Memories"
            + "C418 - tsuki no koibumi"
            + "MC Hariel - Tá Fácil Dizer Que Me Ama"
            + "Jack Ü - Jungle Bae"
            + "Pusher - Clear"
            + "Sega - Sonic Boom"
            + "Macklemore & Ryan Lewis - White Walls"
        }

        executesDiscord {
            val query = args.joinToString(" ")
            if (query.isBlank()) explainAndExit()

            val lyricsInfo = LyricsUtil.findLyricsByQuery(query)

            if (lyricsInfo != null) {
                val provider = lyricsInfo.provider
                val songInfo = lyricsInfo.songInfo

                val embed = EmbedBuilder().apply {
                    setTitle("\uD83C\uDFB6\uD83D\uDCC4 ${songInfo.artistName} - ${songInfo.songName}", songInfo.href)
                    setColor(Constants.LORITTA_AQUA)
                    setThumbnail(songInfo.artworkUrl)
                    setDescription(MarkdownUtil.codeblock(songInfo.lyrics))
                    setFooter("${provider.name} - ${provider.baseURL}", provider.icon)
                }

                sendMessage(embed.build())
            } else {
                reply(
                        "${locale["commands.music.lyrics.couldntFind"]} ${locale["commands.music.lyrics.sorryForTheInconvenience"]} \uD83D\uDE2D",
                        Constants.ERROR
                )
            }
        }
    }
}