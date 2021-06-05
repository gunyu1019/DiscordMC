package kr.yhs.discordmc

import kr.yhs.discordmc.listener.DiscordListener
import kr.yhs.discordmc.listener.BukkitListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
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
            .addEventListeners(DiscordListener(this))

        jda = builder.build()
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
