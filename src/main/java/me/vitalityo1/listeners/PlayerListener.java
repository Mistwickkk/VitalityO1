package me.vitalityo1.listeners;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final VitalityO1 plugin;
    private final Set<UUID> vanished = new HashSet<>();
    private final Set<UUID> godMode = new HashSet<>();
    private final Map<UUID, Long> lastSeen = new HashMap<>();
    private final Map<UUID, UUID> lastMessaged = new HashMap<>();

    public PlayerListener(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        plugin.getEconomyManager().ensureLoaded(uuid);

        String msg = plugin.getConfig().getString("messages.join-message", "&a+ &e{player} &ajoined the server.");
        event.setJoinMessage(MessageUtil.color(msg.replace("{player}", player.getName())));

        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (vanished.contains(online.getUniqueId())) {
                player.hidePlayer(plugin, online);
            }
        }

        lastSeen.put(uuid, System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        String msg = plugin.getConfig().getString("messages.quit-message", "&c- &e{player} &cleft the server.");
        event.setQuitMessage(MessageUtil.color(msg.replace("{player}", player.getName())));

        plugin.getTeleportManager().cancelTeleport(uuid);
        plugin.getTeleportManager().cancelTpaRequest(uuid);

        lastSeen.put(uuid, System.currentTimeMillis());
        saveLastSeen(uuid);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) return;

        UUID uuid = player.getUniqueId();
        if (plugin.getTeleportManager().hasPendingTeleport(uuid)
                && plugin.getTeleportManager().movedDuringWarmup(uuid, to)) {
            plugin.getTeleportManager().cancelTeleport(uuid);
            MessageUtil.send(player, "messages.teleport-cancelled");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        plugin.getTeleportManager().setBack(player.getUniqueId(), from);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (godMode.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        plugin.getTeleportManager().setBack(player.getUniqueId(), player.getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        plugin.getTeleportManager().setBack(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
    }

    public void vanishPlayer(Player player) {
        vanished.add(player.getUniqueId());
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            if (!other.hasPermission("vitalityo1.vanish")) {
                other.hidePlayer(plugin, player);
            }
        }
    }

    public void unvanishPlayer(Player player) {
        vanished.remove(player.getUniqueId());
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            other.showPlayer(plugin, player);
        }
    }

    public boolean isVanished(UUID uuid) {
        return vanished.contains(uuid);
    }

    public void setGod(UUID uuid, boolean enabled) {
        if (enabled) godMode.add(uuid);
        else godMode.remove(uuid);
    }

    public boolean isGod(UUID uuid) {
        return godMode.contains(uuid);
    }

    public long getLastSeen(UUID uuid) {
        Long l = lastSeen.get(uuid);
        if (l != null) return l;
        return loadLastSeen(uuid);
    }

    private long loadLastSeen(UUID uuid) {
        org.bukkit.configuration.file.YamlConfiguration data = plugin.getDataManager().loadPlayerData(uuid);
        long time = data.getLong("last-seen", 0L);
        if (time > 0) lastSeen.put(uuid, time);
        return time;
    }

    private void saveLastSeen(UUID uuid) {
        org.bukkit.configuration.file.YamlConfiguration data = plugin.getDataManager().loadPlayerData(uuid);
        data.set("last-seen", System.currentTimeMillis());
        plugin.getDataManager().savePlayerData(uuid, data);
    }

    public void setLastMessaged(UUID sender, UUID receiver) {
        lastMessaged.put(sender, receiver);
        lastMessaged.put(receiver, sender);
    }

    public UUID getLastMessaged(UUID uuid) {
        return lastMessaged.get(uuid);
    }
}
