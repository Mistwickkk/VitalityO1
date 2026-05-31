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

public class WarpManager {

    private final VitalityO1 plugin;
    private final Map<String, Location> warps = new HashMap<>();
    private static final String DATA_FILE = "warps";

    public WarpManager(VitalityO1 plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        YamlConfiguration data = plugin.getDataManager().loadGlobalData(DATA_FILE);
        if (data.contains("warps")) {
            for (String name : data.getConfigurationSection("warps").getKeys(false)) {
                String path = "warps." + name + ".";
                String worldName = data.getString(path + "world");
                if (worldName == null) continue;
                World world = Bukkit.getWorld(worldName);
                if (world == null) continue;
                double x = data.getDouble(path + "x");
                double y = data.getDouble(path + "y");
                double z = data.getDouble(path + "z");
                float yaw = (float) data.getDouble(path + "yaw");
                float pitch = (float) data.getDouble(path + "pitch");
                warps.put(name.toLowerCase(), new Location(world, x, y, z, yaw, pitch));
            }
        }
    }

    public void saveAll() {
        YamlConfiguration data = new YamlConfiguration();
        for (Map.Entry<String, Location> entry : warps.entrySet()) {
            String path = "warps." + entry.getKey() + ".";
            Location loc = entry.getValue();
            data.set(path + "world", loc.getWorld() != null ? loc.getWorld().getName() : "world");
            data.set(path + "x", loc.getX());
            data.set(path + "y", loc.getY());
            data.set(path + "z", loc.getZ());
            data.set(path + "yaw", loc.getYaw());
            data.set(path + "pitch", loc.getPitch());
        }
        plugin.getDataManager().saveGlobalData(DATA_FILE, data);
    }

    public Location getWarp(String name) {
        return warps.get(name.toLowerCase());
    }

    public void setWarp(String name, Location location) {
        warps.put(name.toLowerCase(), location);
        saveAll();
    }

    public boolean deleteWarp(String name) {
        boolean removed = warps.remove(name.toLowerCase()) != null;
        if (removed) saveAll();
        return removed;
    }

    public Set<String> getWarpNames() {
        return Collections.unmodifiableSet(warps.keySet());
    }

    public boolean exists(String name) {
        return warps.containsKey(name.toLowerCase());
    }
}
