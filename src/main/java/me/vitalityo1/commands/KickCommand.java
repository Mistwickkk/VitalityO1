package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class KickCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public KickCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.moderation.kick")) return true;
        if (args.length == 0) {
            MessageUtil.sendRaw(sender, "&cUsage: /kick <player> [reason]");
            return true;
        }

        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null) {
            MessageUtil.send(sender, "messages.player-not-found", "{player}", args[0]);
            return true;
        }

        if (sender instanceof Player p && p.equals(target)) {
            MessageUtil.send(sender, "messages.kick-self");
            return true;
        }

        String reason = args.length > 1
                ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length))
                : "Kicked by an admin";

        String screen = MessageUtil.getRaw("messages.kick-screen", "{reason}", reason);
        target.kickPlayer(screen);

        if (plugin.getConfig().getBoolean("moderation.broadcast-kicks", true)) {
            String broadcast = MessageUtil.getRaw("messages.kick-broadcast",
                    "{player}", target.getName(), "{reason}", reason);
            plugin.getServer().broadcastMessage(broadcast);
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
