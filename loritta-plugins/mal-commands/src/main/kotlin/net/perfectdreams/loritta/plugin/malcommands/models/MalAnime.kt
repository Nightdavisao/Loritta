package net.perfectdreams.loritta.plugin.malcommands.models

class MalAnime(
        val image: String?,
        val info: AnimeInfo,
        val score: Float?,
        val synopsis: String?,
        val rank: Int?,
        val popularity: Int?
//        val background: String?,
//        val related: Array<String>?,
//        val characters: Array<String>?,
//        val staff: Array<String>?
)

class AnimeInfo(
        val name: String,
        val type: AnimeType?,
        // Alternative names usually are english, synonyms (and the original japanese name, of course)
//        val altNames: Array<String>,
        // Episodes can be null, because MAL also store upcoming animes
        val duration: String?,
        val episodes: Int?,
        val status: AnimeStatus?,
//        // "Aired" value can be null, because of non-available animes (?)
        val aired: String?,
//        val premiered: String,
//        val broadcast: String,
//        // producers and licensors can be null, because of the same reasons I mentioned above
//        val producers: Array<String>?,
//        val licensors: Array<String>?,
//        val studios: List<String>?,
        val source: String?,
        // we don't really need to make a enum class for this one
        val genres: List<String>?
//        val rating: String
)

enum class AnimeStatus {
    FINISHED_AIRING, CURRENTLY_AIRING, NOT_YET_AIRED
}

enum class AnimeType {
    TV, MOVIE, OVA, ONA, SPECIAL
}