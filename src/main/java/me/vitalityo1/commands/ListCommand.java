package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class ListCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public ListCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.list")) return true;

        int count = plugin.getServer().getOnlinePlayers().size();
        int max = plugin.getServer().getMaxPlayers();

        StringJoiner names = new StringJoiner("&7, &e");
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            names.add(p.getName());
        }

        String prefix = MessageUtil.getPrefix();
        sender.sendMessage(MessageUtil.color(
                prefix + "&aPlayers online: &e" + count + "&a/&e" + max));
        if (count > 0) {
            sender.sendMessage(MessageUtil.color("&e" + names));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
