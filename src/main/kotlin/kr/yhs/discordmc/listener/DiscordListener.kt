package kr.yhs.discordmc.listener

import kr.yhs.discordmc.Main
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

class DiscordListener(private val plugin: JavaPlugin) : ListenerAdapter() {
    private val listColor: List<String> = listOf(
        "<black>", "<dark_blue>", "<dark_green>", "<dark_aqua>", "<red>",
        "<dark_purple>", "<gold>", "<gray>", "<dark_gray>", "<blue>", "<green>", "<aqua>", "<purple>", "<yellow>",
        "<white>"
    )

    private fun replaceChatColor(message: String): String {
        return message.replace("<black>", "${ChatColor.BLACK}")
            .replace("<dark_blue>", "${ChatColor.DARK_BLUE}")
            .replace("<dark_green>", "${ChatColor.DARK_GREEN}")
            .replace("<dark_aqua>", "${ChatColor.DARK_AQUA}")
            .replace("<red>", "${ChatColor.DARK_RED}")
            .replace("<dark_purple>", "${ChatColor.DARK_PURPLE}")
            .replace("<gold>", "${ChatColor.GOLD}")
            .replace("<gray>", "${ChatColor.GRAY}")
            .replace("<dark_gray>", "${ChatColor.DARK_GRAY}")
            .replace("<blue>", "${ChatColor.BLUE}")
            .replace("<green>", "${ChatColor.GREEN}")
            .replace("<aqua>", "${ChatColor.AQUA}")
            .replace("<purple>", "${ChatColor.LIGHT_PURPLE}")
            .replace("<yellow>", "${ChatColor.YELLOW}")
            .replace("<white>", "${ChatColor.WHITE}")
            .replace("<magic>", "${ChatColor.MAGIC}")
            .replace("<bold>", "${ChatColor.BOLD}")
            .replace("<strikethrough>", "${ChatColor.STRIKETHROUGH}")
            .replace("<underline>", "${ChatColor.UNDERLINE}")
            .replace("<italic>", "${ChatColor.ITALIC}")
            .replace("<reset>", "${ChatColor.RESET}")
    }

    private fun replaceChatFormat(
        event: MessageReceivedEvent,
        formatter: String,
        markdown: Boolean,
        customColor: Boolean
    ): String {
        val customColorMessage: String

        val s: String = replaceChatColor(formatter)
        var markdownMessage = ""

        val stringMessage: String = event.message.contentRaw

        if (markdown) {
            var setBold = false
            var setColor: String? = null
            var setItalic = false
            var setUnderline = false
            var setStrikethrough = false

            var index = 0
            while (index <= stringMessage.length - 1) {
                val value: String = stringMessage.slice(index until stringMessage.length)
                var alreadyColor = false

                if (customColor) {
                    for (color in listColor) {
                        if (value.startsWith(color)) {
                            setColor = color
                            index += color.length
                            alreadyColor = true
                            break
                        }
                    }
                }

                if (value.startsWith("**")) {
                    if (setBold) {
                        setBold = false
                        markdownMessage += "${ChatColor.RESET}"
                        if (customColor && setColor != null) {
                            markdownMessage += setColor
                            index += setColor.length
                        }
                    } else {
                        setBold = true
                        markdownMessage += "${ChatColor.BOLD}"
                    }
                    index += 2
                } else if (value.startsWith("*")) {
                    if (setItalic) {
                        setItalic = false
                        markdownMessage += "${ChatColor.RESET}"
                        if (customColor && setColor != null) {
                            markdownMessage += setColor
                            index += setColor.length
                        }
                    } else {
                        setItalic = true
                        markdownMessage += "${ChatColor.ITALIC}"
                    }
                    index += 1
                } else if (value.startsWith("~~")) {
                    if (setStrikethrough) {
                        setStrikethrough = false
                        markdownMessage += "${ChatColor.RESET}"
                        if (customColor && setColor != null) {
                            markdownMessage += setColor
                            index += setColor.length
                        }
                    } else {
                        setStrikethrough = true
                        markdownMessage += "${ChatColor.STRIKETHROUGH}"
                    }
                    index += 2
                } else if (value.startsWith("__")) {
                    if (setUnderline) {
                        setUnderline = false
                        markdownMessage += "${ChatColor.RESET}"
                        if (customColor && setColor != null) {
                            markdownMessage += setColor
                            index += setColor.length
                        }
                    } else {
                        setUnderline = true
                        markdownMessage += "${ChatColor.UNDERLINE}"
                    }
                    index += 2
                } else {
                    if (!alreadyColor) {
                        index += 1
                        markdownMessage += value[0]
                    }
                }
            }
        } else {
            markdownMessage = stringMessage
        }

        customColorMessage = if (customColor) {
            replaceChatColor(markdownMessage)
        } else {
            markdownMessage
        }

        return s.replace("<message>", customColorMessage)
            .replace("<sender>", event.author.asTag)
            .replace("<mention>", event.author.asMention)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (!event.message.author.isBot) {
            if (event.channelType != ChannelType.PRIVATE) {
                if (event.channel.id == Main.instance?.config?.getString("channelId")) {
                    val format: String =
                        plugin.config.getString("messageFormat") ?: "<<dark_purple><sender><reset>> <message>"
                    val customColor: Boolean = plugin.config.getBoolean("customColor")
                    val supportMarkdown: Boolean = plugin.config.getBoolean("customColor")
                    val component = Component.text(replaceChatFormat(event, format, supportMarkdown, customColor))
                    Bukkit.broadcast(component)
                }
            }
        }
    }
}