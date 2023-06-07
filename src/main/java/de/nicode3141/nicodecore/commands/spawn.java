package de.nicode3141.nicodecore.commands;

import java.util.HashMap;
import java.util.Objects;

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

        int randXLocation, randYLocation;

        int xIO = (int) (Math.random() * 2);
        int yIO = (int) (Math.random() * 2);

        if(xIO%2 == 0) {
            randXLocation = -(int) (Math.random() * outerRadius) + innerRadius*2;
        }else {
            randXLocation = (int) (Math.random() * outerRadius) + innerRadius*2;
        }

        double sqrt = Math.sqrt((Math.pow(outerRadius + innerRadius, 2) - Math.pow(innerRadius, 2) - Math.pow(randXLocation, 2)));
        if(yIO%2 == 0) {
            randYLocation = -(int) sqrt;
        }else {
            randYLocation = (int) sqrt;
        }


        Location spawnLocation = new Location(Bukkit.getWorld("world"), randXLocation + spawnIslandMid.getBlockX(),spawnIslandMid.getBlockY(),randYLocation + spawnIslandMid.getBlockZ());

        Player player = (Player) sender;

        Location playerLocation = player.getLocation();

        if(playerLocation.distance(spawnLocation) < 10){
            player.sendMessage("§e[nicodeCore] §cYou are already in the spawn area!");

        } else {

            int cooldownTime;

            if (playerLocation.distance(spawnLocation) < 99) {
                cooldownTime = 1;
            } else {
                cooldownTime = (int) Math.round(playerLocation.distance(spawnLocation) / 100);
            }

            if (cooldowns.containsKey(player.getName())) {
                long secondsLeft = ((cooldowns.get(player.getName()) / 1000 + cooldownTime) - System.currentTimeMillis() / 1000);
                if (secondsLeft > 0) {
                    player.sendMessage(ChatColor.RED + "You must wait " + secondsLeft + " seconds before teleporting to spawn again!");
                    return true;
                }
            }

            cooldowns.put(player.getName(), System.currentTimeMillis());

            player.sendMessage(ChatColor.RED + String.valueOf(cooldownTime) +  ChatColor.YELLOW + " seconds to be teleported!");

            for (int i = cooldownTime; i > 0; i--) {
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
