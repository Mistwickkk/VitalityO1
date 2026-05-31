package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class StaffChatCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public StaffChatCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.moderation.staffchat")) return true;
        if (args.length == 0) {
            MessageUtil.sendRaw(sender, "&cUsage: /sc <message>");
            return true;
        }

        String name = sender instanceof Player p ? p.getName() : "Console";
        String message = String.join(" ", args);
        String formatted = MessageUtil.getRaw("messages.staff-chat-format",
                "{player}", name, "{message}", message);

        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (online.hasPermission("vitalityo1.moderation.staffchat")) {
                online.sendMessage(formatted);
            }
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(formatted);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
