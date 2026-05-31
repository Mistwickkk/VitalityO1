package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;
import java.util.stream.Collectors;

public class DelWarpCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public DelWarpCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.admin")) return true;
        if (args.length == 0) {
            MessageUtil.sendRaw(sender, "&cUsage: /delwarp <name>");
            return true;
        }
        String name = args[0].toLowerCase();
        if (!plugin.getWarpManager().deleteWarp(name)) {
            MessageUtil.send(sender, "messages.warp-not-found", "{warp}", name);
            return true;
        }
        MessageUtil.send(sender, "messages.warp-deleted", "{warp}", name);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return plugin.getWarpManager().getWarpNames().stream()
                    .filter(w -> w.startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
