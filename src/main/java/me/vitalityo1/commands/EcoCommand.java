package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class EcoCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public EcoCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.economy.admin")) return true;
        if (!plugin.getConfig().getBoolean("modules.economy", true)) {
            MessageUtil.send(sender, "messages.module-disabled");
            return true;
        }

        if (args.length < 3) {
            MessageUtil.sendRaw(sender, "&cUsage: /eco <give|take|set> <player> <amount>");
            return true;
        }

        String action = args[0].toLowerCase();
        @SuppressWarnings("deprecation")
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            MessageUtil.send(sender, "messages.player-not-found", "{player}", args[1]);
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            MessageUtil.send(sender, "messages.invalid-number");
            return true;
        }

        if (amount < 0) {
            MessageUtil.send(sender, "messages.pay-invalid-amount");
            return true;
        }

        String sym = plugin.getEconomyManager().getCurrencySymbol();
        String targetName = target.getName() != null ? target.getName() : args[1];

        switch (action) {
            case "give" -> {
                plugin.getEconomyManager().addBalance(target.getUniqueId(), amount);
                double newBal = plugin.getEconomyManager().getBalance(target.getUniqueId());
                MessageUtil.send(sender, "messages.eco-give",
                        "{symbol}", sym,
                        "{amount}", String.format("%.2f", amount),
                        "{player}", targetName,
                        "{balance}", String.format("%.2f", newBal));
            }
            case "take" -> {
                plugin.getEconomyManager().removeBalance(target.getUniqueId(), amount);
                double newBal = plugin.getEconomyManager().getBalance(target.getUniqueId());
                MessageUtil.send(sender, "messages.eco-take",
                        "{symbol}", sym,
                        "{amount}", String.format("%.2f", amount),
                        "{player}", targetName,
                        "{balance}", String.format("%.2f", newBal));
            }
            case "set" -> {
                plugin.getEconomyManager().setBalance(target.getUniqueId(), amount);
                MessageUtil.send(sender, "messages.eco-set",
                        "{symbol}", sym,
                        "{amount}", String.format("%.2f", amount),
                        "{player}", targetName);
            }
            default -> MessageUtil.sendRaw(sender, "&cUsage: /eco <give|take|set> <player> <amount>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return List.of("give", "take", "set");
        if (args.length == 2) {
            String input = args[1].toLowerCase();
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
