package de.nicode3141.nicodecore.commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Random;

public class rtp implements CommandExecutor {
    private final HashMap<String, Long> cooldowns = new HashMap<>();
    private final Plugin plugin;

    public rtp(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(!(sender instanceof Player)){
            sender.sendMessage("This command is only for players!");
            return true;
        }

        World world = Bukkit.getWorld("world");

        int cooldownTime = plugin.getConfig().getInt("rtp.cooldown");
        int countdownTime = plugin.getConfig().getInt("rtp.countdown");

        Player player = (Player) sender;

        if (cooldowns.containsKey(player.getName())) {
            long secondsLeft = ((cooldowns.get(player.getName()) / 1000 + cooldownTime) - System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                player.sendMessage(ChatColor.DARK_GREEN + "[nicodeCore] " + ChatColor.RED + "Please wait " + secondsLeft + " seconds before teleporting again!");
                return true;
            }
        }

        cooldowns.put(player.getName(), System.currentTimeMillis());

        Location spawnIslandMid = new Location(Bukkit.getWorld("world"), 10,70,10);

        int innerRadius = plugin.getConfig().getInt("rtp.innerRadius"); //MIN Distance
        int outerRadius = plugin.getConfig().getInt("rtp.outerRadius"); //MAX Distance

        player.sendMessage(ChatColor.DARK_GREEN + "[nicodeCore] " + ChatColor.GREEN + "You will be teleported in " + 5 + " seconds");

        Location randomLocation;
        Block block;
        int tries = 0;
        do {
            // Same Random func as in the spawn class
            Random random = new Random();
            double angle = random.nextDouble() * 2 * Math.PI; //Random angle
            double x = Math.sin(angle) * (outerRadius + innerRadius) + 0.5; //Random Pos around the donut + 0.5 prevents stuck players...
            double z = Math.cos(angle) * (outerRadius + innerRadius) + 0.5; //Same here for z (also known as y)
            double y = world.getHighestBlockYAt((int) x + spawnIslandMid.getBlockX(), (int) z + spawnIslandMid.getBlockZ()) + 1;

            randomLocation = new Location(Bukkit.getWorld("world"), x + spawnIslandMid.getBlockX(), y, z + spawnIslandMid.getBlockZ());

            block = world.getBlockAt((int) x + spawnIslandMid.getBlockX(),(int) y -1, (int) z + spawnIslandMid.getBlockZ());
            tries++;

        } while ((block.getType() == Material.WATER || block.getType() == Material.LAVA || block.getZ() < 60) || tries < plugin.getConfig().getInt("rtp.maxTries"));


        for (int i = countdownTime; i > 0; i--) {
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
        player.teleport(randomLocation);
        try {
            Thread.sleep(1300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        player.playSound(randomLocation, "entity.player.levelup", 100, 0.7F);

        return true;
    }
}
