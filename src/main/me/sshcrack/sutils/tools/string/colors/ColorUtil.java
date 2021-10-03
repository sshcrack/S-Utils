package me.sshcrack.sutils.tools.string.colors;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    public static String translateColors(String text) {
        String translated = ChatColor.translateAlternateColorCodes('&', text);
        String withGradients = translateGradients(translated);

        Pattern pattern = Pattern.compile("<HEX#+.{6}>");
        Matcher matcher = pattern.matcher(withGradients);

        return matcher.replaceAll((res) -> {
            String raw = res.group();
            String hex = raw.replace("<HEX", "").replace(">", "");


            Color color = Color.decode(hex);
            return ChatColor.of(color) + "";
        });
    }

    public static String translateGradients(String msg) {
        //Matches <GRA#e31010,#e310t0>example</GRA> -> <GRA#e31010,#e310t0>example</GRA>
        Pattern outerPattern = Pattern.compile("<GRA#.{6},#.{6}>.*</GRA>");

        Matcher matcher = outerPattern.matcher(msg);

        return matcher.replaceAll((res) -> {
            String raw = res.group();
            Color[] colors = getGradientColors(raw);
            String text = getGradientText(raw);

            if(colors == null || text == null)
                return raw;

            Color start = colors[0];
            Color end = colors[1];

            return Gradient.getGradient(start, end, text);
        });
    }

    private static @Nullable Color[] getGradientColors(@NotNull String raw) {
        //Matches <GRA#e31010,#e310t0>example</GRA> -> #e31010,#e310t0
        Pattern pattern = Pattern.compile("(?<=<GRA)#.{6},#.{6}(?=((>.*(</GRA>))|(,#.{6}>.*</GRA>)))");
        Matcher matcher = pattern.matcher(raw);
        if(!matcher.find())
            return null;

        String group = matcher.group();
        String[] parts = group.split(",", 2);

        return new Color[]{
                Color.decode(parts[0]),
                Color.decode(parts[1])
        };
    }

    private static @Nullable String getGradientText(@NotNull String raw) {
        //Matches <GRA#e31010,#e310t0>example</GRA> -> example
        Pattern pattern = Pattern.compile("(?<=<GRA#.{6},#.{6}>).*(?=</GRA>)");
        Matcher matcher = pattern.matcher(raw);
        if(!matcher.find())
            return null;

        return matcher.group();
    }
}
