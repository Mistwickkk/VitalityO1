package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SpawnCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public SpawnCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.spawn")) return true;

        Player player = (Player) sender;

        String worldName = plugin.getConfig().getString("spawn.world", "");
        if (worldName == null || worldName.isBlank()) {
            MessageUtil.send(player, "messages.spawn-not-set");
            return true;
        }

        World world = plugin.getServer().getWorld(worldName);
        if (world == null) {
            MessageUtil.send(player, "messages.spawn-not-set");
            return true;
        }

        double x = plugin.getConfig().getDouble("spawn.x", 0);
        double y = plugin.getConfig().getDouble("spawn.y", 64);
        double z = plugin.getConfig().getDouble("spawn.z", 0);
        float yaw = (float) plugin.getConfig().getDouble("spawn.yaw", 0);
        float pitch = (float) plugin.getConfig().getDouble("spawn.pitch", 0);

        Location spawn = new Location(world, x, y, z, yaw, pitch);

        MessageUtil.send(player, "messages.spawn-teleport");
        plugin.getTeleportManager().scheduleTeleport(player, spawn);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
