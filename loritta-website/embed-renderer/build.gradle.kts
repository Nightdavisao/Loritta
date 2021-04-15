plugins {
	kotlin("js")
	kotlin("plugin.serialization") version Versions.KOTLIN
}

kotlin {
	js {
		browser()
	}

	sourceSets {
		js().compilations["main"].defaultSourceSet {
			dependencies {
				implementation(project(":loritta-api"))
				implementation(project(":loritta-serializable-commons"))
				api("org.jetbrains.kotlinx:kotlinx-html-js:0.6.11")
				api("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KOTLIN_SERIALIZATION}")
			}
		}
	}
}