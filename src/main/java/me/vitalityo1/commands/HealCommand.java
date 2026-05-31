package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class HealCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public HealCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.heal")) return true;
        if (!plugin.getConfig().getBoolean("modules.utilities", true)) {
            MessageUtil.send(sender, "messages.module-disabled");
            return true;
        }

        if (args.length > 0 && sender.hasPermission("vitalityo1.admin")) {
            Player target = plugin.getServer().getPlayerExact(args[0]);
            if (target == null) {
                MessageUtil.send(sender, "messages.player-not-found", "{player}", args[0]);
                return true;
            }
            heal(target);
            if (sender != target) {
                MessageUtil.send(sender, "messages.heal-other", "{player}", target.getName());
            }
            MessageUtil.send(target, "messages.heal-self");
        } else {
            if (MessageUtil.isPlayerOnly(sender)) return true;
            Player player = (Player) sender;
            heal(player);
            MessageUtil.send(player, "messages.heal-self");
        }
        return true;
    }

    private void heal(Player player) {
        player.setHealth(player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH) != null
                ? player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue() : 20.0);
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setFireTicks(0);
        player.clearActivePotionEffects();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("vitalityo1.admin")) {
            String input = args[0].toLowerCase();
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
