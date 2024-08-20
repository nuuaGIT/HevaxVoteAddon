package de.nuua.hevaxVoteAddon.Commands;

import de.nuua.hevaxVoteAddon.Manager.VoteCooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class votemuteCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private String currentVoteId;
    private static final Map<UUID, Long> mutedPlayers = new HashMap<>();

    public votemuteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hevax.votemute")) {
            sender.sendMessage("§8» §c✘ §8⋆ §7Du hast keine Berechtigung für diesen Befehl.");
            return true;
        }

        if (!VoteCooldownManager.canVote(false)) {
            long remainingTime = VoteCooldownManager.getRemainingCooldown(false) / 60000; // Konvertiere zu Minuten
            sender.sendMessage("§8» §e⚠ §8⋆ §7Die letzte Abstimmung wurde erst beendet. Warte noch §b" + remainingTime + " Minuten§7.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§8» §e⚠ §8⋆ §7Nutze: §f/votemute §b<Spielername> <Grund>");
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("§8» §c✘ §8⋆ §7Der Spieler §f" + args[0] + " §7ist nicht online.");
            return false;
        }


        if (VoteCooldownManager.isVoteActive()) {
            sender.sendMessage("§8» §c✘ §8⋆ §7Es läuft bereits eine Abstimmung.");
            return false;
        }


        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        currentVoteId = "vote_mute_" + System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        config.set(currentVoteId + ".target", targetPlayer.getName());
        config.set(currentVoteId + ".reason", reason);
        config.set(currentVoteId + ".yes_votes", 0);
        config.set(currentVoteId + ".no_votes", 0);
        config.set(currentVoteId + ".creation_date", System.currentTimeMillis());
        config.set(currentVoteId + ".expiration_date", System.currentTimeMillis() + 120000);
        plugin.saveConfig();

        TextComponent yesComponent = new TextComponent("§8  | §a§lJA");
        yesComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ja"));
        yesComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §fKlick mich! §8(/ja)").create()));
        TextComponent noComponent = new TextComponent("§8  | §c§lNEIN");
        noComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nein"));
        noComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8» §fKlick mich! §8(/nein)").create()));

        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8 ◇§m------------------§8» §b§lᴀʙѕᴛɪᴍᴍᴜɴɢ§8 §8«§m------------------§8◇");
        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8  | §7Eine Abstimmung zum §fStummen §7von §b" + targetPlayer.getName() + " §7wurde gestartet!");
        Bukkit.broadcastMessage("§8  | §7Grund: §f" + reason);
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
                    Bukkit.broadcastMessage("§8  | §7Eine Abstimmung zum §fStummen §7von §b" + targetPlayer.getName() + " §7läuft!");
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
        String targetPlayer = config.getString(currentVoteId + ".target");
        //String reason = config.getString(currentVoteId + ".reason");

        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8 ◇§m------------------§8» §b§lᴀʙѕᴛɪᴍᴍᴜɴɢ§8 §8«§m------------------§8◇");
        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8  | §7Eine Abstimmung zum §fStummen §7von §b" + targetPlayer + "§f");
        Bukkit.broadcastMessage("§8  | §7wurde §aerfolgreich §7beendet!");
        Bukkit.broadcastMessage("§8");
        Bukkit.broadcastMessage("§8  §7ѕᴛɪᴍᴍᴇɴ");
        Bukkit.broadcastMessage("§8  | §a§lJA§8·········§f§l" + yesVotes + " §7Stimmen");
        Bukkit.broadcastMessage("§8  | §c§lNEIN§8···§f§l" + noVotes + " §7Stimmen");
        Bukkit.broadcastMessage("§8");

        if (yesVotes > noVotes) {
            Player player = Bukkit.getPlayer(targetPlayer);
            if (player != null) {
                mutePlayer(player);
                Bukkit.broadcastMessage("§8  | §f" + targetPlayer + " §7wurde für 15 Minuten §agestummt§f.");
            } else {
                Bukkit.broadcastMessage("§8  | §c§l" + targetPlayer + " §cist nicht mehr online.");
            }
        } else {
            Bukkit.broadcastMessage("§8  | §f" + targetPlayer + " §7bleibt ungestummt.");
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

    private void mutePlayer(Player player) {
        mutedPlayers.put(player.getUniqueId(), System.currentTimeMillis() + 900000); // 15 minutes

        new BukkitRunnable() {
            @Override
            public void run() {
                mutedPlayers.remove(player.getUniqueId());
                player.sendMessage("§8» §a✔ §8⋆ §aDu kannst nun wieder schreiben!");
            }
        }.runTaskLater(plugin, 18000); // 15 minutes = 18000 ticks
    }

    public static boolean isMuted(Player player) {
        Long muteEnd = mutedPlayers.get(player.getUniqueId());
        return muteEnd != null && System.currentTimeMillis() < muteEnd;
    }



}
