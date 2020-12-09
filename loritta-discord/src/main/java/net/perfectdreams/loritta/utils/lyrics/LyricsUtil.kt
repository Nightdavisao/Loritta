package net.perfectdreams.loritta.utils.lyrics

import mu.KotlinLogging
import net.perfectdreams.loritta.utils.lyrics.models.LyricsInfo
import net.perfectdreams.loritta.utils.lyrics.models.PartialSongInfo
import net.perfectdreams.loritta.utils.lyrics.providers.impl.LetrasMus
import net.perfectdreams.loritta.utils.lyrics.providers.impl.SongLyrics
import net.perfectdreams.loritta.utils.lyrics.providers.impl.WikiaLyric

object LyricsUtil {
    val logger = KotlinLogging.logger {  }

    val providers = mutableListOf(
            WikiaLyric(),
            SongLyrics(),
            LetrasMus()
    )

    fun findLyricsByQuery(query: String): LyricsInfo? {
        for (provider in providers) {
            try {
                val songInfo = provider.queryLyrics(query)

                if (songInfo != null) {
                    return LyricsInfo(
                            provider,
                            songInfo
                    )
                }
            } catch (e: Exception) {
                logger.warn(e) { "Something wrong happened when querying lyrics using ${provider::class.simpleName}" }
                continue
            }
        }
        return null
    }

    fun splitToArtistAndName(str: String): PartialSongInfo? {
        val split = str.split(" - ")
        return if (split.size >= 2)
            PartialSongInfo(split[0], split.drop(1).joinToString(" - "))
        else
            null
    }
}