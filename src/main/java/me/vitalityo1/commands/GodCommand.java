package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class GodCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public GodCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.god")) return true;
        if (!plugin.getConfig().getBoolean("modules.utilities", true)) {
            MessageUtil.send(sender, "messages.module-disabled");
            return true;
        }

        if (args.length > 0 && sender.hasPermission("vitalityo1.admin")) {
            Player target = plugin.getServer().getPlayerExact(args[0]);
            if (target == null) {
                MessageUtil.send(sender, "messages.player-not-found", "{player}", args[0]);
                return true;
            }
            boolean newState = !plugin.getPlayerListener().isGod(target.getUniqueId());
            plugin.getPlayerListener().setGod(target.getUniqueId(), newState);
            if (!sender.equals(target)) {
                MessageUtil.send(sender,
                        newState ? "messages.god-enabled-other" : "messages.god-disabled-other",
                        "{player}", target.getName());
            }
            MessageUtil.send(target, newState ? "messages.god-enabled" : "messages.god-disabled");
        } else {
            if (MessageUtil.isPlayerOnly(sender)) return true;
            Player player = (Player) sender;
            boolean newState = !plugin.getPlayerListener().isGod(player.getUniqueId());
            plugin.getPlayerListener().setGod(player.getUniqueId(), newState);
            MessageUtil.send(player, newState ? "messages.god-enabled" : "messages.god-disabled");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("vitalityo1.admin")) {
            String input = args[0].toLowerCase();
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
