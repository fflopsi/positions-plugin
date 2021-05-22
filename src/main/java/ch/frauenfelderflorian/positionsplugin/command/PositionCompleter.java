package ch.frauenfelderflorian.positionsplugin.command;

import ch.frauenfelderflorian.positionsplugin.PositionsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PositionCompleter implements TabCompleter {
    private final PositionsPlugin plugin;
    private final ArrayList<String> COMMANDS;
    private final ArrayList<String> POSITIONS;
    private final ArrayList<String> COMPLETION;

    public PositionCompleter(PositionsPlugin plugin) {
        this.plugin = plugin;
        COMMANDS = new ArrayList<>();
        COMMANDS.add("-del");
        COMMANDS.add("-tp");
        COMMANDS.add("-list");
        COMMANDS.add("-save");
        COMMANDS.add("-load");
        COMMANDS.add("-info");
        POSITIONS = new ArrayList<>();
        COMPLETION = new ArrayList<>();
        reload();
    }

    public void reload() {
        POSITIONS.clear();
        COMPLETION.clear();
        POSITIONS.addAll(plugin.positions.usablePositions.keySet());
        COMPLETION.addAll(COMMANDS);
        COMPLETION.addAll(POSITIONS);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final ArrayList<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], COMPLETION, completions);
        } else if (args.length == 2) {
            for (String str : COMMANDS) {
                if (args[0].equals(str)) {
                    StringUtil.copyPartialMatches(args[1], POSITIONS, completions);
                }
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
