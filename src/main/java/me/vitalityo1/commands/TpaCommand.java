package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class TpaCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public TpaCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.tpa")) return true;
        if (!plugin.getConfig().getBoolean("modules.teleportation", true)) {
            MessageUtil.send(sender, "messages.module-disabled");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            MessageUtil.sendRaw(player, "&cUsage: /tpa <player>");
            return true;
        }

        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null) {
            MessageUtil.send(player, "messages.player-not-found", "{player}", args[0]);
            return true;
        }

        if (target.equals(player)) {
            MessageUtil.send(player, "messages.tpa-self");
            return true;
        }

        plugin.getTeleportManager().requestTpa(player, target);
        MessageUtil.send(player, "messages.tpa-sent", "{player}", target.getName());
        MessageUtil.send(target, "messages.tpa-received", "{player}", player.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
