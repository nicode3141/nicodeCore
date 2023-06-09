package de.nicode3141.nicodecore.commands;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class spawn implements CommandExecutor {
    private final Plugin plugin;
    private final HashMap<String, Long> cooldowns = new HashMap<>();

    public spawn(Plugin plugin) {
        this.plugin = plugin;
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players are able to execute this command!");
            return true;
        }

        Location spawnIslandMid = new Location(Bukkit.getWorld("world"), plugin.getConfig().getInt("spawnCoordinates.x"), plugin.getConfig().getInt("spawnCoordinates.y"), plugin.getConfig().getInt("spawnCoordinates.z"));

        int innerRadius = plugin.getConfig().getInt("innerRadius");
        int outerRadius = plugin.getConfig().getInt("outerRadius");

        Random random = new Random();
        int angle = (int) ((int) random.nextDouble() * 2 * Math.PI); // Random angle
        short x = (short) ((short) Math.sin(angle) * (outerRadius + innerRadius) + 0.5); // Offset by 0.5 to prevent being stuck in Blocks
        short z = (short) ((short) Math.cos(angle) * (outerRadius + innerRadius) + 0.5); // Same here (z in Minecraft is the y axis in irl)...


        Location spawnLocation = new Location(Bukkit.getWorld("world"), x + spawnIslandMid.getBlockX(),spawnIslandMid.getBlockY(),z + spawnIslandMid.getBlockZ());

        Player player = (Player) sender;

        Location playerLocation = player.getLocation();

        if(playerLocation.distance(spawnLocation) < 10){
            player.sendMessage("§e[nicodeCore] §cYou are already in the spawn area!");

        } else {

            int cooldownTime = plugin.getConfig().getInt("cooldown");
            short countdownTime;

            if (playerLocation.distance(spawnLocation) < 99) {
                countdownTime = 1;
            } else if( (int) Math.round(playerLocation.distance(spawnLocation)) >= 3200){
                countdownTime = (short) Math.round(playerLocation.distance(spawnLocation) / 100);
            } else {
                countdownTime = 32;
            }

            if (cooldowns.containsKey(player.getName())) {
                long secondsLeft = ((cooldowns.get(player.getName()) / 1000 + cooldownTime) - System.currentTimeMillis() / 1000);
                if (secondsLeft > 0) {
                    player.sendMessage(ChatColor.RED + "You must wait " + secondsLeft + " seconds before teleporting to spawn again!");
                    return true;
                }
            }

            cooldowns.put(player.getName(), System.currentTimeMillis());

            player.sendMessage(ChatColor.RED + String.valueOf(countdownTime) +  ChatColor.YELLOW + " seconds to be teleported!");

            for (short i = countdownTime; i > 0; i--) {
                if(i == 10 || i <= 5){
                    player.sendTitle(ChatColor.GREEN + String.valueOf(i), "teleporting...", 1, 15, 4);
                    player.playNote(player.getLocation(), Instrument.PIANO, Note.flat(1, Note.Tone.C));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            player.teleport(spawnLocation);
            player.playSound(spawnLocation, "entity.player.levelup", 100, 0.7F);
            player.sendMessage("§a[nicodeCore] You have been teleported to Spawn!");

            return true;
        }
        return true;
    }
}
