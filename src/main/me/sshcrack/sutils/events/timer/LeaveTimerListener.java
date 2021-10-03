package me.sshcrack.sutils.events.timer;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import java.util.Collection;

public class LeaveTimerListener implements Listener {
    Main plugin = Main.plugin;
    PluginManager manager = this.plugin.getServer().getPluginManager();

    public LeaveTimerListener() {
        manager.registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
        if(players.size() != 0)
            return;

        if(!UtilTimer.isPaused())
            UtilTimer.pause();
    }
}
