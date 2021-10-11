package me.sshcrack.sutils.tools.string;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.string.colors.ColorUtil;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringFormatter {
    private static final Main plugin = Main.plugin;

    public static String replaceLast(String str, String oldValue, String newValue) {
        str = StringUtils.reverse(str);
        str = str.replaceFirst(StringUtils.reverse(oldValue), StringUtils.reverse(newValue));
        str = StringUtils.reverse(str);
        return str;
    }

    public static String repeat(String original, int repeat) {
        StringBuilder repeated = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            repeated.append(original);
        }

        return repeated.toString();
    }

    public static String getKickMSG(String issuer) {
        FileConfiguration msgFile = MessageManager.getMessages();

        String bars = msgFile.getString("reset.kick.bars");
        assert bars != null;

        String barColored = ChatColor.translateAlternateColorCodes('&', bars);

        String appendStart = MessageManager.getMessage("reset.kick.append.start");
        int startLength = ChatColor.stripColor(appendStart).length();

        String appendEnd = MessageManager.getMessage("reset.kick.append.end");
        int endLength = ChatColor.stripColor(appendEnd).length();

        List<String> lines = msgFile.getStringList("reset.kick.msg.lines");
        List<Map<?, ?>> colors = msgFile.getMapList("reset.kick.msg.colors");

        if (bars.equals("\n"))
            return "Invalid config file.";

        StringBuilder finalString = new StringBuilder();

        finalString.append(barColored);
        finalString.append("\n");

        for (int i = 0; i < lines.size(); i++) {
            int tempFinal = i;

            String line = lines.get(i);
            List<Map<?, ?>> matchingColors = colors
                    .stream()
                    .filter(e -> {
                        Object confLine = e.get("line");
                        if (!(confLine instanceof Integer))
                            return false;

                        int confInt = (int) confLine;
                        return confInt == tempFinal;
                    })
                    .collect(Collectors.toList());


            String withPlayerName = line.replace("INSERTPLAYERNAME", issuer);
            String colored = addColors(withPlayerName, matchingColors);
            String centered = CentredMessage.generate(colored);

            String firstToReplace = centered.substring(0, startLength);
            String lastToReplace = centered.substring(centered.length() - 1);

            String appendedStart = centered.replaceFirst(firstToReplace, appendStart);
            String appendedEnd = replaceLast(appendedStart, lastToReplace, appendEnd);

            finalString.append(appendedEnd);
            finalString.append("\n");
        }

        finalString.append(bars);
        finalString.append("\n\n");

        String version = ChatColor.GRAY + plugin.getDescription().getVersion();
        finalString.append(version);


        return ChatColor.translateAlternateColorCodes('&', finalString.toString());
    }

    private static String addColors(String original, List<Map<?, ?>> colors) {

        String output = original;
        for (Map<?, ?> matching : colors) {
            Object colorObj = matching.get("color");
            Object beforeObj = matching.get("before" );
            Object afterObj = matching.get("after");

            boolean beforeStr = beforeObj instanceof String;
            boolean afterStr = afterObj instanceof String;
            if(!beforeStr && !afterStr)
                continue;

            if(!(colorObj instanceof String))
                continue;

            String colorStr = (String) colorObj;

            String search = (String) (beforeStr ? beforeObj : afterObj);
            String replacement = beforeStr ? colorStr + beforeObj : afterObj + colorStr;

            output = output.replace(search, replacement);
            output = ColorUtil.translate(output);
        }

        return output;
    }
}
