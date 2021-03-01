package net.perfectdreams.loritta.plugin.malcommands.utils

import com.github.kevinsawicki.http.HttpRequest
import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import mu.KotlinLogging
import net.perfectdreams.loritta.plugin.malcommands.models.*

object AnimeUtils {
    private val logger = KotlinLogging.logger { }
    private val animeFields = listOf(
        "alternative_titles",
        "media_type",
        "num_episodes",
        "status",
        "start_date",
        "end_date",
        "average_episode_duration",
        "synopsis",
        "mean",
        "genres",
        "rank",
        "popularity",
        "num_list_users",
        "num_favorites",
        "favorites_info",
        "num_scoring_users",
        "start_season",
        "broadcast",
        "my_list_status{start_date,finish_date}",
        "nsfw",
        "created_at",
        "updated_at"
    )

    fun getEmoteForAnimeType(type: AnimeType?): String {
        return when (type) {
            AnimeType.MOVIE, AnimeType.OVA -> "\uD83C\uDFA5"
            AnimeType.ONA -> "\uD83D\uDDA5️"
            AnimeType.SPECIAL -> "\uD83C\uDF1F"
            else -> "\uD83D\uDCFA"
        }
    }

    fun queryAnime(query: String): List<Anime> {
        val requestHeaders = mapOf(
            // fun fact: client id is hardcoded in resource strings from Android app
            "x-mal-client-id" to "df368c0b8286b739ee77f0b905960700",
            "user-agent" to "MAL (android, 1.0.8)",
            "cache-control" to "public, max-age=60"
        )
        val request = HttpRequest.get(
            "https://api.myanimelist.net/v3/anime", true,
            "q", query,
            "limit", "10", // default is 30
            "offset", "0",
            "fields", animeFields.joinToString(","),
        )
            .acceptGzipEncoding()
            .headers(requestHeaders)

        val body = request.body()
        logger.debug { body }

        val gsonBuilder = GsonBuilder().create()
        return gsonBuilder.fromJson<AnimeQueryData>(body).data
    }

}
