package kr.yhs.discordmc

import kr.yhs.discordmc.listener.DiscordListener
import kr.yhs.discordmc.listener.BukkitListener
import kr.yhs.discordmc.listener.InteractionListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin(), Listener {
    companion object {
        var jda: JDA? = null
        var instance: Main? = null
        var serverAddress: String? = null
    }

    override fun onEnable() {
        instance = this
        if (!dataFolder.exists()) {
            this.saveDefaultConfig()
            Bukkit.getLogger().info("DiscordMC - Initialized configuration!")
        }
        val builder = JDABuilder.createDefault(this.config.getString("token"))
            .setActivity(Activity.playing("Minecraft : $serverAddress"))
            .enableIntents(GatewayIntent.GUILD_MESSAGES)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .disableCache(CacheFlag.VOICE_STATE)
            .addEventListeners(DiscordListener(this))
            .addEventListeners(InteractionListener(this))

        jda = builder.build()
        if (this.config.getBoolean("onlineCommandEnable")) {
            val commandName = this.config.getString("onlineCommandName")?: "online"
            val commandDescription = this.config.getString("onlineCommandDescription")?: "서버에 접속한 사용자 정보를 불러옵니다."
            jda!!.updateCommands().addCommands(
                Commands.slash(commandName, commandDescription)
            ).queue()
        }
        Bukkit.getLogger().info("DiscordMC - Succeed JDA(Discord Bot) enabled!")

        server.pluginManager.registerEvents(this, this)
        Bukkit.getLogger().info("DiscordMC - Paper Event registered")

        server.pluginManager.apply {
            registerEvents(BukkitListener(this@Main), this@Main)
        }
        Bukkit.getLogger().info("DiscordMC - Plugin load done.")
    }

    override fun onDisable() {
        jda?.shutdown()
        Bukkit.getLogger().info("DiscordMC - JDA(Discord Bot) is Shutdown.")
    }
}
