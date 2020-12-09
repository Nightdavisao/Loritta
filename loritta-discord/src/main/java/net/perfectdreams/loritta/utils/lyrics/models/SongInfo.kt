package net.perfectdreams.loritta.utils.lyrics.models

data class SongInfo(
        val artistName: String,
        val songName: String,
        val artworkUrl: String?,
        val lyrics: String,
        val href: String?
)
