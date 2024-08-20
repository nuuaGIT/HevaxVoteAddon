package de.nuua.hevaxVoteAddon;

import de.nuua.hevaxVoteAddon.Commands.*;
import de.nuua.hevaxVoteAddon.Commands.Abstimmung.jaCommand;
import de.nuua.hevaxVoteAddon.Commands.Abstimmung.neinCommand;
import de.nuua.hevaxVoteAddon.Listener.banListener;
import de.nuua.hevaxVoteAddon.Listener.mutedListener;
import de.nuua.hevaxVoteAddon.TabComplete.universalTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // load config
        getConfig().options().copyDefaults();
        saveDefaultConfig();



        universalTabCompleter tabCompleter = new universalTabCompleter(this);

        getCommand("votewetter").setExecutor(new votewetterCommand(this));
        getCommand("votewetter").setTabCompleter(tabCompleter);

        getCommand("votezeit").setExecutor(new votezeitCommand(this));
        getCommand("votezeit").setTabCompleter(tabCompleter);

        getCommand("votekick").setExecutor(new votekickCommand(this));
        getCommand("votekick").setTabCompleter(tabCompleter);

        getCommand("votemute").setExecutor(new votemuteCommand(this));
        getCommand("votemute").setTabCompleter(tabCompleter);

        getCommand("voteban").setExecutor(new votebanCommand(this));
        getCommand("voteban").setTabCompleter(tabCompleter);

        getCommand("ja").setExecutor(new jaCommand(this));
        getCommand("nein").setExecutor(new neinCommand(this));


        Bukkit.getPluginManager().registerEvents(new mutedListener(), this);
        Bukkit.getPluginManager().registerEvents(new banListener(this), this);


    }

    @Override
    public void onDisable() {
        FileConfiguration config = getConfig();
        // alle laufenden abstimmungen entfernen
        for (String key : config.getKeys(false)) {
            if (key.startsWith("vote_") || key.startsWith("vote_zeit_")) {
                config.set(key, null);
                System.out.println("abstimmungen geloescht");
            }
        }

        // alle bans entfernen
        config.set("banned_players", null);
        System.out.println("bans geloescht");

        saveConfig();
        System.out.println("Loeschung gespeichert");
    }

}
