package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetWarpCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public SetWarpCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.admin")) return true;

        if (args.length == 0) {
            MessageUtil.sendRaw(sender, "&cUsage: /setwarp <name>");
            return true;
        }

        Player player = (Player) sender;
        String name = args[0].toLowerCase();
        plugin.getWarpManager().setWarp(name, player.getLocation());
        MessageUtil.send(player, "messages.warp-set", "{warp}", name);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
