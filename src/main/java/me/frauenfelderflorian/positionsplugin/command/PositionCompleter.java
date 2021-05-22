package me.frauenfelderflorian.positionsplugin.command;

import me.frauenfelderflorian.positionsplugin.PositionsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PositionCompleter implements TabCompleter {
    private final PositionsPlugin plugin;
    private final ArrayList<String> NAME_COMMANDS;
    private final ArrayList<String> COMMANDS;
    private final ArrayList<String> POSITIONS;

    public PositionCompleter(PositionsPlugin plugin) {
        this.plugin = plugin;
        NAME_COMMANDS = new ArrayList<>();
        NAME_COMMANDS.add("add");
        NAME_COMMANDS.add("show");
        NAME_COMMANDS.add("tp");
        NAME_COMMANDS.add("del");
        COMMANDS = new ArrayList<>();
        COMMANDS.addAll(NAME_COMMANDS);
        COMMANDS.add("list");
        COMMANDS.add("clear");
        COMMANDS.add("info");
        POSITIONS = new ArrayList<>();
        reload();
    }

    public void reload() {
        POSITIONS.clear();
        POSITIONS.addAll(plugin.positions.usablePositions.keySet());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final ArrayList<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
        } else if (args.length == 2) {
            for (String str : NAME_COMMANDS) {
                if (args[0].equals(str)) {
                    StringUtil.copyPartialMatches(args[1], POSITIONS, completions);
                }
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
