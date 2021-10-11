package me.sshcrack.sutils.tools;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Tools {
    public static boolean implementsClass(Class<?> toTest, Class<?> toMatch) {
        List<Class<?>> currInterfaces = List.of(toTest.getInterfaces());
        while(currInterfaces.size() != 0) {
            List<Class<?>> nested = new ArrayList<>();
            for (Class<?> currInterface : currInterfaces) {
                if(Objects.equals(currInterface.getCanonicalName(), toMatch.getCanonicalName())) {
                    return true;
                }
                nested.addAll(List.of(currInterface.getInterfaces()));
            }

            currInterfaces = nested;
        }

        return false;
    }

    public static <T> T firstDefault(@Nullable T first, @Nullable T second) {
        return first != null ? first : second;
    }

    public static @Nullable Location getAirAround(Location loc, int radius, int heightNeeded) {
        return getAirAround(loc, radius, heightNeeded, 0);
    }

    public static @Nullable Location getAirAround(Location loc, int radius) {
        return getAirAround(loc, radius, 2, 0);
    }


    public static @Nullable Location getAirAround(Location loc, int radius, int heightNeeded, int minRange) {
        ArrayList<Location>  matchingBlocks = new ArrayList<>();

        for(int x = -radius; x <= radius; x++) {
            for(int z = -radius; z <= radius; z++) {

                for(int rY = -radius; rY <= radius; rY++) {
                    int currAirBlocks = 0;
                    for(int y = -heightNeeded; y < heightNeeded; y++) {
                        Location particleLoc = loc.clone().add(x, y + rY, z);
                        Block block = particleLoc.getBlock();
                        Material type = block.getType();

                        if(!type.isAir() && type.isSolid() && type != Material.LAVA) {
                            currAirBlocks = 0;
                            continue;
                        }


                        currAirBlocks++;
                        if(currAirBlocks !=  heightNeeded)
                            continue;

                        Location spawnLog = particleLoc.clone().add(0, -y, 0);
                        matchingBlocks.add(spawnLog);

                        currAirBlocks = 0;
                    }
                }

            }
        }

        if(matchingBlocks.size() == 0)
            return null;

        Location closestLoc = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for(Location found : matchingBlocks) {
            double distance = found.distance(loc);
            if(distance < minDistance && distance > minRange) {
                minDistance = distance;
                closestLoc = found;
            }
        }

        return closestLoc;
    }

    public static List<Component> stringListToComponent(List<String> strList) {
        return strList
                .stream()
                .map(Component::text)
                .collect(Collectors.toList());
    }

    public static Audience getPlayers() {
        return Audience.audience(Bukkit.getOnlinePlayers());
    }
}
