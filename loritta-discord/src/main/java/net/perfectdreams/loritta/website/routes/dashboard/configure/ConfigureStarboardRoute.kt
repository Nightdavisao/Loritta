package net.perfectdreams.loritta.website.routes.dashboard.configure

import com.mrpowergamerbr.loritta.Loritta
import com.mrpowergamerbr.loritta.network.Databases
import com.mrpowergamerbr.loritta.utils.locale.BaseLocale
import com.mrpowergamerbr.loritta.website.evaluate
import io.ktor.application.ApplicationCall
import net.dv8tion.jda.api.entities.Guild
import net.perfectdreams.loritta.platform.discord.LorittaDiscord
import net.perfectdreams.loritta.website.routes.dashboard.RequiresGuildAuthLocalizedRoute
import net.perfectdreams.loritta.website.session.LorittaJsonWebSession
import net.perfectdreams.loritta.website.utils.extensions.legacyVariables
import net.perfectdreams.loritta.website.utils.extensions.respondHtml
import net.perfectdreams.temmiediscordauth.TemmieDiscordAuth
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.collections.set

class ConfigureStarboardRoute(loritta: LorittaDiscord) : RequiresGuildAuthLocalizedRoute(loritta, "/configure/starboard") {
	override suspend fun onGuildAuthenticatedRequest(call: ApplicationCall, locale: BaseLocale, discordAuth: TemmieDiscordAuth, userIdentification: LorittaJsonWebSession.UserIdentification, guild: Guild) {
		loritta as Loritta
		val serverConfig = loritta.getOrCreateServerConfig(guild.idLong)

		transaction(Databases.loritta) {
			serverConfig.starboardConfig
		}

		val variables = call.legacyVariables(locale)

		variables["saveType"] = "starboard"
		variables["serverConfig"] = FakeServerConfig(
				FakeServerConfig.FakeStarboardConfig(
						serverConfig.starboardConfig?.enabled ?: false,
						serverConfig.starboardConfig?.starboardChannelId?.toString(),
						serverConfig.starboardConfig?.requiredStars ?: 1
				)
		)

		call.respondHtml(evaluate("starboard.html", variables))
	}

	class FakeServerConfig(val starboardConfig: FakeStarboardConfig) {
		class FakeStarboardConfig(
				val isEnabled: Boolean,
				val starboardId: String?,
				val requiredStars: Int
		)
	}
}