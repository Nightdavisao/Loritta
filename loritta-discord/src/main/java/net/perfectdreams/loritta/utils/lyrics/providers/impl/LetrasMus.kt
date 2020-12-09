package net.perfectdreams.loritta.utils.lyrics.providers.impl

import com.mrpowergamerbr.loritta.commands.vanilla.music.LyricsCommand
import com.mrpowergamerbr.loritta.utils.encodeToUrl
import net.perfectdreams.loritta.utils.lyrics.LyricsUtil
import net.perfectdreams.loritta.utils.lyrics.models.SongInfo
import net.perfectdreams.loritta.utils.lyrics.providers.LyricsProvider
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.io.IOException

class LetrasMus(
        override val name: String = "letras.mus.br",
        override val baseURL: String = "a",
        override val icon: String? = null): LyricsProvider {

    override fun queryLyrics(query: String): SongInfo? {
        val split = LyricsUtil.splitToArtistAndName(query) ?: return null
        val artist = split.artistName
        val musicName = split.songName

        val response = try {
            Jsoup.connect("https://www.letras.mus.br/${artist.replace(" ", "-").encodeToUrl()}/${musicName.replace(" ", "-").encodeToUrl()}/")
                    .ignoreHttpErrors(true)
                    .execute()
        } catch (e: IOException) { // O letras.mus tem um bug de redirecionamento infinito, vamos ignorar caso isto aconteça
            return null
        }

        if (response.statusCode() == 404)
            return null

        val document = response.parse()
        val headTitle = document.getElementsByClass("cnt-head_title").firstOrNull() ?: return null
        val h1 = headTitle.getElementsByTag("h1") ?: return null
        val h2 = headTitle.getElementsByTag("h2") ?: return null

        val songTitle = h1.text()
        if (!songTitle.contains(musicName, true))
            return null

        val songLyricsDiv = document.getElementsByClass("cnt-letra").firstOrNull() ?: return null

        val lyrics = songLyricsDiv.html()
        val avatarUrl = headTitle.getElementsByTag("img").firstOrNull()?.attr("src")

        return SongInfo(
                h2.text() ?: artist,
                songTitle ?: musicName,
                avatarUrl,
                Jsoup.clean(lyrics
                        .replace("<br>", "\n")
                        .replace("<p>", "\n"),
                        "localhost",
                        Whitelist.none(),
                        Document.OutputSettings().prettyPrint(false)
                ),
                response.url().toString()
        )
    }

}