package me.vitalityo1.listeners;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private final VitalityO1 plugin;
    private final Map<UUID, Long> lastMessage = new HashMap<>();

    public ChatListener(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!plugin.getConfig().getBoolean("modules.chat", true)) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (plugin.getModerationManager().isMuted(uuid)) {
            String timeLeft = plugin.getModerationManager().getMuteTimeRemaining(uuid);
            MessageUtil.send(player, "messages.mute-chat-blocked", "{time}", timeLeft);
            event.setCancelled(true);
            return;
        }

        long cooldown = plugin.getConfig().getLong("chat.anti-spam-cooldown", 1500);
        if (cooldown > 0 && !player.hasPermission("vitalityo1.admin")) {
            Long last = lastMessage.get(uuid);
            if (last != null && (System.currentTimeMillis() - last) < cooldown) {
                MessageUtil.sendRaw(player, "&cPlease wait before sending another message.");
                event.setCancelled(true);
                return;
            }
        }
        lastMessage.put(uuid, System.currentTimeMillis());

        String message = event.getMessage();

        if (plugin.getConfig().getBoolean("chat.swear-filter-enabled", true)
                && !player.hasPermission("vitalityo1.admin")) {
            List<String> swearWords = plugin.getConfig().getStringList("chat.swear-words");
            String replacement = plugin.getConfig().getString("chat.swear-replacement", "***");
            for (String word : swearWords) {
                if (word == null || word.isBlank()) continue;
                message = message.replaceAll("(?i)" + java.util.regex.Pattern.quote(word), replacement);
            }
        }

        String format = plugin.getConfig().getString("chat.format",
                "&7[{world}] {prefix}&e{player}&7: &f{message}");
        format = format
                .replace("{player}", player.getName())
                .replace("{world}", player.getWorld().getName())
                .replace("{prefix}", "")
                .replace("{message}", message);

        double bal = plugin.getEconomyManager().getBalance(uuid);
        String sym = plugin.getEconomyManager().getCurrencySymbol();
        format = format.replace("{balance}", sym + String.format("%.2f", bal));

        event.setFormat(MessageUtil.color(format).replace("%", "%%"));
    }
}
