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

public class BalanceCommand implements TabExecutor {

    private final VitalityO1 plugin;

    public BalanceCommand(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!MessageUtil.checkPermission(sender, "vitalityo1.economy.balance")) return true;
        if (!plugin.getConfig().getBoolean("modules.economy", true)) {
            MessageUtil.send(sender, "messages.module-disabled");
            return true;
        }

        String sym = plugin.getEconomyManager().getCurrencySymbol();

        if (args.length == 0) {
            if (MessageUtil.isPlayerOnly(sender)) return true;
            Player player = (Player) sender;
            double bal = plugin.getEconomyManager().getBalance(player.getUniqueId());
            MessageUtil.send(sender, "messages.balance-self",
                    "{symbol}", sym,
                    "{balance}", String.format("%.2f", bal));
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            MessageUtil.send(sender, "messages.player-not-found", "{player}", args[0]);
            return true;
        }

        double bal = plugin.getEconomyManager().getBalance(target.getUniqueId());
        MessageUtil.send(sender, "messages.balance-other",
                "{player}", target.getName() != null ? target.getName() : args[0],
                "{symbol}", sym,
                "{balance}", String.format("%.2f", bal));
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
