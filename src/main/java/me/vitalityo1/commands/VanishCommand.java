package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class VanishCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public VanishCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.vanish")) return true;
        if (!plugin.getConfig().getBoolean("modules.utilities", true)) {
            MessageUtil.send(sender, "messages.module-disabled");
            return true;
        }

        Player player = (Player) sender;
        if (plugin.getPlayerListener().isVanished(player.getUniqueId())) {
            plugin.getPlayerListener().unvanishPlayer(player);
            MessageUtil.send(player, "messages.vanish-disabled");
        } else {
            plugin.getPlayerListener().vanishPlayer(player);
            MessageUtil.send(player, "messages.vanish-enabled");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
