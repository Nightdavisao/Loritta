package com.mrpowergamerbr.loritta.utils

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.managers.Presence
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Guarda todos os shards da Loritta
 */
class LorittaShards {
    var shards: MutableList<JDA> = CopyOnWriteArrayList<JDA>()
    val lastJdaEventTime = mutableMapOf<JDA, Long>()

    fun getGuildById(id: String): Guild? {
        for (shard in shards) {
            var guild = shard.getGuildById(id);
            if (guild != null) { return guild; }
        }
        return null;
    }

    fun getGuilds(): List<Guild> {
        // Pegar todas as guilds em todos os shards
        var guilds = ArrayList<Guild>();

        for (shard in shards) {
            guilds.addAll(shard.guilds);
        }
        return guilds;
    }

    fun getGuildCount(): Int {
        return shards.sumBy { it.guilds.size }
    }

    fun getUserCount(): Int {
        return shards.sumBy { it.users.size }
    }

    fun getUsers(): List<User> {
        // Pegar todas os users em todos os shards
        var users = ArrayList<User>();

        for (shard in shards) {
            users.addAll(shard.users);
        }

        var nonDuplicates = users.distinctBy { it.id }

        return nonDuplicates;
    }

    fun getUserById(id: String?): User? {
        for (shard in shards) {
            var user = shard.getUserById(id);
            if (user != null) {
                return user
            }
        }
        return null;
    }

    fun retriveUserById(id: String?): User? {
        return getUserById(id) ?: shards[0].retrieveUserById(id).complete()
    }

    fun getMutualGuilds(user: User): List<Guild> {
        // Pegar todas as mutual guilds em todos os shards
        var guilds = ArrayList<Guild>()
        for (shard in shards) {
            guilds.addAll(shard.getMutualGuilds(user))
        }
        return guilds;
    }

    fun getPresence(): Presence {
        // Pegar primeira shard e retornar a presença dela
        return shards[0].presence
    }

    /**
     * Atualiza a presença do bot em todas as shards
     */
    fun setGame(game: Game) {
        for (shard in shards) {
            shard.presence.game = game
        }
    }
}