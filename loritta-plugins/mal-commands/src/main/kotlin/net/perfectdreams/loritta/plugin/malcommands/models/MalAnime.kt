package net.perfectdreams.loritta.plugin.malcommands.models

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import net.perfectdreams.loritta.plugin.malcommands.utils.adapter.SimpleDateAdapter
import java.util.*

data class AnimeQueryData(
    val data: List<Anime>
)

data class Anime(val node: AnimeNode)

data class AnimeNode(
    val id: Int,
    val title: String,
    @SerializedName("start_date")
    @JsonAdapter(SimpleDateAdapter::class)
    val startDate: Long?,
    @SerializedName("end_date")
    @JsonAdapter(SimpleDateAdapter::class)
    val endDate: Long?,
    @SerializedName("main_picture")
    val pictures: AnimePictures,
    val genres: List<AnimeGenre>,
    val alternativeTitles: AnimeAlternativeTitles?,
    val mean: Double?,
    val rank: Int?,
    val popularity: Int?,
    val status: AnimeStatus,
    val nsfw: String,
    @SerializedName("num_episodes")
    val episodes: Int,
    @SerializedName("average_episode_duration")
    val duration: Int,
    @SerializedName("media_type")
    val mediaType: AnimeType?,
    val synopsis: String
)

data class AnimePictures(
    val large: String,
    val medium: String
)

data class AnimeGenre(
    val id: Int,
    val name: String
)

data class AnimeAlternativeTitles(
    @SerializedName("en")
    val english: String,
    @SerializedName("ja")
    val japanese: String,
    val synonyms: List<String>
)


enum class AnimeStatus {
    @SerializedName("finished_airing")
    FINISHED_AIRING,

    @SerializedName("currently_airing")
    CURRENTLY_AIRING,

    @SerializedName("not_yet_aired")
    NOT_YET_AIRED
}

enum class AnimeType {
    @SerializedName("tv")
    TV,

    @SerializedName("movie")
    MOVIE,

    @SerializedName("ova")
    OVA,

    @SerializedName("ona")
    ONA,

    @SerializedName("special")
    SPECIAL
}