package net.perfectdreams.loritta.plugin.malcommands.commands

import com.mrpowergamerbr.loritta.utils.*
import com.mrpowergamerbr.loritta.utils.extensions.edit
import com.mrpowergamerbr.loritta.utils.locale.BaseLocale
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.perfectdreams.loritta.api.commands.ArgumentType
import net.perfectdreams.loritta.api.commands.CommandCategory
import net.perfectdreams.loritta.platform.discord.commands.DiscordAbstractCommandBase
import net.perfectdreams.loritta.plugin.malcommands.MalCommandsPlugin
import net.perfectdreams.loritta.plugin.malcommands.models.AnimeStatus
import net.perfectdreams.loritta.plugin.malcommands.models.AnimeType
import net.perfectdreams.loritta.plugin.malcommands.models.MalAnime
import net.perfectdreams.loritta.plugin.malcommands.utils.MalConstants.MAL_COLOR
import net.perfectdreams.loritta.plugin.malcommands.utils.AnimeUtils
import net.perfectdreams.loritta.utils.Emotes

class MalAnimeCommand(val m: MalCommandsPlugin) : DiscordAbstractCommandBase(m.loritta, listOf("malanime", "anime"), CommandCategory.ANIME) {
    private val LOCALE_PREFIX = "commands.anime.mal.anime"
    private val logger = KotlinLogging.logger { }
    private val maximumIndexAmount = 9 // Used for the maximum amount of indexes on search

    override fun command() = create {
        localizedDescription("$LOCALE_PREFIX.description")

        examples {
            + "Nichijou"
            + "Pop Team Epic"
        }

        usage {
            argument(ArgumentType.TEXT) {}
        }

        executesDiscord {
            if (args.isEmpty())
                explainAndExit()

            val embed = EmbedBuilder()
            val query = args.joinToString(" ")

            logger.debug { "The anime query is \"$query\"" }

            val animeQueries = AnimeUtils.queryAnime(query)

            if (animeQueries.isEmpty())
                fail(locale["$LOCALE_PREFIX.notfound"])

            val message = sendMessage(
                    embed.apply {
                        setTitle("${Emotes.SUPER_LORI_HAPPY} **|** Resultados da pesquisa: \"$query\"")
                        setColor(MAL_COLOR)
                        setDescription(
                                buildString {
                                    this.append("Para escolher você precisa escrever de acordo com o número ao lado do nome do anime (entendeu?)\n\n")
                                    for (i in 0 until maximumIndexAmount.coerceAtMost(animeQueries.size)) {
                                        val anime = animeQueries[i]
                                        val emoji = AnimeUtils.getEmoteForAnimeType(anime.mediaType!!)
                                        this.append("${Constants.INDEXES[i]} $emoji • [${anime.name}](${anime.url})\n")
                                    }
                                }
                        )
                    }.build()
            )

            message.onResponseByAuthor(
                    this.discordMessage.author.idLong,
                    this.guild.idLong,
                    this.discordMessage.channel.idLong) {
                val index = it.message.contentStripped.toIntOrNull()

                if (index != null) {
                    if (index in 0..maximumIndexAmount) {
                        val partialAnime = animeQueries[index - 1]
                        val anime = AnimeUtils.parseAnime(partialAnime.url)
                                ?: return@onResponseByAuthor

                        message.edit(
                                this@executesDiscord.getUserMention(true),
                                createResourceEmbed(
                                        partialAnime.url,
                                        anime,
                                        locale
                                ).build(), true
                        )
                        return@onResponseByAuthor
                    }
                }

                return@onResponseByAuthor
            }
        }
    }

    private fun createResourceEmbed(url: String, anime: MalAnime, locale: BaseLocale): EmbedBuilder {
        logger.debug { anime.toString() }

        val emoji = AnimeUtils.getEmoteForAnimeType(anime.info.type!!) + " "

        val embed = EmbedBuilder()

        val score = if (anime.score != null) anime.score.toString() else locale["$LOCALE_PREFIX.unknown"]
        val rank = if (anime.rank != null) "#${anime.rank}" else locale["$LOCALE_PREFIX.unknown"]
        val popularity = if (anime.popularity != null) "#${anime.popularity}" else locale["$LOCALE_PREFIX.unknown"]

        embed.apply {
            setTitle(emoji + anime.info.name, url)
            setColor(MAL_COLOR)
            setThumbnail(anime.image)
            // Anime type (TV, special, OVA, etc)
            addField(locale["$LOCALE_PREFIX.type.name"], when (anime.info.type) {
                AnimeType.TV -> locale["$LOCALE_PREFIX.type.tv"]
                AnimeType.SPECIAL -> locale["$LOCALE_PREFIX.type.special"]
                AnimeType.OVA -> locale["$LOCALE_PREFIX.type.ova"]
                AnimeType.ONA -> locale["$LOCALE_PREFIX.type.ona"]
                AnimeType.MOVIE -> locale["$LOCALE_PREFIX.type.movie"]
                else -> locale["$LOCALE_PREFIX.unknown"]
            }, true)
            // Anime airing status
            addField(locale["$LOCALE_PREFIX.status.name"], when (anime.info.status) {
                AnimeStatus.CURRENTLY_AIRING -> locale["$LOCALE_PREFIX.status.airing"]
                AnimeStatus.NOT_YET_AIRED -> locale["$LOCALE_PREFIX.status.not_yet_aired"]
                AnimeStatus.FINISHED_AIRING -> locale["$LOCALE_PREFIX.status.finished"]
                else -> locale["$LOCALE_PREFIX.unknown"]
            }, true)
            // "Aired at" status
            addField("\uD83D\uDCC6 " + locale["$LOCALE_PREFIX.status.aired"], anime.info.aired, true)
            // MAL scoring stuff
            addField("⭐ " + locale["$LOCALE_PREFIX.score"], score, true)
            addField("\uD83C\uDF1F " + locale["$LOCALE_PREFIX.rank"], rank, true)
            addField("\uD83E\uDD29 " + locale["$LOCALE_PREFIX.popularity"], popularity, true)
            // Episodes, duration, genres and source info
            addField(locale["$LOCALE_PREFIX.episodes"], anime.info.episodes?.toString()
                    ?: locale["$LOCALE_PREFIX.unknown"], true)
            addField("⏲️ " + locale["$LOCALE_PREFIX.duration"], anime.info.duration
                    ?: locale["$LOCALE_PREFIX.unknown"], true)
            addField("\uD83C\uDFF7️ " + locale["$LOCALE_PREFIX.genres"], anime.info.genres!!.joinToString(", "), true)
            addField("\uD83D\uDD17 " + locale["$LOCALE_PREFIX.source"], anime.info.source, true)
            // Synopsis!
            setDescription(anime.synopsis!!.substringIfNeeded())
        }

        return embed
    }
}