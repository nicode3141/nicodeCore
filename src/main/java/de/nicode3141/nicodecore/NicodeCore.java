package de.nicode3141.nicodecore;

import de.nicode3141.nicodecore.commands.newspawn;
import de.nicode3141.nicodecore.commands.spawn;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class NicodeCore extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Plugin NicodeCore loaded!");

        getCommand("spawn").setExecutor(new spawn(this));
        getCommand("newspawn").setExecutor(new newspawn(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
