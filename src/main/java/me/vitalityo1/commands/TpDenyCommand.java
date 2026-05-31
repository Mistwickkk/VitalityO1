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

public class TpDenyCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public TpDenyCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.tpa")) return true;

        Player player = (Player) sender;
        UUID requester = plugin.getTeleportManager().getTpaRequester(player.getUniqueId());
        if (requester == null) {
            MessageUtil.send(player, "messages.tpa-no-request");
            return true;
        }
        plugin.getTeleportManager().denyTpa(player);
        MessageUtil.send(player, "messages.tpa-denied");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
