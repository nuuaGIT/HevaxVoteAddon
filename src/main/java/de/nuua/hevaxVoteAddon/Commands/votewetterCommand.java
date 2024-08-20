package de.nuua.hevaxVoteAddon.Commands;

import de.nuua.hevaxVoteAddon.Manager.VoteCooldownManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class votewetterCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private String currentVoteId;

    public votewetterCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("hevax.votewetter")) {
            sender.sendMessage("§8» §c✘ §8⋆ §7Dazu hast du keine Berechtigung. Nutze §f/hilfe");
            return true;
        }

        if (!VoteCooldownManager.canVote(false)) {
            long remainingTime = VoteCooldownManager.getRemainingCooldown(false) / 60000; // Konvertiere zu Minuten
            sender.sendMessage("§8» §e⚠ §8⋆ §7Die letzte Abstimmung wurde erst beendet. Warte noch §b" + remainingTime + " Minuten§7.");
            return true;
        }


        if (args.length != 2) {
            sender.sendMessage("§8» §e⚠ §8⋆ §7Nutze: §f/votewetter §b<Sonne/Regen> <Plotwelt/Farmwelt>");
            return false;
        }
        if (!(args[0].equalsIgnoreCase("sonne") || args[0].equalsIgnoreCase("regen"))) {
            sender.sendMessage("§8» §e⚠ §8⋆ §7Nutze: §f/votewetter §b<Sonne/Regen> <Plotwelt/Farmwelt>");
            return false;
        }
        if (!(args[1].equalsIgnoreCase("Plotwelt") || args[1].equalsIgnoreCase("Farmwelt"))) {
            sender.sendMessage("§8» §e⚠ §8⋆ §7Nutze: §f/votewetter §b<Sonne/Regen> <Plotwelt/Farmwelt>");
            return false;
        }
        if (VoteCooldownManager.isVoteActive()) {
            sender.sendMessage("§8» §c✘ §8⋆ §7Es läuft bereits eine Abstimmung.");
            return false;
        }

        currentVoteId = "vote_wetter_" + System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        config.set(currentVoteId + ".weather", args[0]);
        config.set(currentVoteId + ".world", args[1]);
        config.set(currentVoteId + ".yes_votes", 0);
        config.set(currentVoteId + ".no_votes", 0);
        config.set(currentVoteId + ".creation_date", System.currentTimeMillis());
        config.set(currentVoteId + ".expiration_date", System.currentTimeMillis() + 120000); // 2 minutes
        plugin.saveConfig();

        TextComponent yesComponent = new TextComponent("§8  | §a§lJA");
        yesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ja"));
        yesComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §fKlick mich! §8(/ja)").create()));
        TextComponent noComponent = new TextComponent("§8  | §c§lNEIN");
        noComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nein"));
        noComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §fKlick mich! §8(/nein)").create()));

        String wetterC = args[0].substring(0, 1).toUpperCase() + args[0].substring(1).toLowerCase();
        String worldC = args[1].substring(0, 1).toUpperCase() + args[1].substring(1).toLowerCase();

        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8 ◇§m------------------§8» §b§lᴀʙѕᴛɪᴍᴍᴜɴɢ§8 §8«§m------------------§8◇");
        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8  | §7Eine Abstimmung für §f" + wetterC + " §7in §b" + worldC + "§7 wurde gestartet!");
        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8  §7ᴅᴇɪɴᴇ ѕᴛɪᴍᴍᴇ");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(yesComponent);
            player.spigot().sendMessage(noComponent);
        }
        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8 ◇§m------------------§8» §b§lᴀʙѕᴛɪᴍᴍᴜɴɢ§8 §8«§m------------------§8◇");
        Bukkit.broadcastMessage("§8");

        VoteCooldownManager.setVoteActive(true);

        new BukkitRunnable() {
            int countdown = 120;

            @Override
            public void run() {
                if (countdown <= 0) {
                    endVote();
                    cancel();
                } else if (countdown == 60) {
                    Bukkit.broadcastMessage("§8");
                    Bukkit.broadcastMessage("§8 ◇§m------------------§8» §b§lᴀʙѕᴛɪᴍᴍᴜɴɢ§8 §8«§m------------------§8◇");
                    Bukkit.broadcastMessage("§8");
                    Bukkit.broadcastMessage("§8  | §7Eine Abstimmung für §f" + wetterC + " §7in §b" + worldC + "§7 läuft!");
                    Bukkit.broadcastMessage("§8  | §f§oEs bleiben §b§l60 §r§b§oSekunden§f.");
                    Bukkit.broadcastMessage("§8");
                    Bukkit.broadcastMessage("§8  §7ᴅᴇɪɴᴇ ѕᴛɪᴍᴍᴇ");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.spigot().sendMessage(yesComponent);
                        player.spigot().sendMessage(noComponent);
                    }
                    Bukkit.broadcastMessage("§8");
                    Bukkit.broadcastMessage("§8 ◇§m------------------§8» §b§lᴀʙѕᴛɪᴍᴍᴜɴɢ§8 §8«§m------------------§8◇");
                    Bukkit.broadcastMessage("§8");
                } else if (countdown <= 5) {
                    Bukkit.broadcastMessage("§8» §b§lᴀʙѕᴛɪᴍᴍᴜɴɢ §8⋆ §7Abstimmungsende in §b" + String.valueOf(countdown) + " Sekunden§7!");
                }
                countdown--;
            }
        }.runTaskTimer(plugin, 0, 20);
        return true;
    }


    private void endVote() {
        FileConfiguration config = plugin.getConfig();
        int yesVotes = config.getInt(currentVoteId + ".yes_votes", 0);
        int noVotes = config.getInt(currentVoteId + ".no_votes", 0);
        String weather = config.getString(currentVoteId + ".weather");
        String world = config.getString(currentVoteId + ".world");

        String wetterC = weather.substring(0, 1).toUpperCase() + weather.substring(1).toLowerCase();
        String worldC = world.substring(0, 1).toUpperCase() + world.substring(1).toLowerCase();

        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8 ◇§m------------------§8» §b§lᴀʙѕᴛɪᴍᴍᴜɴɢ§8 §8«§m------------------§8◇");
        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8  | §7Eine Abstimmung für §f" + wetterC + " §7in §b" + worldC + "§f");
        Bukkit.broadcastMessage("§8  | §7wurde §aerfolgreich §7beendet!");
        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8  §7ѕᴛɪᴍᴍᴇɴ");
        Bukkit.broadcastMessage("§8  | §a§lJA§8·········§f§l" + yesVotes + " §7Stimmen");
        Bukkit.broadcastMessage("§8  | §c§lNEIN§8···§f§l" + noVotes + " §7Stimmen");
        Bukkit.broadcastMessage("§8");

        if (yesVotes > noVotes) {
            changeWeather(weather, world);
            Bukkit.broadcastMessage(ChatColor.GREEN + "§8  | §fDas Wetter wurde §ageändert§f.");
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "§8  | §fDas Wetter bleibt §cunverändert§f.");
        }
        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8 ◇§m------------------§8» §b§lᴀʙѕᴛɪᴍᴍᴜɴɢ§8 §8«§m------------------§8◇");
        Bukkit.broadcastMessage("§8");

        config.set(currentVoteId, null);
        plugin.saveConfig();
        currentVoteId = null;

        VoteCooldownManager.setLastVoteTime(false);
        VoteCooldownManager.setVoteActive(false);
    }

    private void changeWeather(String weather, String world) {
        String useWeather = weather.toLowerCase();
        String useWorld=  world.toUpperCase();

        switch (useWeather) {
            case "regen":
                Bukkit.getWorld(useWorld).setStorm(true);
                break;
            case "sonne":
                Bukkit.getWorld(useWorld).setStorm(false);
                Bukkit.getWorld(useWorld).setThundering(false);
                break;
        }


    }
}