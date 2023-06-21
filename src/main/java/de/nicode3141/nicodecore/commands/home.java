package de.nicode3141.nicodecore.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class home implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Location homeLocation = new Location(Bukkit.getWorld("world"), 10 ,10 ,10);

        Player player = (Player) commandSender;

        player.teleport(homeLocation);

        return true;
    }
}
