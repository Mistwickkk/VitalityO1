package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PayCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public PayCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (MessageUtil.isPlayerOnly(sender)) return true;
        if (!MessageUtil.checkPermission(sender, "vitalityo1.economy.pay")) return true;
        if (!plugin.getConfig().getBoolean("modules.economy", true)) {
            MessageUtil.send(sender, "messages.module-disabled");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            MessageUtil.sendRaw(player, "&cUsage: /pay <player> <amount>");
            return true;
        }

        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null) {
            MessageUtil.send(player, "messages.player-not-found", "{player}", args[0]);
            return true;
        }

        if (target.equals(player)) {
            MessageUtil.send(player, "messages.pay-self");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            MessageUtil.send(player, "messages.invalid-number");
            return true;
        }

        if (amount <= 0) {
            MessageUtil.send(player, "messages.pay-invalid-amount");
            return true;
        }

        String sym = plugin.getEconomyManager().getCurrencySymbol();

        if (!plugin.getEconomyManager().hasEnough(player.getUniqueId(), amount)) {
            MessageUtil.send(player, "messages.pay-insufficient",
                    "{symbol}", sym,
                    "{amount}", String.format("%.2f", amount));
            return true;
        }

        plugin.getEconomyManager().removeBalance(player.getUniqueId(), amount);
        plugin.getEconomyManager().addBalance(target.getUniqueId(), amount);

        MessageUtil.send(player, "messages.pay-sent",
                "{player}", target.getName(),
                "{symbol}", sym,
                "{amount}", String.format("%.2f", amount));
        MessageUtil.send(target, "messages.pay-received",
                "{player}", player.getName(),
                "{symbol}", sym,
                "{amount}", String.format("%.2f", amount));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
