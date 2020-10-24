package net.perfectdreams.loritta.plugin.malcommands.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.safety.Whitelist

// TODO: move this to out of there
fun Document.findElementByString(element: String, string: String): Element? {
    return this.selectFirst("$element:contains($string)")
}

// TODO: move this to out of there
fun Element.nextSiblingString(): String? {
    return this.nextSibling().toString().removeHtmlEntities()
            ?.trim()
}

// TODO: move this to out of there
fun Element.preservedText(): String? {
    val document = this
    document.select("br").before("\\n")
    document.select("p").before("\\n")
    return document.html().replace("\\n", "\n").removeHtmlEntities()
}

// TODO: move this to out of there
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