package me.vitalityo1.managers;

import me.vitalityo1.VitalityO1;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HomeManager {

    private final VitalityO1 plugin;
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();

    public HomeManager(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    private void loadPlayer(UUID uuid) {
        if (homes.containsKey(uuid)) return;
        YamlConfiguration data = plugin.getDataManager().loadPlayerData(uuid);
        Map<String, Location> playerHomes = new HashMap<>();
        if (data.contains("homes")) {
            for (String name : data.getConfigurationSection("homes").getKeys(false)) {
                String path = "homes." + name + ".";
                String worldName = data.getString(path + "world");
                if (worldName == null) continue;
                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;
                double x = data.getDouble(path + "x");
                double y = data.getDouble(path + "y");
                double z = data.getDouble(path + "z");
                float yaw = (float) data.getDouble(path + "yaw");
                float pitch = (float) data.getDouble(path + "pitch");
                playerHomes.put(name.toLowerCase(), new Location(world, x, y, z, yaw, pitch));
            }
        }
        homes.put(uuid, playerHomes);
    }

    private void savePlayer(UUID uuid) {
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null) return;
        YamlConfiguration data = plugin.getDataManager().loadPlayerData(uuid);
        data.set("homes", null);
        for (Map.Entry<String, Location> entry : playerHomes.entrySet()) {
            String path = "homes." + entry.getKey() + ".";
            Location loc = entry.getValue();
            data.set(path + "world", loc.getWorld() != null ? loc.getWorld().getName() : "world");
            data.set(path + "x", loc.getX());
            data.set(path + "y", loc.getY());
            data.set(path + "z", loc.getZ());
            data.set(path + "yaw", loc.getYaw());
            data.set(path + "pitch", loc.getPitch());
        }
        plugin.getDataManager().savePlayerData(uuid, data);
    }

    public void saveAll() {
        for (UUID uuid : homes.keySet()) {
            savePlayer(uuid);
        }
    }

    public Location getHome(UUID uuid, String name) {
        loadPlayer(uuid);
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null) return null;
        return playerHomes.get(name.toLowerCase());
    }

    public void setHome(UUID uuid, String name, Location location) {
        loadPlayer(uuid);
        homes.computeIfAbsent(uuid, k -> new HashMap<>()).put(name.toLowerCase(), location);
        savePlayer(uuid);
    }

    public boolean deleteHome(UUID uuid, String name) {
        loadPlayer(uuid);
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null) return false;
        boolean removed = playerHomes.remove(name.toLowerCase()) != null;
        if (removed) savePlayer(uuid);
        return removed;
    }

    public Set<String> getHomeNames(UUID uuid) {
        loadPlayer(uuid);
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null) return Collections.emptySet();
        return Collections.unmodifiableSet(playerHomes.keySet());
    }

    public int getHomeCount(UUID uuid) {
        loadPlayer(uuid);
        Map<String, Location> playerHomes = homes.get(uuid);
        return playerHomes == null ? 0 : playerHomes.size();
    }

    public int getHomeLimit() {
        return plugin.getConfig().getInt("homes.default-limit", 3);
    }
}
