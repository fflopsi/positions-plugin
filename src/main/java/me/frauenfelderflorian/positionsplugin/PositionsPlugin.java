package me.frauenfelderflorian.positionsplugin;

import me.frauenfelderflorian.positionsplugin.command.PositionCompleter;
import me.frauenfelderflorian.positionsplugin.data.Positions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public final class PositionsPlugin extends JavaPlugin {
    private static final String[] INFO = {
            "This is a plugin written by Florian Frauenfelder for Minecraft Spigot.",
            "A command consists of the following parts, separated by a whitespace: " +
                    "\"/position\" (or the alias \"/pos\"), an option (and optionally the name of the position).",
            "The options are these:",
            "add: add new / overwrite position (at location of player) (name needed)",
            "show: show saved position (name needed)",
            "tp: teleport player (OP) to position (name needed)",
            "del: delete saved position (name needed)",
            "list: show all saved positions",
            "clear: delete all saved positions",
            "info: show this info message",
    };
    public Positions positions;
    private PositionCompleter tabCompleter;

    @Override
    public void onEnable() {
        positions = new Positions("positions.yml");
        positions.load();
        tabCompleter = new PositionCompleter(this);
        Objects.requireNonNull(getServer().getPluginCommand("position")).setTabCompleter(tabCompleter);
    }

    @Override
    public void onDisable() {
        positions.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("position".equals(command.getName())) {
            try {
                switch (args[0]) {
                    case "add" -> {
                        if (sender instanceof Player) {
                            positions.add(args[1], Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getLocation());
                            tabCompleter.reload();
                            Bukkit.broadcastMessage("added " + positions.positionToString(args[1]) + " to saved positions");
                        } else {
                            sender.sendMessage("you cannot add positions");
                        }
                    }
                    case "show" -> {
                        try {
                            sender.sendMessage(positions.positionToString(args[1]));
                        } catch (NullPointerException e) {
                            sender.sendMessage("this position does not exist");
                        }
                    }
                    case "tp" -> {
                        try {
                            if (sender instanceof Player && sender.isOp()) {
                                Objects.requireNonNull(Bukkit.getPlayer(sender.getName()))
                                        .teleport(positions.usablePositions.get(args[1]));
                                Bukkit.broadcastMessage("teleported " + sender.getName()
                                        + " to position " + positions.positionToString(args[1]));
                            } else {
                                sender.sendMessage("you are not allowed to do this");
                            }
                        } catch (NullPointerException e) {
                            sender.sendMessage("this position does not exist");
                        }
                    }
                    case "del" -> {
                        try {
                            Bukkit.broadcastMessage("deleted " + positions.positionToString(args[1]));
                            positions.delete(args[1]);
                            tabCompleter.reload();
                        } catch (NullPointerException e) {
                            sender.sendMessage("this position does not exist");
                        }
                    }
                    case "list" -> {
                        sender.sendMessage("all saved positions:");
                        SortedSet<String> keys = new TreeSet<>(positions.usablePositions.keySet());
                        for (String key : keys) {
                            sender.sendMessage(positions.positionToString(key));
                        }
                    }
                    case "clear" -> {
                        Bukkit.broadcastMessage("deleted all positions");
                        positions.clearAll();
                        tabCompleter.reload();
                    }
                    case "info" -> sender.sendMessage(INFO);
                    case "save" -> {
                        sender.sendMessage("This command is executed when something is changed, " +
                                "so there is usually no need to call it manually.");
                        positions.save();
                    }
                    case "load" -> {
                        sender.sendMessage("This command is executed when the plugin is being enabled, " +
                                "so there is usually no need to call it manually.");
                        positions.load();
                    }
                    default -> sender.sendMessage("not a valid command");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                sender.sendMessage("This is not a valid command for the plugin \"Positions\".");
                sender.sendMessage("To see more info about this plugin, type \"/position info\".");
            }
        } else {
            sender.sendMessage("error");
        }
        return true;
    }
}
