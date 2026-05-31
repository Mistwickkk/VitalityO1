package me.vitalityo1.utils;

import me.vitalityo1.VitalityO1;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    private MessageUtil() {}

    public static String color(String msg) {
        if (msg == null) return "";
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String format(String template, String... replacements) {
        if (template == null) return "";
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            template = template.replace(replacements[i], replacements[i + 1]);
        }
        return color(template);
    }

    public static String getRaw(String path, String... replacements) {
        VitalityO1 plugin = VitalityO1.getInstance();
        String prefix = plugin.getConfig().getString("messages.prefix", "&8[&6VitalityO1&8] &r");
        String msg = plugin.getConfig().getString(path, "&cMissing message: " + path);
        msg = prefix + msg;
        return format(msg, replacements);
    }

    public static void send(CommandSender sender, String configPath, String... replacements) {
        sender.sendMessage(getRaw(configPath, replacements));
    }

    public static void sendRaw(CommandSender sender, String message, String... replacements) {
        sender.sendMessage(format(message, replacements));
    }

    public static String getPrefix() {
        String prefix = VitalityO1.getInstance().getConfig().getString("messages.prefix", "&8[&6VitalityO1&8] &r");
        return color(prefix);
    }

    public static String applyPlayerPlaceholders(String text, Player player) {
        if (text == null) return "";
        text = text.replace("{player}", player.getName());
        text = text.replace("{world}", player.getWorld().getName());
        double bal = VitalityO1.getInstance().getEconomyManager().getBalance(player.getUniqueId());
        String sym = VitalityO1.getInstance().getEconomyManager().getCurrencySymbol();
        text = text.replace("{balance}", sym + String.format("%.2f", bal));
        text = text.replace("{prefix}", "");
        return text;
    }

    public static boolean isPlayerOnly(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getRaw("messages.player-only"));
            return true;
        }
        return false;
    }

    public static boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            send(sender, "messages.no-permission");
            return false;
        }
        return true;
    }
}
