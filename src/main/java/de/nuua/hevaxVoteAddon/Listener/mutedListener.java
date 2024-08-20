package de.nuua.hevaxVoteAddon.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

import static de.nuua.hevaxVoteAddon.Commands.votemuteCommand.isMuted;

public class mutedListener implements Listener {
    @EventHandler
    public void onMutedChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if(isMuted(p)) {
            e.setCancelled(true);
            p.sendMessage("§8» §c✘ §8⋆ §cDu wurdest für §l15 Minuten §cgestummt.");
        }
    }


    @EventHandler
    public void onMutedCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (isMuted(player)) {
            String command = event.getMessage().split(" ")[0].toLowerCase();
            List<String> blockedCommands = Arrays.asList(
                    "/msg",
                    "/r",
                    "/message",
                    "/reply",
                    "/werbung"
            );
            if (blockedCommands.contains(command)) {
                event.setCancelled(true);
                player.sendMessage("§8» §c✘ §8⋆ §cDu wurdest für §l15 Minuten §cgestummt.");
            }
        }
    }

}


