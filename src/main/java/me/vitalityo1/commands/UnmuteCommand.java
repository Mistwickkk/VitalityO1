package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class UnmuteCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public UnmuteCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.moderation.mute")) return true;
        if (args.length == 0) {
            MessageUtil.sendRaw(sender, "&cUsage: /unmute <player>");
            return true;
        }

        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null) {
            MessageUtil.send(sender, "messages.player-not-found", "{player}", args[0]);
            return true;
        }

        if (!plugin.getModerationManager().isMuted(target.getUniqueId())) {
            MessageUtil.send(sender, "messages.not-muted", "{player}", target.getName());
            return true;
        }

        plugin.getModerationManager().unmute(target.getUniqueId());
        MessageUtil.send(target, "messages.unmute-notify");

        if (plugin.getConfig().getBoolean("moderation.broadcast-mutes", true)) {
            plugin.getServer().broadcastMessage(
                    MessageUtil.getRaw("messages.unmute-broadcast", "{player}", target.getName()));
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
