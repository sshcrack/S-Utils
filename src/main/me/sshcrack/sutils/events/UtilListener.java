package me.sshcrack.sutils.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.message.MessageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

public class UtilListener implements Listener {
    Main plugin;
    ScoreboardManager manager;

    public UtilListener(Main plugin) {
        this.plugin = plugin;
        this.manager = plugin.getServer().getScoreboardManager();
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        String name = e.getPlayer().getDisplayName();
        String quitMSG = MessageManager.getMessageF("events.leave", name);

        e.quitMessage(Component.text(quitMSG));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getDisplayName();
        String joinMSG = MessageManager.getMessageF("events.join", name);

        e.joinMessage(Component.text(joinMSG));

        Scoreboard board = manager.getMainScoreboard();
        Objective objective = board.getObjective("showhealth");
        if (objective != null)
            return;

        String dName = ChatColor.RED + "\u2665";
        objective = board.registerNewObjective("showhealth", "health", Component.text(dName), RenderType.HEARTS);
        objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }
}