package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class BackCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public BackCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.back")) return true;

        Player player = (Player) sender;
        Location back = plugin.getTeleportManager().getBack(player.getUniqueId());
        if (back == null || back.getWorld() == null) {
            MessageUtil.send(player, "messages.back-no-location");
            return true;
        }
        MessageUtil.send(player, "messages.back-teleport");
        plugin.getTeleportManager().scheduleTeleport(player, back);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
