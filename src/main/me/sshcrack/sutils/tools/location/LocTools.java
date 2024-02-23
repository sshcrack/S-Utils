package me.sshcrack.sutils.tools.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class LocTools {
    public static Location strToLoc(@NotNull String str) {
        String[] split = str.split("@");

        String rawLoc = split[0];
        String worldName = split[1];

        String[] locSplit = rawLoc.split(",");
        double x, y, z;
        float yaw, pitch;

        x = Double.parseDouble(locSplit[0]);
        y = Double.parseDouble(locSplit[1]);
        z = Double.parseDouble(locSplit[2]);

        yaw = Float.parseFloat(locSplit[3]);
        pitch = Float.parseFloat(locSplit[4]);

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        Block block = world.getBlockAt((int) x, (int) y, (int) z);

        Location loc = block.getLocation();

        loc.setX(x);
        loc.setY(y);
        loc.setZ(z);

        loc.setYaw(yaw);
        loc.setPitch(pitch);

        return loc;
    }

    public static String locToStr(@NotNull Location loc) {
        double x, y, z;
        float yaw, pitch;

        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        yaw = loc.getYaw();
        pitch = loc.getPitch();

        return String.format("%,.2f,%,.2f,%,.2f,%f,%f@%s", x, y, z, yaw, pitch, loc.getWorld().getName());
    }

    public static Location worldToNether(Location loc) {
        World nether = Bukkit.getWorld("world_nether");

        Location l = loc.clone().multiply(1f / 8f);
        return new Location(nether, l.getX(), loc.getY(), l.getZ());
    }

    public static Location netherToWorld(Location loc) {
        World overworld = Bukkit.getWorld("world");

        Location l = loc.clone().multiply(8);
        return new Location(overworld, l.getX(), loc.getY(), l.getZ());
    }
}
