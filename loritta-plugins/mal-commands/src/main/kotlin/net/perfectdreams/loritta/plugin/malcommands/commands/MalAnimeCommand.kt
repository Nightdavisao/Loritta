package net.perfectdreams.loritta.plugin.malcommands.commands

import com.mrpowergamerbr.loritta.utils.*
import com.mrpowergamerbr.loritta.utils.extensions.edit
import com.mrpowergamerbr.loritta.utils.extensions.humanize
import com.mrpowergamerbr.loritta.utils.locale.BaseLocale
import mu.KotlinLogging
import net.dv8tion.jda.api.EmbedBuilder
import net.perfectdreams.loritta.api.commands.ArgumentType
import net.perfectdreams.loritta.api.commands.CommandCategory
import net.perfectdreams.loritta.platform.discord.commands.DiscordAbstractCommandBase
import net.perfectdreams.loritta.plugin.malcommands.MalCommandsPlugin
import net.perfectdreams.loritta.plugin.malcommands.models.*
import net.perfectdreams.loritta.plugin.malcommands.utils.MalConstants.MAL_COLOR
import net.perfectdreams.loritta.plugin.malcommands.utils.AnimeUtils
import net.perfectdreams.loritta.plugin.malcommands.utils.MalConstants.MAL_URL

class MalAnimeCommand(m: MalCommandsPlugin) : DiscordAbstractCommandBase(m.loritta, listOf("malanime", "anime"), CommandCategory.ANIME) {
    private val LOCALE_PREFIX = "commands.command.malanime"
    private val logger = KotlinLogging.logger { }
    private val maximumIndexAmount = 9 // Used for the maximum amount of indexes on search

    override fun command() = create {
        localizedDescription("$LOCALE_PREFIX.description")
        localizedExamples("$LOCALE_PREFIX.examples")

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
                        setTitle("Resultados da pesquisa: \"$query\"")
                        setColor(MAL_COLOR)
                        setDescription(
                                buildString {
                                    this.append("Para escolher você precisa escrever de acordo com o número ao lado do nome do anime (entendeu?)\n\n")
                                    for (i in 0 until maximumIndexAmount.coerceAtMost(animeQueries.size)) {
                                        val anime = animeQueries[i].node
                                        logger.info { anime }
                                        val animeUrl = MAL_URL + "anime/${anime.id}"
                                        val emoji = AnimeUtils.getEmoteForAnimeType(anime.mediaType)
                                        this.append("${Constants.INDEXES[i]} $emoji • [${anime.title}](${animeUrl})\n")
                                    }
                                }
                        )
                        setFooter(MAL_URL)
                    }.build()
            )

            message.onResponseByAuthor(
                    this.discordMessage.author.idLong,
                    this.guild.idLong,
                    this.discordMessage.channel.idLong) {
                val index = it.message.contentStripped.toIntOrNull()

                if (index != null) {
                    if (index in 0..maximumIndexAmount) {
                        val anime = animeQueries[index - 1].node

                        message.edit(
                                this@executesDiscord.getUserMention(true),
                                createAnimeEmbed(
                                    MAL_URL + "anime/${anime.id}",
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

    private fun createAnimeEmbed(url: String, anime: AnimeNode, locale: BaseLocale): EmbedBuilder {
        logger.debug { anime.toString() }

        val emoji = AnimeUtils.getEmoteForAnimeType(anime.mediaType) + " "

        val embed = EmbedBuilder()

        val score = if (anime.mean != null) anime.mean.toString() else locale["$LOCALE_PREFIX.unknown"]
        val rank = if (anime.rank != null) "#${anime.rank}" else locale["$LOCALE_PREFIX.unknown"]
        val popularity = if (anime.popularity != null) "#${anime.popularity}" else locale["$LOCALE_PREFIX.unknown"]

        embed.apply {
            setTitle(emoji + anime.title, url)
            setColor(MAL_COLOR)
            setThumbnail(anime.pictures.large)
            // Anime type (TV, special, OVA, etc)
            addField(locale["$LOCALE_PREFIX.type.name"], when (anime.mediaType) {
                AnimeType.TV -> locale["$LOCALE_PREFIX.type.tv"]
                AnimeType.SPECIAL -> locale["$LOCALE_PREFIX.type.special"]
                AnimeType.OVA -> locale["$LOCALE_PREFIX.type.ova"]
                AnimeType.ONA -> locale["$LOCALE_PREFIX.type.ona"]
                AnimeType.MOVIE -> locale["$LOCALE_PREFIX.type.movie"]
                else -> locale["$LOCALE_PREFIX.unknown"]
            }, true)
            // Anime airing status
            addField(locale["$LOCALE_PREFIX.status.name"], when (anime.status) {
                AnimeStatus.CURRENTLY_AIRING -> locale["$LOCALE_PREFIX.status.airing"]
                AnimeStatus.NOT_YET_AIRED -> locale["$LOCALE_PREFIX.status.not_yet_aired"]
                AnimeStatus.FINISHED_AIRING -> locale["$LOCALE_PREFIX.status.finished"]
                else -> locale["$LOCALE_PREFIX.unknown"]
            }, true)

            val startDate = anime.startDate?.humanize(locale, false)
            val endDate = anime.endDate?.humanize(locale, false)

            // TODO: locale
            val date = if (anime.startDate == null && anime.endDate == null)
                locale["$LOCALE_PREFIX.unknown"]
            else if (anime.endDate == null)
                "$startDate até ?"
            else if (anime.startDate == null)
                "? até $endDate"
            else
                "$startDate até $endDate"

            val duration = anime.duration / 60

            addField("\uD83D\uDCC6 " + locale["$LOCALE_PREFIX.status.aired"], date, true)
            // MAL scoring stuff
            addField("⭐ " + locale["$LOCALE_PREFIX.score"], score, true)
            addField("\uD83C\uDF1F " + locale["$LOCALE_PREFIX.rank"], rank, true)
            addField("\uD83E\uDD29 " + locale["$LOCALE_PREFIX.popularity"], popularity, true)
            // Episodes, duration, genres and source info
            addField(locale["$LOCALE_PREFIX.episodes"], anime.episodes.toString(), true)
            // TODO: locale
            addField("⏲️ " + locale["$LOCALE_PREFIX.duration"], "$duration minutos", true)
            addField("\uD83C\uDFF7️ " + locale["$LOCALE_PREFIX.genres"],
                anime.genres.joinToString(", ") { it.name }, true)
            // Synopsis!
            setDescription(anime.synopsis.substringIfNeeded())
            setFooter(MAL_URL)
        }

        return embed
    }
}