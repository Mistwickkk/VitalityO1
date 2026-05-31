package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ReplyCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public ReplyCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.msg")) return true;
        if (args.length == 0) {
            MessageUtil.sendRaw(sender, "&cUsage: /reply <message>");
            return true;
        }

        Player player = (Player) sender;
        UUID targetUUID = plugin.getPlayerListener().getLastMessaged(player.getUniqueId());
        if (targetUUID == null) {
            MessageUtil.send(player, "messages.reply-no-target");
            return true;
        }

        Player target = plugin.getServer().getPlayer(targetUUID);
        if (target == null || !target.isOnline()) {
            MessageUtil.send(player, "messages.player-not-found", "{player}", "that player");
            return true;
        }

        String message = String.join(" ", args);
        String formatted = MessageUtil.getRaw("messages.msg-format",
                "{sender}", player.getName(),
                "{receiver}", target.getName(),
                "{message}", message);

        player.sendMessage(formatted);
        target.sendMessage(formatted);
        plugin.getPlayerListener().setLastMessaged(player.getUniqueId(), target.getUniqueId());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
