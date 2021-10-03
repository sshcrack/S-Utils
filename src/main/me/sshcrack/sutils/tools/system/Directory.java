package me.sshcrack.sutils.tools.system;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Directory {
    public static void deleteFolder(String folder) {
        if (Files.exists(Paths.get(folder))) {
            try {
                Files.walk(Paths.get(folder)).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyDir(String src, String dest) {
        File srcDir = new File(src);
        File destDir = new File(dest);
        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createPlayerData(String folder) {
        Path path = Paths.get(folder);
        Path playerData = Paths.get(folder, "playerdata");

        try {
            if (Files.notExists(path)) {
                Files.createDirectory(path);
            }

            if (Files.notExists(playerData)) {
                Files.createDirectory(playerData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
