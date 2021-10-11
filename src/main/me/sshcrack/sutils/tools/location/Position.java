package me.sshcrack.sutils.tools.location;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Position implements Serializable {
    private final Location loc;
    private final String name;

    public Position(String name, Location loc) {
        if(!checkValidName(name))
            throw new IllegalArgumentException("Invalid name provided.");

        this.loc = loc;
        this.name = name;
    }

    public static boolean checkValidName(String name) {
        Pattern validPattern = Pattern.compile("^(?![0-9]*$)[a-zA-Z0-9]+$");
        Matcher matcher = validPattern.matcher(name);

        return matcher.find();
    }

    public static @Nullable Position fromString(String str) {
        Pattern namePattern = Pattern.compile("(?<=').*(?='\\(.*\\))");
        Pattern locPattern = Pattern.compile("(?<=\\().*(?=\\))");

        Matcher nameMatcher = namePattern.matcher(str);
        Matcher locMatcher = locPattern.matcher(str);

        if(!nameMatcher.find() || !locMatcher.find())
            return null;

        String name = nameMatcher.group();
        String strLoc = locMatcher.group();

        Location loc = LocTools.strToLoc(strLoc);

        if(!checkValidName(name))
            return null;

        return new Position(name, loc);
    }

    @Override
    public String toString() {
        String strLoc = LocTools.locToStr(loc);

        return String.format("'%s'(%s)", name, strLoc);
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return loc;
    }
}
