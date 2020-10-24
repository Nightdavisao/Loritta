package net.perfectdreams.loritta.plugin.malcommands.utils

import com.github.kevinsawicki.http.HttpRequest
import com.github.salomonbrys.kotson.*
import com.google.gson.JsonParser
import com.mrpowergamerbr.loritta.utils.encodeToUrl
import mu.KotlinLogging
import net.perfectdreams.loritta.plugin.malcommands.models.*
import net.perfectdreams.loritta.plugin.malcommands.utils.MalConstants.MAL_URL

object AnimeUtils {
    private val logger = KotlinLogging.logger { }

    private fun getAnimeTypeEnum(type: String?): AnimeType? {
        return when (type?.toLowerCase()) {
            "tv" -> AnimeType.TV
            "ona" -> AnimeType.ONA
            "movie" -> AnimeType.MOVIE
            "ova" -> AnimeType.OVA
            "special" -> AnimeType.SPECIAL
            else -> null
        }
    }

    fun getEmoteForAnimeType(type: AnimeType): String {
        return when (type) {
            AnimeType.MOVIE, AnimeType.OVA -> "\uD83C\uDFA5"
            AnimeType.ONA -> "\uD83D\uDDA5️"
            AnimeType.SPECIAL -> "\uD83C\uDF1F"
            else -> "\uD83D\uDCFA"
        }
    }

    fun queryAnime(query: String): List<PartialAnime> {
        val response = HttpRequest.get(
                "${MAL_URL}search/prefix.json?type=anime&keyword=${query.encodeToUrl()}&v=1"
        ).body()
        val parsed = JsonParser.parseString(response)

        return parsed.obj["categories"].array[0].obj["items"].array.map {
            val jsonObject = it.obj
            PartialAnime(
                    jsonObject["id"].int,
                    jsonObject["name"].string,
                    getAnimeTypeEnum(jsonObject.obj["payload"].obj["media_type"].string.toLowerCase()),
                    jsonObject["url"].string
            )
        }
    }

    fun parseAnime(url: String): MalAnime? {
        val document = ScrappingUtils.requestDocument(url)!!

        val animeInfo = AnimeInfo(
                name = document.selectFirst(".title-name").text().trim(),
                type = getAnimeTypeEnum(document.findElementByString("span", "Type:")
                        ?.nextElementSibling()
                        ?.text()
                        ?.trim()),
                status = when (document.findElementByString("span", "Status:")
                        ?.nextSiblingString()?.toLowerCase()) {
                    "finished airing" -> AnimeStatus.FINISHED_AIRING
                    "currently airing" -> AnimeStatus.CURRENTLY_AIRING
                    "not yet aired" -> AnimeStatus.NOT_YET_AIRED
                    else -> null
                },
                aired = document.findElementByString("span", "Aired:")
                        ?.nextSiblingString(),
                duration = document.findElementByString("span", "Duration:")
                        ?.nextSiblingString(),
                episodes = document.findElementByString("span", "Episodes:")
                        ?.nextSiblingString()
                        ?.toIntOrNull(),
                source = document.findElementByString("span", "Source:")
                        ?.nextSiblingString(),
                genres = document.select("span[itemprop=\"genre\"]").map { it.text() }
        )

        logger.info { animeInfo.type }

        return MalAnime(
                info = animeInfo,
                image = document
                        .selectFirst("img[itemprop=\"image\"][alt=\"${animeInfo.name}\"]")
                        .attr("data-src"),
                score = document.selectFirst(".score-label")
                        .text()
                        ?.toFloatOrNull(),
                synopsis = document.selectFirst("p[itemprop=\"description\"]")
                        .preservedText(),
                rank = document.findElementByString("span", "Ranked:")
                        ?.nextSiblingString()
                        ?.asMalRank(),
                popularity = document.findElementByString("span", "Popularity:")
                        ?.nextSiblingString()
                        ?.asMalRank()
        )
    }

}
