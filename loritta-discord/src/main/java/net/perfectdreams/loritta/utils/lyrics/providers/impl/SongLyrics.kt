package net.perfectdreams.loritta.utils.lyrics.providers.impl


import com.mrpowergamerbr.loritta.utils.encodeToUrl
import net.perfectdreams.loritta.utils.lyrics.LyricsUtil
import net.perfectdreams.loritta.utils.lyrics.models.SongInfo
import net.perfectdreams.loritta.utils.lyrics.providers.LyricsProvider
import org.jsoup.Jsoup

class SongLyrics(
        override val name: String = "SongLyrics",
        override val baseURL: String = "http://www.songlyrics.com/",
        override val icon: String? = null): LyricsProvider {

    override fun queryLyrics(query: String): SongInfo? {
        val split = LyricsUtil.splitToArtistAndName(query) ?: return null
        val artist = split.artistName
        val musicName = split.songName

        val response = Jsoup.connect("http://www.songlyrics.com/${artist.replace(" ", "-").encodeToUrl()}/${musicName.replace(" ", "-").encodeToUrl()}-lyrics/")
                .ignoreHttpErrors(true)
                .execute()

        if (response.statusCode() == 404)
            return null

        val document = response.parse()
        val songLyricsDiv = document.getElementById("songLyricsDiv") ?: return null

        val lyrics = songLyricsDiv.html()

        return SongInfo(
                artist,
                musicName,
                null,
                lyrics.replace("<br>", "\n"),
                response.url().toString()
        )
    }
}