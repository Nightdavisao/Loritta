package net.perfectdreams.loritta.sweetmorenitta.utils

import net.perfectdreams.loritta.utils.locale.BaseLocale
import com.mrpowergamerbr.loritta.utils.loritta
import kotlinx.html.DIV
import kotlinx.html.IMG
import kotlinx.html.ScriptType
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.fieldSet
import kotlinx.html.i
import kotlinx.html.id
import kotlinx.html.img
import kotlinx.html.ins
import kotlinx.html.legend
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.unsafe
import net.perfectdreams.loritta.utils.Sponsor

fun DIV.adWrapper(callback: DIV.() -> Unit) {
    // Wraps the div in a nice wrapper
    div {
        style = "text-align: center;"
        fieldSet {
            style = "display: inline;\n" +
                    "border: 2px solid rgba(0,0,0,.05);\n" +
                    "border-radius: 7px;\n" +
                    "color: rgba(0,0,0,.3);"

            legend {
                style = "margin-left: auto;"
                i("fas fa-ad")
            }

            div {
                callback.invoke(this)
            }
        }
    }
}

fun DIV.imgSrcSet(path: String, fileName: String, sizes: String, max: Int, min: Int, stepInt: Int, block : IMG.() -> Unit = {}) {
    val srcsets = mutableListOf<String>()
    val split = fileName.split(".")
    val ext = split.last()

    for (i in (max - stepInt) downTo min step stepInt) {
        // "${websiteUrl}$versionPrefix/assets/img/home/lori_gabi.png 1178w, ${websiteUrl}$versionPrefix/assets/img/home/lori_gabi_1078w.png 1078w, ${websiteUrl}$versionPrefix/assets/img/home/lori_gabi_978w.png 978w, ${websiteUrl}$versionPrefix/assets/img/home/lori_gabi_878w.png 878w, ${websiteUrl}$versionPrefix/assets/img/home/lori_gabi_778w.png 778w, ${websiteUrl}$versionPrefix/assets/img/home/lori_gabi_678w.png 678w, ${websiteUrl}$versionPrefix/assets/img/home/lori_gabi_578w.png 578w, ${websiteUrl}$versionPrefix/assets/img/home/lori_gabi_478w.png 478w, ${websiteUrl}$versionPrefix/assets/img/home/lori_gabi_378w.png 378w, ${websiteUrl}$versionPrefix/assets/img/home/lori_gabi_278w.png 278w, ${websiteUrl}$versionPrefix/assets/img/home/lori_gabi_178w.png 178w"
        srcsets.add("$path${split.first()}_${i}w.$ext ${i}w")
    }

    srcsets.add("$path$fileName ${max}w")

    imgSrcSet(
        "$path$fileName",
        sizes,
        srcsets.joinToString(", "),
        block
    )
}

fun DIV.imgSrcSet(filePath: String, sizes: String, srcset: String, block : IMG.() -> Unit = {})  {
    img(src = filePath) {
        style = "width: auto;"
        attributes["sizes"] = sizes
        attributes["srcset"] = srcset

        this.apply(block)
    }
}

fun DIV.generateNitroPayAdOrSponsor(
    sponsorId: Int,
    adSlot: String,
    displayTypes: List<NitroPayAdDisplay>
) {
    val sponsors = loritta.sponsors
    val sponsor = sponsors.getOrNull(sponsorId)

    if (sponsor != null) {
        generateSponsor(sponsor)
    } else {
        generateNitroPayAd(adSlot, displayTypes)
    }
}

fun DIV.generateNitroPayAd(adSlot: String, displayTypes: List<NitroPayAdDisplay>) {
    if (NitroPayAdDisplay.DESKTOP in displayTypes) {
        generateNitroPayAd(
            "$adSlot-desktop",
            listOf(
                NitroPayAdSize(
                    728,
                    90
                ),
                NitroPayAdSize(
                    970,
                    90
                ),
                NitroPayAdSize(
                    970,
                    250
                )
            ),
            NitroPayAdGenerator.FULL_WIDTH_CONTENT_DESKTOP_MEDIA_QUERY
        )
    }

    if (NitroPayAdDisplay.PHONE in displayTypes) {
        generateNitroPayAd(
            "$adSlot-phone",
            listOf(
                NitroPayAdSize(
                    300,
                    250
                ),
                NitroPayAdSize(
                    320,
                    50
                )
            ),
            NitroPayAdGenerator.FULL_WIDTH_CONTENT_PHONE_MEDIA_QUERY
        )
    }

    if (NitroPayAdDisplay.TABLET in displayTypes) {
        generateNitroPayAd(
            "$adSlot-tablet",
            listOf(
                NitroPayAdSize(
                    728,
                    90
                ),
                NitroPayAdSize(
                    970,
                    90
                ),
                NitroPayAdSize(
                    970,
                    250
                ),
                NitroPayAdSize(
                    300,
                    250
                ),
                NitroPayAdSize(
                    320,
                    50
                )
            ),
            NitroPayAdGenerator.FULL_WIDTH_CONTENT_TABLET_MEDIA_QUERY
        )
    }
}

