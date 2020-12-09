package net.perfectdreams.loritta.utils.lyrics.models

import net.perfectdreams.loritta.utils.lyrics.providers.LyricsProvider

data class LyricsInfo(
        val provider: LyricsProvider,
        val songInfo: SongInfo
)