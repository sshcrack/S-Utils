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
    public static ArrayList<TwoConsumer<TimerState, String>> onTimerUpdate = new ArrayList<>();
    public static ArrayList<Consumer<TimerState>> onStateChange = new ArrayList<>();

    public static Timer timer = new Timer();
    public static UtilTimer instance = new UtilTimer();
    public static final String currLoc = "timer.curr";

    private long currMillis = 0;
    private long startMillis = new Date().getTime();
    private boolean paused = true;



    public UtilTimer() {
        FileConfiguration config = Main.plugin.getConfig();

        boolean time_saved = config.isInt(currLoc) || config.isLong(currLoc);
        if (!time_saved)
            config.set(currLoc, 0L);

        this.currMillis = config.getLong(currLoc);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String format = TimeFormatter.formatTime(currMillis);
                if (paused) {
                    notifyUpdate(TimerState.PAUSED, format);
                    return;
                }

                long now = new Date().getTime();
                long diff = now - startMillis;

                currMillis += diff;
                startMillis = now;


                notifyUpdate(TimerState.RUNNING, format);
            }
        }, 0, 1000);
    }

    public void notifyUpdate(TimerState state, String format) {
        for (TwoConsumer<TimerState, String> func : UtilTimer.onTimerUpdate) {
            func.accept(state, format);
        }
    }

    public void notifyState(TimerState state) {
        for (Consumer<TimerState> func : UtilTimer.onStateChange) {
            func.accept(state);
        }
    }

    public void resumePrivate() {
        if (!this.paused)
            return;

        this.startMillis = new Date().getTime();

        this.paused = false;
        notifyUpdate(TimerState.RUNNING, TimeFormatter.formatTime(currMillis));
        notifyState(TimerState.RUNNING);
    }

    public void pausePrivate() {
        if (this.paused)
            return;

        this.paused = true;
        notifyUpdate(TimerState.PAUSED, TimeFormatter.formatTime(currMillis));
        notifyState(TimerState.PAUSED);
    }

    public static void disable() {
        UtilTimer.onTimerUpdate.clear();
        UtilTimer.onStateChange.clear();

        UtilTimer.timer.cancel();
        UtilTimer.timer.purge();

        UtilTimer.timer = new Timer();
    }

    public static boolean isPaused() {
        return UtilTimer.instance.paused;
    }

    public static void resume() {
        UtilTimer.instance.resumePrivate();
    }

    public static void pause() {
        UtilTimer.instance.pausePrivate();
    }

    public static void reset() {
        instance.currMillis = 0;
        pause();
    }

    public static long getMillis() {
        return instance.currMillis;
    }
}
