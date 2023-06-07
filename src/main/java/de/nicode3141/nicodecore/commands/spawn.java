package de.nicode3141.nicodecore.commands;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

        Location spawnIslandMid = new Location(Bukkit.getWorld("world"), 10,70,10);

        int innerRadius = 5;
        int outerRadius = 5;

        Random random = new Random();
        double angle = random.nextDouble() * 2 * Math.PI; // Random angle around the donut
        double x = Math.sin(angle) * (outerRadius + innerRadius) + 0.5; // Offset by 0.5 to prevent being stuck in blocks
        double z = Math.cos(angle) * (outerRadius + innerRadius) + 0.5;


        Location spawnLocation = new Location(Bukkit.getWorld("world"), x + spawnIslandMid.getBlockX(),spawnIslandMid.getBlockY(),z + spawnIslandMid.getBlockZ());

        Player player = (Player) sender;

        Location playerLocation = player.getLocation();

        if(playerLocation.distance(spawnLocation) < 10){
            player.sendMessage("§e[nicodeCore] §cYou are already in the spawn area!");

        } else {

            int cooldownTime = 2;
            int countdownTime;

            if (playerLocation.distance(spawnLocation) < 99) {
                countdownTime = 1;
            } else {
                countdownTime = (int) Math.round(playerLocation.distance(spawnLocation) / 100);
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

            for (int i = countdownTime; i > 0; i--) {
                if(i == 10 || i <= 5){
                    player.sendMessage(ChatColor.RED + String.valueOf(i) + ChatColor.YELLOW + " seconds to be teleported!");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            player.teleport(spawnLocation);
            player.sendMessage("§a[nicodeCore] You have been teleported to Spawn!");

            return true;
        }
        return true;
    }
}
