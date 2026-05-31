package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.BanList;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BanCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public BanCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.moderation.ban")) return true;
        if (args.length == 0) {
            MessageUtil.sendRaw(sender, "&cUsage: /ban <player> [reason]");
            return true;
        }

        if (sender instanceof Player p && p.getName().equalsIgnoreCase(args[0])) {
            MessageUtil.send(sender, "messages.ban-self");
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
        String reason = args.length > 1
                ? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
                : "Banned by an admin";

        plugin.getServer().getBanList(BanList.Type.NAME)
                .addBan(target.getName() != null ? target.getName() : args[0], reason, null, sender.getName());

        String screen = MessageUtil.getRaw("messages.ban-screen", "{reason}", reason);
        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().kickPlayer(screen);
        }

        if (plugin.getConfig().getBoolean("moderation.broadcast-bans", true)) {
            String name = target.getName() != null ? target.getName() : args[0];
            plugin.getServer().broadcastMessage(
                    MessageUtil.getRaw("messages.ban-broadcast", "{player}", name, "{reason}", reason));
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
