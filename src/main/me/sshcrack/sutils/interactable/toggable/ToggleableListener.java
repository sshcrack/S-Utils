package me.sshcrack.sutils.interactable.toggable;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.tools.timer.TimerState;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class ToggleableListener extends Toggleable implements Listener {
    //If the challenge is listening for events
    private boolean eventsRegistered = false;
    private final ToggleableListener.Properties properties;


    public ToggleableListener(String id, ToggleableListener.Properties properties) {
        super(id, properties);

        this.properties = properties;
    }

    @Override
    public void enable() {
        super.enable();

        ToggleableListener.Properties props = (Properties) this.getProperties();
        if(props.timer_enabled) {
            UtilTimer.onStateChange.add(state -> {
                if(props.always_enabled)
                    return;

                if(state == TimerState.PAUSED)
                    unregister();

                if(!isEnabled())
                    return;

                if(state == TimerState.RUNNING)
                    registerEvents();
            });
        }
    }

    @Override
    public void disable() {
        super.disable();

        unregister();
    }

    public void registerEvents() {
        if(eventsRegistered)
            return;

        eventsRegistered = true;
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(this, Main.plugin);

        this.onRegister();
    }

    public void unregister() {
        if(!eventsRegistered)
            return;

        eventsRegistered = false;
        HandlerList.unregisterAll(this);

        onUnregister();
        if(isEnabled())
            onEnable();
    }

    public void onRegister() {
    }

    public void onUnregister() {
    }

    public static class Properties extends GenericProperties<ToggleableListener.Properties> {
        private boolean timer_enabled = false;
        private boolean always_enabled = false;

        public ToggleableListener.Properties timerEnabled() {
            timer_enabled = true;

            return this;
        }

        public ToggleableListener.Properties alwaysEnabled() {
            always_enabled = true;

            return this;
        }
    }
}
