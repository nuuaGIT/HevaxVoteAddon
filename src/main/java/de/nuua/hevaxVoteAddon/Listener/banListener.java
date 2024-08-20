package de.nuua.hevaxVoteAddon.Listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class banListener implements Listener {


    private final JavaPlugin plugin;

    public banListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();
        long banEndTime = config.getLong("banned_players." + player.getUniqueId(), 0);

        if (banEndTime > System.currentTimeMillis()) {
            long remainingTime = (banEndTime - System.currentTimeMillis()) / 60000;
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cDu bist noch für " + remainingTime + " Minuten gebannt.");
        } else if (banEndTime != 0) {
            config.set("banned_players." + player.getUniqueId(), null);
            plugin.saveConfig();
        }
    }
}