fun DIV.generateNitroPayVideoAdOrSponsor(sponsorId: Int, adSlot: String, adName: String? = null, showIfSponsorIsMissing: Boolean = true, showOnMobile: Boolean = true)
        = generateNitroPayVideoAdOrSponsor(loritta.sponsors, sponsorId, adSlot, adName, showIfSponsorIsMissing)

fun DIV.generateNitroPayVideoAdOrSponsor(sponsors: List<Sponsor>, sponsorId: Int, adSlot: String, adName: String? = null, showIfSponsorIsMissing: Boolean = true, showOnMobile: Boolean = true) {
    val sponsor = sponsors.getOrNull(sponsorId)

    if (sponsor != null) {
        generateSponsor(sponsor)
    } else if (showIfSponsorIsMissing) {
        generateNitroPayVideoAd(adSlot)
    }
}

fun DIV.generateAdOrSponsor(sponsorId: Int, adSlot: String, adName: String? = null, showIfSponsorIsMissing: Boolean = true, showOnMobile: Boolean = true)
        = generateAdOrSponsor(loritta.sponsors, sponsorId, adSlot, adName, showIfSponsorIsMissing)

fun DIV.generateAdOrSponsor(sponsors: List<Sponsor>, sponsorId: Int, adSlot: String, adName: String? = null, showIfSponsorIsMissing: Boolean = true, showOnMobile: Boolean = true) {
    val sponsor = sponsors.getOrNull(sponsorId)

    if (sponsor != null) {
        generateSponsor(sponsor)
    } else if (showIfSponsorIsMissing) {
        generateAd(adSlot, adName, showOnMobile)
    }
}

fun DIV.generateSponsor(sponsor: Sponsor) {
    div(classes = "media") {
        generateSponsorNoWrap(sponsor)
    }
}

fun DIV.generateSponsorNoWrap(sponsor: Sponsor) {
    a(href = "/sponsor/${sponsor.slug}", classes = "sponsor-wrapper", target = "_blank") {
        div(classes = "sponsor-pc-image") {
            img(src = sponsor.getRectangularBannerUrl(), classes = "sponsor-banner")
        }
        div(classes = "sponsor-mobile-image") {
            img(src = sponsor.getSquareBannerUrl(), classes = "sponsor-banner")
        }
    }
}

fun DIV.generateHowToSponsorButton(locale: BaseLocale) {
    div(classes = "media") {
        style = "justify-content: end;"
        div {
            style = "font-size: 0.8em; margin: 8px;"
            + (locale["website.sponsors.wantYourServerHere"] + " ")
            a(href = "/sponsors") {
                span(classes = "sponsor-button") {
                    + "Premium Slots"
                }
            }
        }
    }
}

fun DIV.generateAd(adSlot: String, adName: String? = null, showOnMobile: Boolean = true) {
    // O "adName" não é utilizado para nada, só está aí para que fique mais fácil de analisar aonde está cada ad (caso seja necessário)
    // TODO: Random ID
    val adGen = "ad-$adSlot-"

    div(classes = "centralized-ad") {
        ins(classes = "adsbygoogle") {
            style = "display: block;"

            if (!showOnMobile)
                attributes["id"] = adGen

            attributes["data-ad-client"] = "ca-pub-9989170954243288"
            attributes["data-ad-slot"] = adSlot
            attributes["data-ad-format"] = "auto"
            attributes["data-full-width-responsive"] = "true"
        }
    }

    if (!showOnMobile) {
        script(type = ScriptType.textJavaScript) {
            unsafe {
                raw("if (document.body.clientWidth >= 1366) { (adsbygoogle = window.adsbygoogle || []).push({}); } else { console.log(\"Not displaying ad: Browser width is too smol!\"); document.querySelector(\"#$adGen\").remove(); }")
            }
        }
    } else {
        script(type = ScriptType.textJavaScript) {
            unsafe {
                raw("(adsbygoogle = window.adsbygoogle || []).push({});")
            }
        }
    }
}

fun DIV.generateNitroPayVideoAd(adId: String) {
    div(classes = "centralized-ad") {
        div(classes = "nitropay-ad") {
            id = adId
            attributes["data-nitropay-ad-type"] = NitroPayAdType.VIDEO_PLAYER.name.toLowerCase()
        }
    }
}