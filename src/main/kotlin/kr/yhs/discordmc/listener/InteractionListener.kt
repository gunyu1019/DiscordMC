package kr.yhs.discordmc.listener

import kr.yhs.discordmc.Main
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class InteractionListener(private val plugin: JavaPlugin) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when {
            event.name.equals("online") -> {
                val noMember: String? = plugin.config.getString("NoMemberMessage")
                val title: String? = plugin.config.getString("embedTitle")
                val color: Int = plugin.config.getInt("embedColor")
                val field1: String? = plugin.config.getString("embedField1")
                val field2: String? = plugin.config.getString("embedField2")

                val embed = EmbedBuilder().setTitle(title ?: "**온라인 유저**")
                    .setColor(color)
                    .addField(
                        field1 ?: "인원:",
                        "${Bukkit.getOnlinePlayers().size}명/ ${Bukkit.getMaxPlayers()}명",
                        false
                    )

                var memberStr = "```\n"
                if (Bukkit.getOnlinePlayers().isNotEmpty()) {
                    for ((i, player) in Bukkit.getOnlinePlayers().withIndex()) {
                        memberStr += "$i 번째: ${player.name}\n"
                    }
                } else {
                    memberStr += "${noMember ?: "사람이 없습니다."}\n"
                }
                memberStr += "```"

                embed.addField(field2 ?: "목록:", memberStr, false)
                if (Main.serverAddress != null) embed.setFooter("========${Main.serverAddress ?: ""}========")

                val result: MessageEmbed = embed.build()
                event.replyEmbeds(result).queue()
            }
            else -> super.onSlashCommandInteraction(event)
        }
    }
}