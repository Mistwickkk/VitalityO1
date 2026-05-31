package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetHomeCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public SetHomeCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.home")) return true;
        if (!plugin.getConfig().getBoolean("modules.homes", true)) {
            MessageUtil.send(sender, "messages.module-disabled");
            return true;
        }

        Player player = (Player) sender;
        String name = args.length > 0 ? args[0].toLowerCase() : "home";

        int limit = plugin.getHomeManager().getHomeLimit();
        int count = plugin.getHomeManager().getHomeCount(player.getUniqueId());
        boolean exists = plugin.getHomeManager().getHome(player.getUniqueId(), name) != null;

        if (!exists && count >= limit && !player.hasPermission("vitalityo1.admin")) {
            MessageUtil.send(player, "messages.home-limit-reached", "{limit}", String.valueOf(limit));
            return true;
        }

        plugin.getHomeManager().setHome(player.getUniqueId(), name, player.getLocation());
        MessageUtil.send(player, "messages.home-set", "{home}", name);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
