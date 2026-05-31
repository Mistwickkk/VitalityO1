package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarnCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public WarnCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.moderation.warn")) return true;
        if (args.length == 0) {
            MessageUtil.sendRaw(sender, "&cUsage: /warn <player> [reason]");
            return true;
        }

        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null) {
            MessageUtil.send(sender, "messages.player-not-found", "{player}", args[0]);
            return true;
        }

        String reason = args.length > 1
                ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                : "No reason provided";

        int count = plugin.getModerationManager().addWarn(target.getUniqueId());
        int max = plugin.getModerationManager().getMaxWarnings();

        MessageUtil.send(target, "messages.warn-notify",
                "{count}", String.valueOf(count),
                "{max}", String.valueOf(max),
                "{reason}", reason);

        String staffName = sender instanceof Player p ? p.getName() : "Console";
        if (plugin.getConfig().getBoolean("moderation.broadcast-warns", true)) {
            plugin.getServer().broadcastMessage(
                    MessageUtil.getRaw("messages.warn-broadcast",
                            "{player}", target.getName(),
                            "{staff}", staffName,
                            "{reason}", reason));
        }

        if (count >= max) {
            String action = plugin.getModerationManager().getAutoAction();
            if ("BAN".equals(action)) {
                plugin.getServer().getBanList(org.bukkit.BanList.Type.NAME)
                        .addBan(target.getName(), "Reached maximum warnings (" + max + ")", null, "VitalityO1");
                target.kickPlayer(MessageUtil.color("&cYou have been banned for reaching the warning limit."));
                plugin.getModerationManager().clearWarns(target.getUniqueId());
            } else {
                target.kickPlayer(MessageUtil.color("&cYou have been kicked for reaching the warning limit."));
            }
        }
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
