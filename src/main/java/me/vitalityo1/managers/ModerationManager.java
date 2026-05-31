package me.vitalityo1.managers;

import me.vitalityo1.VitalityO1;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModerationManager {

    private final VitalityO1 plugin;

    private final Map<UUID, Long> mutes = new HashMap<>();
    private final Map<UUID, String> muteReasons = new HashMap<>();
    private final Map<UUID, Integer> warns = new HashMap<>();
    private static final String DATA_FILE = "moderation";

    public ModerationManager(VitalityO1 plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        YamlConfiguration data = plugin.getDataManager().loadGlobalData(DATA_FILE);
        if (data.contains("mutes")) {
            for (String key : data.getConfigurationSection("mutes").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    long expiry = data.getLong("mutes." + key + ".expiry", 0L);
                    String reason = data.getString("mutes." + key + ".reason", "");
                    if (expiry == -1 || expiry > System.currentTimeMillis()) {
                        mutes.put(uuid, expiry);
                        muteReasons.put(uuid, reason);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        if (data.contains("warns")) {
            for (String key : data.getConfigurationSection("warns").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    warns.put(uuid, data.getInt("warns." + key, 0));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void saveAll() {
        YamlConfiguration data = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : mutes.entrySet()) {
            String key = entry.getKey().toString();
            data.set("mutes." + key + ".expiry", entry.getValue());
            data.set("mutes." + key + ".reason", muteReasons.getOrDefault(entry.getKey(), ""));
        }
        for (Map.Entry<UUID, Integer> entry : warns.entrySet()) {
            data.set("warns." + entry.getKey().toString(), entry.getValue());
        }
        plugin.getDataManager().saveGlobalData(DATA_FILE, data);
    }

    public boolean isMuted(UUID uuid) {
        Long expiry = mutes.get(uuid);
        if (expiry == null) return false;
        if (expiry == -1) return true;
        if (System.currentTimeMillis() > expiry) {
            mutes.remove(uuid);
            muteReasons.remove(uuid);
            return false;
        }
        return true;
    }

    public void mute(UUID uuid, long durationMillis, String reason) {
        long expiry = durationMillis == -1 ? -1 : System.currentTimeMillis() + durationMillis;
        mutes.put(uuid, expiry);
        muteReasons.put(uuid, reason);
        saveAll();
    }

    public void unmute(UUID uuid) {
        mutes.remove(uuid);
        muteReasons.remove(uuid);
        saveAll();
    }

    public String getMuteTimeRemaining(UUID uuid) {
        Long expiry = mutes.get(uuid);
        if (expiry == null) return "0s";
        if (expiry == -1) return "permanently";
        long remaining = expiry - System.currentTimeMillis();
        if (remaining <= 0) return "0s";
        return formatDuration(remaining);
    }

    public String getMuteReason(UUID uuid) {
        return muteReasons.getOrDefault(uuid, "No reason provided");
    }

    public int getWarnCount(UUID uuid) {
        return warns.getOrDefault(uuid, 0);
    }

    public int addWarn(UUID uuid) {
        int count = warns.getOrDefault(uuid, 0) + 1;
        warns.put(uuid, count);
        saveAll();
        return count;
    }

    public void clearWarns(UUID uuid) {
        warns.remove(uuid);
        saveAll();
    }

    public int getMaxWarnings() {
        return plugin.getConfig().getInt("moderation.max-warnings", 3);
    }

    public String getAutoAction() {
        return plugin.getConfig().getString("moderation.auto-action", "BAN").toUpperCase();
    }

    public static long parseDuration(String input) {
        if (input == null || input.isBlank()) return -1;
        long total = 0;
        int num = 0;
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                num = num * 10 + Character.getNumericValue(c);
            } else {
                total += switch (c) {
                    case 's' -> num * 1000L;
                    case 'm' -> num * 60_000L;
                    case 'h' -> num * 3_600_000L;
                    case 'd' -> num * 86_400_000L;
                    default -> 0L;
                };
                num = 0;
            }
        }
        return total == 0 ? -1 : total;
    }

    public static String formatDuration(long millis) {
        long seconds = millis / 1000;
        if (seconds < 60) return seconds + "s";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + "m " + (seconds % 60) + "s";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h " + (minutes % 60) + "m";
        long days = hours / 24;
        return days + "d " + (hours % 24) + "h";
    }
}
