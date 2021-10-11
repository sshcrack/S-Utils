package me.sshcrack.sutils.tools.location;

import com.google.common.collect.Lists;
import me.sshcrack.sutils.Main;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class PositionManager {
    private static List<Position> positions = new ArrayList<>();
    static {
        loadConfig();
    }

    public static @Nullable Position get(String name, boolean raw) {
        if(!raw)
            throw new IllegalArgumentException("You should be calling PositionManager#get(name)");

        List<Position> matching = positions
                .stream()
                .filter(e -> e.getName().equals(name))
                .collect(Collectors.toList());
        if(matching.size() == 0)
            return null;

        return matching.get(0);
    }

    public static @Nullable Location get(String name) {
        Position pos = get(name, true);
        if(pos == null)
            return null;

        return pos.getLocation();
    }

    public static boolean exists(String name) {
        return get(name) != null;
    }

    public static boolean add(Position position) {
        boolean exists = exists(position.getName());
        if(exists)
            return false;

        positions.add(position);
        saveConfig();
        return true;
    }

    public static boolean remove(String name) {
        Position pos = get(name, true);

        if(pos == null)
            return false;

        positions.remove(pos);
        return true;
    }

    public static void reset() {
        positions.clear();
        saveConfig();
    }

    public static List<Position> list() {
        positions.sort(Comparator.comparing(Position::getName));
        return positions;
    }

    public static List<String> listNames() {
        List<Position> positions = list();

        return positions
                .stream()
                .map(Position::getName)
                .collect(Collectors.toList());
    }

    private static void loadConfig() {
        FileConfiguration config = Main.plugin.getConfig();
        List<String> positionsRaw =  config.getStringList("positions");
        positions = positionsRaw
                .stream()
                .map(Position::fromString)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static void saveConfig() {
        FileConfiguration config = Main.plugin.getConfig();
        List<String> stringified = positions
                .stream()
                .map(Position::toString)
                .collect(Collectors.toList());

        config.set("positions", stringified);
    }
}
