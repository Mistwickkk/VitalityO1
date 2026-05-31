package me.vitalityo1.listeners;

import me.vitalityo1.VitalityO1;
import me.vitalityo1.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SpawnProtectionListener implements Listener {

    private final VitalityO1 plugin;

    public SpawnProtectionListener(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (!isProtected()) return;
        Player player = event.getPlayer();
        if (canBypass(player)) return;
        if (inProtectedZone(event.getBlock().getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendRaw(player, "&cYou cannot break blocks in the spawn protection zone.");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (!isProtected()) return;
        Player player = event.getPlayer();
        if (canBypass(player)) return;
        if (inProtectedZone(event.getBlock().getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendRaw(player, "&cYou cannot place blocks in the spawn protection zone.");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPvP(EntityDamageByEntityEvent event) {
        if (!isProtected()) return;
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (canBypass(damager)) return;
        if (inProtectedZone(event.getEntity().getLocation())) {
            event.setCancelled(true);
            MessageUtil.sendRaw(damager, "&cPvP is disabled in the spawn protection zone.");
        }
    }

    private boolean isProtected() {
        return plugin.getConfig().getBoolean("spawn-protection.enabled", true)
                && plugin.getConfig().getBoolean("modules.spawn-protection", true);
    }

    private boolean canBypass(Player player) {
        boolean opsCanBypass = plugin.getConfig().getBoolean("spawn-protection.ops-bypass", true);
        return player.hasPermission("vitalityo1.admin") || (opsCanBypass && player.isOp());
    }

    private boolean inProtectedZone(Location loc) {
        int radius = plugin.getConfig().getInt("spawn-protection.radius", 16);
        String spawnWorld = plugin.getConfig().getString("spawn.world", "");
        if (loc.getWorld() == null || !loc.getWorld().getName().equals(spawnWorld)) return false;

        double cx = plugin.getConfig().getDouble("spawn.x", 0);
        double cz = plugin.getConfig().getDouble("spawn.z", 0);

        double dx = loc.getX() - cx;
        double dz = loc.getZ() - cz;
        return (dx * dx + dz * dz) <= (double) radius * radius;
    }
}
