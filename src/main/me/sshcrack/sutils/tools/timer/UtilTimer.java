package me.sshcrack.sutils.tools.timer;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.tools.TwoConsumer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class UtilTimer {
    public static final String CONFIG_LOC = "timer.curr";
    public static GeneralTimer instance = new GeneralTimer(CONFIG_LOC);

    public static void disable(boolean clearEvents) {
        instance.disable(clearEvents);
    }

    public static void disable() {
        disable(true);
    }

    public static void save() {
        instance.save();
    }

    public static boolean isPaused() {
        return instance.isPaused();
    }

    public static void resume() {
        instance.resume();
    }

    public static void pause() {
        instance.pause();
    }

    public static void reset() {
        instance.reset();
    }

    public static long getMillis() {
        return instance.getMillis();
    }
}
