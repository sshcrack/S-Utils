package me.sshcrack.sutils.tools.timer;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.tools.TwoConsumer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class GeneralTimer {
    public ArrayList<TwoConsumer<TimerState, String>> onTimerUpdate = new ArrayList<>();
    public ArrayList<Consumer<TimerState>> onStateChange = new ArrayList<>();

    public Timer timer = new Timer();

    private long currMillis = 0;
    private long startMillis = new Date().getTime();
    private boolean paused = true;


    private final String configLoc;
    public GeneralTimer(String configLoc) {
        FileConfiguration config = Main.plugin.getConfig();

        boolean time_saved = config.isInt(configLoc) || config.isLong(configLoc);
        if (!time_saved)
            config.set(configLoc, 0L);

        this.currMillis = config.getLong(configLoc);
        this.configLoc = configLoc;
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
        for (TwoConsumer<TimerState, String> func : onTimerUpdate) {
            func.accept(state, format);
        }
    }

    public void notifyState(TimerState state) {
        for (Consumer<TimerState> func : onStateChange) {
            func.accept(state);
        }
    }

    public void resume() {
        if (!this.paused)
            return;

        this.startMillis = new Date().getTime();

        this.paused = false;
        notifyUpdate(TimerState.RUNNING, TimeFormatter.formatTime(currMillis));
        notifyState(TimerState.RUNNING);
    }

    public void pause() {
        if (this.paused)
            return;

        this.paused = true;
        notifyUpdate(TimerState.PAUSED, TimeFormatter.formatTime(currMillis));
        notifyState(TimerState.PAUSED);
    }

    public boolean isPaused() {
        return paused;
    }


    public void reset() {
        currMillis = 0;
        pause();
    }

    public void setMillis(long millis) {
        currMillis = millis;
    }

    public long getMillis() {
        return currMillis;
    }


    /**
     * Used to really disable this client. Can't be undone lol
     */
    public void disable() {
        disable(true);
    }

    public void clearEvents() {
        onTimerUpdate.clear();
        onStateChange.clear();
    }

    /**
     * Used to really disable this client. Can't be undone lol
     */
    public void disable(boolean clearEvents) {
        if (clearEvents) {
            clearEvents();
        }

        timer.cancel();
        timer.purge();

        timer = new Timer();
    }

    public void save() {
        FileConfiguration config  = Main.plugin.getConfig();
        config.set(configLoc, getMillis());
    }
}
