package net.perfectdreams.loritta.utils.lyrics.providers.impl

import com.mrpowergamerbr.loritta.utils.encodeToUrl
import net.perfectdreams.loritta.utils.lyrics.LyricsUtil
import net.perfectdreams.loritta.utils.lyrics.providers.LyricsProvider
import net.perfectdreams.loritta.utils.lyrics.models.SongInfo
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist

class WikiaLyric(
        override val name: String = "Lyrics Wikia",
        override val baseURL: String = "http://lyrics.wikia.com/wiki/",
        override val icon: String? = null) : LyricsProvider {

    override fun queryLyrics(query: String): SongInfo? {
        val split = LyricsUtil.splitToArtistAndName(query) ?: return null
        val artist = split.artistName
        val musicName = split.songName

        val response = Jsoup.connect("http://lyrics.wikia.com/wiki/${artist.replace(" ", "_").encodeToUrl()}:${musicName.replace(" ", "_").encodeToUrl()}")
                .ignoreHttpErrors(true)
                .execute()

        if (response.statusCode() == 404)
            return null

        val document = response.parse()
        val lyricsBody = document.getElementsByClass("lyricbox")

        if (lyricsBody.isEmpty())
            return null

        val lyrics = lyricsBody.first().html()

        var albumArtUrl: String? = null

        val songHeader = document.getElementById("song-header-container")
        if (songHeader != null) {
            val albumUrlElement = songHeader.getElementsByTag("a").lastOrNull()

            if (albumUrlElement != null) {
                val albumUrl = albumUrlElement.attr("href")

                if (!albumUrl.contains("redlink=1")) {
                    val albumResponse = Jsoup.connect("http://lyrics.wikia.com${albumUrl}")
                            .ignoreHttpErrors(true)
                            .execute()

                    if (albumResponse.statusCode() == 200) {
                        val albumDocument = albumResponse.parse()
                        val contentText = albumDocument.getElementsByClass("plainlinks").firstOrNull()

                        if (contentText != null) {
                            val albumElement = contentText.getElementsByClass("image-thumbnail").firstOrNull()

                            albumArtUrl = albumElement?.attr("href")
                        }
                    }
                }
            }
        }

        val songTitle = document.getElementById("song-header-title")?.text()

        return SongInfo(
                artist,
                songTitle ?: musicName,
                albumArtUrl,
                Jsoup.clean(lyrics, "localhost", Whitelist.none(), Document.OutputSettings().prettyPrint(false)),
                response.url().toString()
        )
    }
}