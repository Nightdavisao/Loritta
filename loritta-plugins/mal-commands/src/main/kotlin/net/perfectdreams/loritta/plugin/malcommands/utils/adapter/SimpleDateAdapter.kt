package net.perfectdreams.loritta.plugin.malcommands.utils.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SimpleDateAdapter: JsonDeserializer<Long> {
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    private val fallbackFormatter = SimpleDateFormat("yyyy")

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long? {
        return try {
            formatter.parse(json!!.asString).toInstant().toEpochMilli()
        } catch (e: ParseException) {
            fallbackFormatter.parse(json!!.asString).toInstant().toEpochMilli()
        } catch (e: Exception) {
            null
        }
    }
}