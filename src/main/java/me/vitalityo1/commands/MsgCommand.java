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

public class MsgCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public MsgCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.msg")) return true;
        if (args.length < 2) {
            MessageUtil.sendRaw(sender, "&cUsage: /msg <player> <message>");
            return true;
        }

        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null) {
            MessageUtil.send(sender, "messages.player-not-found", "{player}", args[0]);
            return true;
        }

        String senderName = sender instanceof Player p ? p.getName() : "Console";
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        String formatted = MessageUtil.getRaw("messages.msg-format",
                "{sender}", senderName,
                "{receiver}", target.getName(),
                "{message}", message);

        sender.sendMessage(formatted);
        target.sendMessage(formatted);

        if (sender instanceof Player player) {
            plugin.getPlayerListener().setLastMessaged(player.getUniqueId(), target.getUniqueId());
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
