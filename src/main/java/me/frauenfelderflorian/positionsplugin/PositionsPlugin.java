package me.frauenfelderflorian.positionsplugin;

import me.frauenfelderflorian.positionsplugin.command.PositionCompleter;
import me.frauenfelderflorian.positionsplugin.data.Positions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PositionsPlugin extends JavaPlugin {
    private static final String[] INFO = {
            "This is a plugin written by Florian Frauenfelder for Minecraft Spigot.",
            "A command consists of the following parts, separated by a whitespace: " +
                    "\"/position\" (or one of the aliases \"/pos\", \"/location\", \"/loc\"), " +
                    "an option (and optionally the name of the position).",
            "The options are these:",
            "add: add new / overwrite position (at location of player) (name needed)",
            "show: show saved position (name needed)",
            "tp: teleport player to position (name needed)",
            "del: delete saved position (name needed)",
            "list: show all saved positions",
            "clear: delete all saved positions",
            "info: show this info message",
    };
    public Positions positions;
    private PositionCompleter tabCompleter;

    @Override
    public void onEnable() {
        this.positions = new Positions("positions.yml");
        this.positions.load();
        this.tabCompleter = new PositionCompleter(this);
        Objects.requireNonNull(getServer().getPluginCommand("position")).setTabCompleter(tabCompleter);
    }

    @Override
    public void onDisable() {
        this.positions.save();
    }

    private void execute(CommandSender sender, String[] args, Location location) {
        switch (args[0]) {
            case "add" -> {
                this.positions.add(args[1], location);
                this.tabCompleter.reload();
            }
            case "show" -> sender.sendMessage(this.positions.positionToString(args[1]));
            case "tp" -> {
                Objects.requireNonNull(Bukkit.getPlayer(sender.getName()))
                        .teleport(this.positions.usablePositions.get(args[1]));
                sender.sendMessage("teleported " + sender.getName()
                        + " to position " + this.positions.positionToString(args[1]));
            }
            case "del" -> {
                sender.sendMessage("deleted " + this.positions.positionToString(args[1]));
                this.positions.delete(args[1]);
                this.tabCompleter.reload();
            }
            case "list" -> {
                sender.sendMessage("all saved positions:");
                this.positions.usablePositions.keySet().forEach(
                        pos -> sender.sendMessage(this.positions.positionToString(pos))
                );
            }
            case "clear" -> {
                sender.sendMessage("deleted all positions");
                this.positions.clearAll();
                this.tabCompleter.reload();
            }
            case "info" -> sender.sendMessage(INFO);
            case "save" -> {
                sender.sendMessage("This command is executed when something is changed, " +
                        "so there is usually no need to call it manually.");
                this.positions.save();
            }
            case "load" -> {
                sender.sendMessage("This command is executed when the plugin is being enabled, " +
                        "so there is usually no need to call it manually.");
                this.positions.load();
            }
            default -> sender.sendMessage("not a valid command");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ("position".equals(command.getName()) && sender instanceof Player) {
            execute(sender, args, Objects.requireNonNull(Bukkit.getPlayer(sender.getName())).getLocation());
        } else {
            sender.sendMessage("you are not allowed to do this");
        }
        return true;
    }
}
