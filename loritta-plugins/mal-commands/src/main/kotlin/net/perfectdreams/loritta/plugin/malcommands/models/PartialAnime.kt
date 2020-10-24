package net.perfectdreams.loritta.plugin.malcommands.models

data class PartialAnime(
        val id: Int,
        val name: String,
        val mediaType: AnimeType?,
        val url: String
)