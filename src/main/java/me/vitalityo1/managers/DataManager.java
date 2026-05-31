package me.vitalityo1.managers;

import me.vitalityo1.VitalityO1;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager {

    private final VitalityO1 plugin;
    private final File dataFolder;

    public DataManager(VitalityO1 plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        ensureFolder("playerdata");
    }

    private void ensureFolder(String name) {
        File folder = new File(dataFolder, name);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public YamlConfiguration loadPlayerData(UUID uuid) {
        File file = getPlayerFile(uuid);
        return YamlConfiguration.loadConfiguration(file);
    }

    public void savePlayerData(UUID uuid, YamlConfiguration data) {
        File file = getPlayerFile(uuid);
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data for " + uuid, e);
        }
    }

    public YamlConfiguration loadGlobalData(String filename) {
        File file = new File(dataFolder, filename + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveGlobalData(String filename, YamlConfiguration data) {
        File file = new File(dataFolder, filename + ".yml");
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save global data: " + filename, e);
        }
    }

    private File getPlayerFile(UUID uuid) {
        File folder = new File(dataFolder, "playerdata");
        return new File(folder, uuid.toString() + ".yml");
    }
}
