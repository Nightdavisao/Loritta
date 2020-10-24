package net.perfectdreams.loritta.plugin.malcommands.utils

import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object ScrappingUtils {
    private val logger = KotlinLogging.logger {  }

    fun requestDocument(endpoint: String): Document? {
        val response = Jsoup.connect(endpoint)
                .ignoreHttpErrors(true)
                .execute()

        logger.debug { "Made request to $endpoint" }

        // Cover all the client and server errors
        if (response.statusCode() in 400..599) return null

        return response.parse()
    }
}