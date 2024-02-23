package me.sshcrack.sutils.interactable.toggable;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.tools.timer.TimerState;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public abstract class ToggleableListener extends Toggleable implements Listener {
    //If the challenge is listening for events
    private boolean eventsRegistered = false;
    private final ToggleableListener.Properties properties;


    public ToggleableListener(String id, ToggleableListener.Properties properties) {
        super(id, properties);

        this.properties = properties;
    }


    /**
     * This function is called when the player clicks on the challenge and enables it

     * If property timer_enable is true, this will register and unregister
     * events for the challenge.
     * If property always_enabled is true, nothing will happen really, it'll just register events
     */
    @Override
    public void onEnable() {
        Main.plugin.getLogger().info(String.format("Enabling toggleable %s", getRoot()));
        super.onEnable();

        ToggleableListener.Properties props = (Properties) this.getProperties();
        Main.plugin.getLogger().info(String.format("Timer enabled %s", props.timer_enabled));
        if(props.timer_enabled) {
            UtilTimer.instance.onStateChange.add(state -> {
                Main.plugin.getLogger().info(String.format("State change %s state %s", props.always_enabled, state));
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

        if(props.always_enabled)
            registerEvents();
    }

    /**
     * Called when the player clicks on the challenge item again
     * and disables the challenge
     */
    @Override
    public void onDisable() {
        super.onDisable();
        Main.plugin.getLogger().info("Disabling");

        unregister();
    }

    /**
     * Internal method. Not suggested to overwrite
     */
    public void registerEvents() {
        if(eventsRegistered)
            return;

        Main.plugin.getLogger().info("Registering");
        eventsRegistered = true;
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(this, Main.plugin);

        this.onRegister();
    }

    /**
     * Internal method. Not suggested to overwrite
     */
    public void unregister() {
        if(!eventsRegistered)
            return;

        Main.plugin.getLogger().info("Unregister");
        eventsRegistered = false;
        HandlerList.unregisterAll(this);

        onUnregister();
    }

    /**
     * Called when the timer is resumed
     */
    public void onRegister() {};

    /**
     * Called when the timer is paused
     */
    public void onUnregister() {};

    public static class Properties extends GenericProperties<ToggleableListener.Properties> {
        private boolean timer_enabled = false;
        private boolean always_enabled = false;

        /**
         * If the timer is enabled, the challenge will register and unregister
         */
        public ToggleableListener.Properties timerEnabled() {
            timer_enabled = true;

            return this;
        }

        /**
         * Always enabled, even though the timer is paused
         */
        public ToggleableListener.Properties alwaysEnabled() {
            always_enabled = true;

            return this;
        }
    }
}
