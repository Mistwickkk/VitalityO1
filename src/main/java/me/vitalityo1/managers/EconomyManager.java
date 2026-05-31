package me.vitalityo1.managers;

import me.vitalityo1.VitalityO1;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {

    private final VitalityO1 plugin;
    private final Map<UUID, Double> balances = new HashMap<>();
    private static final String DATA_FILE = "economy";

    public EconomyManager(VitalityO1 plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        YamlConfiguration data = plugin.getDataManager().loadGlobalData(DATA_FILE);
        if (data.contains("balances")) {
            for (String key : data.getConfigurationSection("balances").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    double balance = data.getDouble("balances." + key, getStartingBalance());
                    balances.put(uuid, balance);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void saveAll() {
        YamlConfiguration data = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            data.set("balances." + entry.getKey().toString(), entry.getValue());
        }
        plugin.getDataManager().saveGlobalData(DATA_FILE, data);
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, getStartingBalance());
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, Math.max(0, amount));
    }

    public void addBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public boolean removeBalance(UUID uuid, double amount) {
        double current = getBalance(uuid);
        if (current < amount) return false;
        setBalance(uuid, current - amount);
        return true;
    }

    public boolean hasEnough(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public void ensureLoaded(UUID uuid) {
        balances.computeIfAbsent(uuid, k -> getStartingBalance());
    }

    private double getStartingBalance() {
        return plugin.getConfig().getDouble("economy.starting-balance", 1000.0);
    }

    public String getCurrencySymbol() {
        return plugin.getConfig().getString("economy.currency-symbol", "$");
    }
}
