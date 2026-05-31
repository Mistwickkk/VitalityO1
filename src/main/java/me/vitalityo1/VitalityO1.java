package me.vitalityo1;

import me.vitalityo1.commands.CommandManager;
import me.vitalityo1.listeners.ChatListener;
import me.vitalityo1.listeners.PlayerListener;
import me.vitalityo1.listeners.SpawnProtectionListener;
import me.vitalityo1.managers.DataManager;
import me.vitalityo1.managers.EconomyManager;
import me.vitalityo1.managers.HomeManager;
import me.vitalityo1.managers.ModerationManager;
import me.vitalityo1.managers.TeleportManager;
import me.vitalityo1.managers.WarpManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class VitalityO1 extends JavaPlugin {

    private static VitalityO1 instance;

    private DataManager dataManager;
    private EconomyManager economyManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private TeleportManager teleportManager;
    private ModerationManager moderationManager;
    private CommandManager commandManager;
    private me.vitalityo1.listeners.PlayerListener playerListener;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.dataManager = new DataManager(this);
        this.economyManager = new EconomyManager(this);
        this.homeManager = new HomeManager(this);
        this.warpManager = new WarpManager(this);
        this.teleportManager = new TeleportManager(this);
        this.moderationManager = new ModerationManager(this);

        this.commandManager = new CommandManager(this);
        commandManager.registerAll();

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        this.playerListener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(new SpawnProtectionListener(this), this);

        getLogger().info("VitalityO1 v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (teleportManager != null) teleportManager.cancelAll();
        if (economyManager != null) economyManager.saveAll();
        if (homeManager != null) homeManager.saveAll();
        if (warpManager != null) warpManager.saveAll();
        if (moderationManager != null) moderationManager.saveAll();

        getLogger().info("VitalityO1 disabled.");
    }

    public static VitalityO1 getInstance() {
        return instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public ModerationManager getModerationManager() {
        return moderationManager;
    }

    public me.vitalityo1.listeners.PlayerListener getPlayerListener() {
        return playerListener;
    }
}
