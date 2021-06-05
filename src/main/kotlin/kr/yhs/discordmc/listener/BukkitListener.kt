package kr.yhs.discordmc.listener

import io.papermc.paper.event.player.AsyncChatEvent
import kr.yhs.discordmc.Main
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*

class BukkitListener(private val plugin: Main) : Listener {
    private fun replacePlayer(player: Player, formatter: String): String {
        return formatter.replace("<player>", player.name)
            .replace("<address>", player.address.address.hostAddress)
            .replace("<exp>", "${player.exp}")
            .replace("<level>", "${player.level}")
    }

    private fun replaceMsgFormat(event: AsyncChatEvent, formatter: String): String {
        val player: String = replacePlayer(event.player, formatter)
        val message: String = (event.message() as TextComponent).content()
        return player.replace("<message>", message)
    }

    private fun replaceAccessFormat(event: PlayerEvent, formatter: String): String {
        val player: String = replacePlayer(event.player, formatter)
        return player.replace("<online>", "${Bukkit.getOnlinePlayers().size}")
            .replace("<max>", "${Bukkit.getMaxPlayers()}")
    }

    private fun replaceDeathFormat(event: PlayerDeathEvent, formatter: String): String {
        val player: String = replacePlayer(event.entity.player!!, formatter)
        return player.replace("<message>", (event.deathMessage() as TextComponent).content())
    }

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val channel = Main.jda?.getTextChannelById(plugin.config.getString("channelId")!!)

        val format: String = plugin.config.getString("chatFormat") ?: "**<player>**: <message>"
        channel?.sendMessage(replaceMsgFormat(event, format))?.queue()
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (!plugin.config.getBoolean("accessEnable")) return
        val channel = Main.jda?.getTextChannelById(plugin.config.getString("channelId")!!)

        val format: String =
            plugin.config.getString("joinFormat") ?: "**<player>**님이 게임에 들어왔습니다. 현재 플레이어 수: <online>/<max>명"
        channel?.sendMessage(replaceAccessFormat(event, format))?.queue()
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        if (!plugin.config.getBoolean("accessEnable")) return
        val channel = Main.jda?.getTextChannelById(plugin.config.getString("channelId")!!)
        val format: String =
            plugin.config.getString("leaveFormat") ?: "**<player>**님이 게임에서 나갔습니다. 현재 플레이어 수: <online>/<max>명"
        channel?.sendMessage(replaceAccessFormat(event, format))?.queue()
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (event.entityType != EntityType.PLAYER) return
        if (!plugin.config.getBoolean("deathEnable")) return

        val channel = Main.jda?.getTextChannelById(plugin.config.getString("channelId")!!)
        val format: String = plugin.config.getString("deathFormat") ?: "**<player>님이 사망 하셨습니다.**"
        channel?.sendMessage(replaceDeathFormat(event, format))?.queue()
    }

    @EventHandler
    fun onPlayerKick(event: PlayerKickEvent) {
        if (!plugin.config.getBoolean("kickEnable")) return
        val channel = Main.jda?.getTextChannelById(plugin.config.getString("channelId")!!)
        val format: String = plugin.config.getString("kickFormat") ?: "**<player>님이 추방 하셨습니다.**"
        channel?.sendMessage(replaceAccessFormat(event, format))?.queue()
    }

    @EventHandler
    fun onPlayerAdvancement(event: PlayerAdvancementDoneEvent) {
        return // 작업 예정
    }
}