package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.managers.ModerationManager;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SeenCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public SeenCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.seen")) return true;
        if (args.length == 0) {
            MessageUtil.sendRaw(sender, "&cUsage: /seen <player>");
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
        String name = target.getName() != null ? target.getName() : args[0];

        if (target.isOnline()) {
            MessageUtil.send(sender, "messages.seen-online", "{player}", name);
            return true;
        }

        if (!target.hasPlayedBefore()) {
            MessageUtil.send(sender, "messages.seen-never", "{player}", name);
            return true;
        }

        long lastSeen = plugin.getPlayerListener().getLastSeen(target.getUniqueId());
        if (lastSeen == 0) {
            lastSeen = target.getLastPlayed();
        }
        if (lastSeen == 0) {
            MessageUtil.send(sender, "messages.seen-never", "{player}", name);
            return true;
        }

        long ago = System.currentTimeMillis() - lastSeen;
        String timeStr = ModerationManager.formatDuration(ago);
        MessageUtil.send(sender, "messages.seen-offline", "{player}", name, "{time}", timeStr);
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
