package net.perfectdreams.loritta.utils.lyrics.providers

import net.perfectdreams.loritta.utils.lyrics.models.SongInfo

interface LyricsProvider {
    val name: String
    val baseURL: String
    val icon: String?

    fun queryLyrics(query: String): SongInfo?
}