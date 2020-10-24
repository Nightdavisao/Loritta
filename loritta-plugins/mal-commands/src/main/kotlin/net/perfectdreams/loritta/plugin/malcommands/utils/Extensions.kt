package net.perfectdreams.loritta.plugin.malcommands.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.safety.Whitelist

fun Document.findElementByString(element: String, string: String): Element? {
    return this.selectFirst("$element:contains($string)")
}

fun Element.nextSiblingString(): String? {
    return this.nextSibling().toString().removeHtmlEntities()
            ?.trim()
}

fun Element.preservedText(): String? {
    val document = this
    document.select("br").before("\\n")
    document.select("p").before("\\n")
    return document.html().replace("\\n", "\n").removeHtmlEntities()
}

fun String.removeHtmlEntities(): String? {
    return Jsoup.clean(
            this,
            "",
            Whitelist.none(),
            Document.OutputSettings().prettyPrint(false))
}

fun String.asMalRank(): Int? {
    return this.trim().removePrefix("#").toIntOrNull()
}