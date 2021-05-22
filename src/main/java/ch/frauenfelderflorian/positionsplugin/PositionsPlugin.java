package ch.frauenfelderflorian.positionsplugin;

import ch.frauenfelderflorian.positionsplugin.command.PositionCompleter;
import ch.frauenfelderflorian.positionsplugin.data.Positions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PositionsPlugin extends JavaPlugin {
    private static final String[] INFO = {
            "This is a plugin written by Florian Frauenfelder.",
            "To add a location, type \"/position\" followed by the name your location should have.",
            "To see the coordinates of a saved location, type \"/position\" followed by the name of the saved location.",
            "To delete a saved location, type \"/position -del\" followed by the name of the position to be deleted.",
            "To see a list of all saved locations, type \"/position -list\".",
            "To teleport yourself to a saved location, type \"/position -tp\" followed by the name of the saved location.",
            "To display this information, type \"/position -info\"."
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
            case "-del" -> {
                sender.sendMessage("deleted " + this.positions.positionToString(args[1]));
                this.positions.usablePositions.remove(args[1]);
                this.positions.save();
                this.tabCompleter.reload();
            }
            case "-tp" -> {
                Objects.requireNonNull(Bukkit.getPlayer(sender.getName()))
                        .teleport(this.positions.usablePositions.get(args[1]));
                sender.sendMessage("teleported " + sender.getName()
                        + " to position " + this.positions.positionToString(args[1]));
            }
            case "-list" -> {
                sender.sendMessage("all saved positions:");
                this.positions.usablePositions.keySet().forEach(
                        pos -> sender.sendMessage(this.positions.positionToString(pos))
                );
            }
            case "-save" -> {
                sender.sendMessage("This command is executed when something is changed, " +
                        "so there is usually no need to call it manually.");
                this.positions.save();
            }
            case "-load" -> {
                sender.sendMessage("This command is executed when the plugin is being enabled, " +
                        "so there is usually no need to call it manually.");
                this.positions.load();
            }
            case "-info" -> sender.sendMessage(INFO);
            default -> {
                if (!this.positions.usablePositions.containsKey(args[0])) {
                    this.positions.add(args[0], location);
                    this.tabCompleter.reload();
                }
                sender.sendMessage(this.positions.positionToString(args[0]));
            }
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
