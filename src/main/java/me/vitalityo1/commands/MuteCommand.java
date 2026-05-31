package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.managers.ModerationManager;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MuteCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public MuteCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.moderation.mute")) return true;
        if (args.length < 2) {
            MessageUtil.sendRaw(sender, "&cUsage: /mute <player> <time> [reason]  (time: 10s, 5m, 2h, 1d)");
            return true;
        }

        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null) {
            MessageUtil.send(sender, "messages.player-not-found", "{player}", args[0]);
            return true;
        }

        if (plugin.getModerationManager().isMuted(target.getUniqueId())) {
            MessageUtil.send(sender, "messages.already-muted", "{player}", target.getName());
            return true;
        }

        long duration = ModerationManager.parseDuration(args[1]);
        String friendly = duration == -1 ? "permanently" : ModerationManager.formatDuration(duration);
        String reason = args.length > 2
                ? String.join(" ", Arrays.copyOfRange(args, 2, args.length))
                : "No reason provided";

        plugin.getModerationManager().mute(target.getUniqueId(), duration, reason);

        MessageUtil.send(target, "messages.mute-notify",
                "{time}", friendly, "{reason}", reason);

        if (plugin.getConfig().getBoolean("moderation.broadcast-mutes", true)) {
            plugin.getServer().broadcastMessage(
                    MessageUtil.getRaw("messages.mute-broadcast",
                            "{player}", target.getName(), "{time}", friendly));
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
        if (args.length == 2) return List.of("10s", "30m", "1h", "1d");
        return List.of();
    }
}
