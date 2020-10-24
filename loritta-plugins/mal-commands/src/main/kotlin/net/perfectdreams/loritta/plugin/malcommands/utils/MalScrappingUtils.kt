package net.perfectdreams.loritta.plugin.malcommands.utils

import net.perfectdreams.loritta.plugin.malcommands.models.MalPeriod
import java.text.SimpleDateFormat
import java.util.*

object MalScrappingUtils {
    fun parsePeriod(str: String): MalPeriod {
        val split = str.split(" to ")
        return MalPeriod(
                parseDate(split[0]),
                parseDate(split[1])
        )
    }

    fun parseDate(str: String): Date? = SimpleDateFormat("MMM DD, yyyy").parse(str)

}