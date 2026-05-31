package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DelHomeCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public DelHomeCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.home")) return true;

        Player player = (Player) sender;
        String name = args.length > 0 ? args[0].toLowerCase() : "home";

        if (!plugin.getHomeManager().deleteHome(player.getUniqueId(), name)) {
            MessageUtil.send(player, "messages.home-not-found", "{home}", name);
            return true;
        }
        MessageUtil.send(player, "messages.home-deleted", "{home}", name);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return List.of();
        if (args.length == 1) {
            Set<String> homes = plugin.getHomeManager().getHomeNames(player.getUniqueId());
            List<String> result = new ArrayList<>();
            for (String h : homes) {
                if (h.startsWith(args[0].toLowerCase())) result.add(h);
            }
            return result;
        }
        return List.of();
    }
}
