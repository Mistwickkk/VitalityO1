package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WarpCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public WarpCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.warp")) return true;
        if (!plugin.getConfig().getBoolean("modules.warps", true)) {
            MessageUtil.send(sender, "messages.module-disabled");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            Set<String> names = plugin.getWarpManager().getWarpNames();
            if (names.isEmpty()) {
                MessageUtil.send(player, "messages.warp-none");
            } else {
                MessageUtil.send(player, "messages.warp-list",
                        "{warps}", String.join(", ", names));
            }
            return true;
        }

        String name = args[0].toLowerCase();
        Location warp = plugin.getWarpManager().getWarp(name);
        if (warp == null) {
            MessageUtil.send(player, "messages.warp-not-found", "{warp}", name);
            return true;
        }

        MessageUtil.send(player, "messages.warp-teleport", "{warp}", name);
        plugin.getTeleportManager().scheduleTeleport(player, warp);
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
