package de.nuua.hevaxVoteAddon.Commands.Abstimmung;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class jaCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public jaCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von Spielern ausgeführt werden.");
            return true;
        }

        Player player = (Player) sender;
        FileConfiguration config = plugin.getConfig();
        String activeVoteId = getActiveVoteId(config);

        if (activeVoteId == null) {
            player.sendMessage(ChatColor.RED + "§8» §c✘ §8⋆ §7Es läuft derzeit keine Abstimmung.");
            return true;
        }

        if (hasPlayerVoted(config, activeVoteId, player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.RED + "§8» §c✘ §8⋆ §7Du hast für diese Abstimmung bereits abgestimmt.");
            return true;
        }

        int yesVotes = config.getInt(activeVoteId + ".yes_votes", 0);
        config.set(activeVoteId + ".yes_votes", yesVotes + 1);

        config.set(activeVoteId + ".voters." + player.getUniqueId().toString(), "yes");

        plugin.saveConfig();

        player.sendMessage("§8» §a✔ §8⋆ §7Du hast §aerfolgreich §7für §a§lJA §7gestimmt.");

        return true;
    }

    private String getActiveVoteId(FileConfiguration config) {
        for (String key : config.getKeys(false)) {
            if (key.startsWith("vote_")) {
                long expirationDate = config.getLong(key + ".expiration_date", 0);
                if (System.currentTimeMillis() < expirationDate) {
                    return key;
                }
            }
        }
        return null;
    }

    private boolean hasPlayerVoted(FileConfiguration config, String voteId, String playerUuid) {
        return config.contains(voteId + ".voters." + playerUuid);
    }
}