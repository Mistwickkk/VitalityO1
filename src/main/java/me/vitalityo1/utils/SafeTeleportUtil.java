package me.vitalityo1.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class SafeTeleportUtil {

    private SafeTeleportUtil() {}

    public static Location findSafe(Location origin) {
        if (origin == null) return null;
        Location loc = origin.clone();
        if (isSafe(loc)) return center(loc);

        for (int y = loc.getBlockY(); y <= loc.getBlockY() + 10; y++) {
            Location candidate = loc.clone();
            candidate.setY(y);
            if (isSafe(candidate)) return center(candidate);
        }

        for (int y = loc.getBlockY(); y >= Math.max(0, loc.getBlockY() - 10); y--) {
            Location candidate = loc.clone();
            candidate.setY(y);
            if (isSafe(candidate)) return center(candidate);
        }

        return center(loc);
    }

    public static boolean isSafe(Location loc) {
        Block feet = loc.getBlock();
        Block head = feet.getRelative(0, 1, 0);
        Block floor = feet.getRelative(0, -1, 0);

        if (!isPassable(feet.getType())) return false;
        if (!isPassable(head.getType())) return false;
        if (!isSolid(floor.getType())) return false;
        if (isHazard(floor.getType())) return false;
        return true;
    }

    private static boolean isPassable(Material m) {
        return m == Material.AIR || m == Material.CAVE_AIR || m == Material.VOID_AIR
                || m == Material.SHORT_GRASS || m == Material.TALL_GRASS
                || m == Material.FERN || m == Material.LARGE_FERN
                || m == Material.SNOW || !m.isSolid();
    }

    private static boolean isSolid(Material m) {
        return m.isSolid();
    }

    private static boolean isHazard(Material m) {
        return m == Material.LAVA || m == Material.FIRE || m == Material.MAGMA_BLOCK
                || m == Material.CAMPFIRE || m == Material.SOUL_CAMPFIRE
                || m == Material.CACTUS || m == Material.SWEET_BERRY_BUSH;
    }

    private static Location center(Location loc) {
        return new Location(
                loc.getWorld(),
                loc.getBlockX() + 0.5,
                loc.getBlockY(),
                loc.getBlockZ() + 0.5,
                loc.getYaw(),
                loc.getPitch()
        );
    }
}
