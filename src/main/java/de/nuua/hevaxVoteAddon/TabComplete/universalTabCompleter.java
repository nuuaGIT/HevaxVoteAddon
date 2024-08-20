package de.nuua.hevaxVoteAddon.TabComplete;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class universalTabCompleter implements TabCompleter {

    private final JavaPlugin plugin;

    public universalTabCompleter(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();


        // commands
        if (sender instanceof Player) {
            Player p = (Player) sender;

            switch (command.getName().toLowerCase()) {
                case "votewetter":
                    if(args.length == 1 && p.hasPermission("hevax.votewetter")) {
                        completions.add("Regen");
                        completions.add("Sonne");
                    } else if(args.length == 2 && p.hasPermission("hevax.votewetter")) {
                        completions.add("Plotwelt");
                        completions.add("Farmwelt");
                    }

                    break;




                case "votezeit":
                    if(args.length == 1 && p.hasPermission("hevax.votezeit")) {
                        completions.add("Morgen"); //23000
                        completions.add("Abend"); //12000
                        completions.add("Mitternacht"); //18000
                        completions.add("Mittag"); //6000
                    } else if(args.length == 2 && p.hasPermission("hevax.votezeit")) {
                        completions.add("Plotwelt");
                        completions.add("Farmwelt");
                    }

                    break;


                case "votekick":
                    if(args.length == 1 && p.hasPermission("hevax.votekick")) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            completions.add(onlinePlayer.getName());
                        }
                    } else if(args.length == 2 && p.hasPermission("hevax.votekick")) {
                        completions.add("<Grund>");
                    }

                    break;

                case "votemute":
                    if(args.length == 1 && p.hasPermission("hevax.votemute")) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            completions.add(onlinePlayer.getName());
                        }
                    } else if(args.length == 2 && p.hasPermission("hevax.votemute")) {
                        completions.add("<Grund>");
                    }

                    break;

                case "voteban":
                    if(args.length == 1 && p.hasPermission("hevax.voteban")) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            completions.add(onlinePlayer.getName());
                        }
                    } else if(args.length == 2 && p.hasPermission("hevax.votemute")) {
                        completions.add("<Grund>");
                    }

                    break;
            }
        }

        return completions;
    }
}