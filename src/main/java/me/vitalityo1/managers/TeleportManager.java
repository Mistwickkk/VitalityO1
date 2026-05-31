package me.vitalityo1.managers;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import me.vitalityo1.utils.SafeTeleportUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final VitalityO1 plugin;

    private final Map<UUID, BukkitTask> pendingTeleports = new HashMap<>();
    private final Map<UUID, Location> startLocations = new HashMap<>();
    private final Map<UUID, Location> backLocations = new HashMap<>();
    private final Map<UUID, UUID> tpaRequests = new HashMap<>();
    private final Map<UUID, BukkitTask> tpaTimeouts = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public TeleportManager(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    public void scheduleTeleport(Player player, Location destination, Runnable onComplete) {
        cancelTeleport(player.getUniqueId());
        int delay = plugin.getConfig().getInt("teleport.delay", 3);
        if (delay <= 0) {
            safeTP(player, destination);
            if (onComplete != null) onComplete.run();
            return;
        }
        MessageUtil.send(player, "messages.teleport-warmup",
                "{seconds}", String.valueOf(delay));
        startLocations.put(player.getUniqueId(), player.getLocation().clone());
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            pendingTeleports.remove(player.getUniqueId());
            startLocations.remove(player.getUniqueId());
            safeTP(player, destination);
            if (onComplete != null) onComplete.run();
        }, delay * 20L);
        pendingTeleports.put(player.getUniqueId(), task);
    }

    public void scheduleTeleport(Player player, Location destination) {
        scheduleTeleport(player, destination, null);
    }

    public void safeTP(Player player, Location destination) {
        Location safe = SafeTeleportUtil.findSafe(destination);
        Location prev = player.getLocation().clone();
        player.teleport(safe);
        setCooldown(player.getUniqueId());
        setBack(player.getUniqueId(), prev);
    }

    public void cancelTeleport(UUID uuid) {
        BukkitTask task = pendingTeleports.remove(uuid);
        if (task != null) task.cancel();
        startLocations.remove(uuid);
    }

    public boolean hasPendingTeleport(UUID uuid) {
        return pendingTeleports.containsKey(uuid);
    }

    public boolean movedDuringWarmup(UUID uuid, Location current) {
        Location start = startLocations.get(uuid);
        if (start == null) return false;
        return start.distanceSquared(current) > 0.09;
    }

    public void setBack(UUID uuid, Location location) {
        backLocations.put(uuid, location.clone());
    }

    public Location getBack(UUID uuid) {
        return backLocations.get(uuid);
    }

    public void requestTpa(Player requester, Player target) {
        cancelTpaRequest(requester.getUniqueId());
        tpaRequests.put(target.getUniqueId(), requester.getUniqueId());
        int timeout = plugin.getConfig().getInt("teleport.tpa-timeout", 60);
        BukkitTask timeoutTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (tpaRequests.get(target.getUniqueId()) != null &&
                    tpaRequests.get(target.getUniqueId()).equals(requester.getUniqueId())) {
                tpaRequests.remove(target.getUniqueId());
                tpaTimeouts.remove(requester.getUniqueId());
                if (requester.isOnline()) {
                    MessageUtil.send(requester, "messages.tpa-expired",
                            "{player}", target.getName());
                }
            }
        }, timeout * 20L);
        BukkitTask old = tpaTimeouts.put(requester.getUniqueId(), timeoutTask);
        if (old != null) old.cancel();
    }

    public UUID getTpaRequester(UUID target) {
        return tpaRequests.get(target);
    }

    public void cancelTpaRequest(UUID requesterUUID) {
        tpaRequests.values().remove(requesterUUID);
        BukkitTask t = tpaTimeouts.remove(requesterUUID);
        if (t != null) t.cancel();
    }

    public void acceptTpa(Player target) {
        UUID requesterUUID = tpaRequests.remove(target.getUniqueId());
        if (requesterUUID == null) return;
        BukkitTask t = tpaTimeouts.remove(requesterUUID);
        if (t != null) t.cancel();
        Player requester = plugin.getServer().getPlayer(requesterUUID);
        if (requester != null && requester.isOnline()) {
            scheduleTeleport(requester, target.getLocation());
            MessageUtil.send(requester, "messages.tpa-accepted");
        }
    }

    public void denyTpa(Player target) {
        UUID requesterUUID = tpaRequests.remove(target.getUniqueId());
        if (requesterUUID == null) return;
        BukkitTask t = tpaTimeouts.remove(requesterUUID);
        if (t != null) t.cancel();
        Player requester = plugin.getServer().getPlayer(requesterUUID);
        if (requester != null && requester.isOnline()) {
            MessageUtil.send(requester, "messages.tpa-denied");
        }
    }

    public boolean isOnCooldown(UUID uuid) {
        long cooldownSecs = plugin.getConfig().getLong("teleport.cooldown", 30);
        Long last = cooldowns.get(uuid);
        if (last == null) return false;
        return (System.currentTimeMillis() - last) < cooldownSecs * 1000L;
    }

    public long getRemainingCooldown(UUID uuid) {
        long cooldownSecs = plugin.getConfig().getLong("teleport.cooldown", 30);
        Long last = cooldowns.get(uuid);
        if (last == null) return 0;
        long remaining = (cooldownSecs * 1000L) - (System.currentTimeMillis() - last);
        return Math.max(0, remaining / 1000);
    }

    private void setCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

    public void cancelAll() {
        pendingTeleports.values().forEach(BukkitTask::cancel);
        pendingTeleports.clear();
        tpaTimeouts.values().forEach(BukkitTask::cancel);
        tpaTimeouts.clear();
    }
}
