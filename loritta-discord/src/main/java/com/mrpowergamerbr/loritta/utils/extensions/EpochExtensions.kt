package com.mrpowergamerbr.loritta.utils.extensions

import com.mrpowergamerbr.loritta.utils.locale.BaseLocale
import java.text.DateFormatSymbols
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

/**
 * "Humanizes" the date
 *
 * @param locale the language that should be used to humanize the date
 * @return       the humanized date
 */
fun OffsetDateTime.humanize(locale: BaseLocale, hours: Boolean = true): String {
	val localeId = locale.id
	val fixedOffset = this.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime()
	val months = DateFormatSymbols(locale.toJavaLocale()).months

	return if (localeId == "en-us") {
		val fancy = when (this.dayOfMonth) {
			1 -> "st"
			2 -> "nd"
			3 -> "rd"
			else -> "th"
		}
		if (hours)
			"${this.dayOfMonth}$fancy of ${months[this.month.value - 1]}, ${fixedOffset.year} at ${fixedOffset.hour.toString().padStart(2, '0')}:${fixedOffset.minute.toString().padStart(2, '0')}"
		else
			"${this.dayOfMonth}$fancy of ${months[this.month.value - 1]}, ${fixedOffset.year}"
	} else {
		if (hours)
			"${this.dayOfMonth} de ${months[this.month.value - 1]}, ${fixedOffset.year} às ${fixedOffset.hour.toString().padStart(2, '0')}:${fixedOffset.minute.toString().padStart(2, '0')}"
		else
			"${this.dayOfMonth} de ${months[this.month.value - 1]}, ${fixedOffset.year}"
	}
}

/**
 * "Humanizes" the date
 *
 * @param locale the language that should be used to humanize the date
 * @return       the humanized date
 */
fun Long.humanize(locale: BaseLocale, hours: Boolean = true): String {
	return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toOffsetDateTime().humanize(locale, hours)
}