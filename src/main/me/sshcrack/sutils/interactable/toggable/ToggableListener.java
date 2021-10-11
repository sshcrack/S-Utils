package me.sshcrack.sutils.interactable.toggable;

import me.sshcrack.sutils.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class ToggableListener extends Toggleable implements Listener {

    public ToggableListener(String id, Properties properties) {
        super(id, properties);
    }

    @Override
    public void enable() {
        super.enable();
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(this, Main.plugin);

        this.onRegister();
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);

        this.onUnregister();
    }

    public void onRegister() {}
    public void onUnregister() {}

}
