package net.perfectdreams.spicymorenitta.utils

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLModElement
import org.w3c.dom.get

object NitroPay : Logging {
	@JsName("renderAds")
	fun renderAds() {
		if (window["nitroAds"] != undefined && window["nitroAds"].loaded == true) {
			info("NitroPay is loaded!")
			renderNitroPayAds()
		} else {
			info("NitroPay is not loaded yet! We are going to wait until the event is triggered to render the ads...")
			document.addEventListener("nitroAds.loaded", {
				// nitroAds just loaded
				renderNitroPayAds()
			})
		}
	}

	private fun renderNitroPayAds() {
		val ads = document.selectAll<HTMLModElement>(".nitropay-ad")

		debug("There are ${ads.size} NitroPay ads in the page...")

		ads.forEach {
			if (!it.hasAttribute("data-request-id")) {
				try {
					console.log(it)

					val adType = it.getAttribute("data-nitropay-ad-type")

					val dynamic = object {}.asDynamic()

					if (adType == "video_player") {
						dynamic.format = "video-ac"
					} else {
						dynamic.refreshLimit = 10
						dynamic.refreshTime = 30
						// Lazy loading
						dynamic.renderVisibleOnly = false
						dynamic.refreshVisibleOnly = true
						val adDisplay = it.getAttribute("data-nitropay-ad-display")

						if (adDisplay == "desktop") {
							dynamic.mediaQuery = "(min-width: 1025px)"
							dynamic.sizes = arrayOf<Array<String>>(
									arrayOf(
											"728",
											"90"
									),
									arrayOf(
											"970",
											"90"
									),
									arrayOf(
											"970",
											"250"
									)
							)
						} else if (adDisplay == "phone") {
							dynamic.mediaQuery = "(min-width: 320px) and (max-width: 767px)"
							dynamic.sizes = arrayOf<Array<String>>(
									arrayOf(
											"300",
											"250"
									),
									arrayOf(
											"320",
											"50"
									)
							)
						} else if (adDisplay == "tablet") {
							dynamic.mediaQuery = "(min-width: 768px) and (max-width: 1024px)"
							dynamic.sizes = arrayOf<Array<String>>(
									arrayOf(
											"728",
											"90"
									),
									arrayOf(
											"970",
											"90"
									),
									arrayOf(
											"970",
											"250"
									),
									arrayOf(
											"300",
											"250"
									),
									arrayOf(
											"320",
											"50"
									)
							)
						} else {
							dynamic.sizes = arrayOf<Any?>()
						}

						val report = object {}.asDynamic()
						report.enabled = true
						report.wording = "Report Ad"
						report.position = "top-right"
						dynamic.report = report

						console.log(dynamic)
					}

					window["nitroAds"].createAd(it.id, dynamic)

					debug("Yay!")
				} catch (e: Throwable) {
					console.log(e)
				}
			}
		}
	}
}