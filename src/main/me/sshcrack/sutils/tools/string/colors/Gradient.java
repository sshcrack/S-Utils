package me.sshcrack.sutils.tools.string.colors;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

public class Gradient {
    public static @NotNull ArrayList<Color> getSteps(@NotNull Color start, @NotNull Color end, int length) {
        ArrayList<Color> colors = new ArrayList<>();

        Double[] startRGB = new Double[] {
                (double) start.getRed(),
                (double) start.getBlue(),
                (double) start.getGreen(),
        };

        Double[] endRGB = new Double[] {
                (double) end.getRed(),
                (double) end.getBlue(),
                (double) end.getGreen(),
        };

        double curr = 0;
        double perStep = 1f / length;

        while (curr < 1) {
            Integer[] rgb = new Integer[3];

            for (int i = 0; i < startRGB.length; i++) {
                rgb[i] = (int) (startRGB[i] * (1 - curr) + endRGB[i] * curr);
            }

            curr += perStep;
            String hex = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);

            Color color = Color.decode(hex);
            colors.add(color);

        }

        return colors;
    }

    public static @NotNull String getGradient(@NotNull Color start, @NotNull Color end, @NotNull String msg) {
        String[] split = msg.split(" ");

        int totalLength = 0;
        for (String str : split) {
            totalLength += str.length();
        }

        ArrayList<Color> colors = getSteps(start, end, totalLength);

        StringBuilder builder = new StringBuilder();
        int currIndex = 0;
        for (String str : split) {
            for (char c : str.toCharArray()) {
                Color color = colors.get(currIndex);

                builder
                        .append(ChatColor.of(color))
                        .append(c);
                currIndex++;
            }

            builder.append(" ");
        }

        return builder.toString();
    }
}
