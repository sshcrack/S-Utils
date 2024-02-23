package me.sshcrack.sutils.events.death;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.toggable.ToggleableListener;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.Tools;
import me.sshcrack.sutils.tools.world.WorldManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class SpeedrunListener extends ToggleableListener {
    public SpeedrunListener() {
        super("speedrun",
                new Properties()
                        .item(new ItemStack(Material.CLOCK))
                        .enchant()
                        .alwaysEnabled()
                        .statusDescription()
                        .clickToggle()
        );
    }

    private boolean resetting = false;

    public void handleReset(Player player) {
        if(this.resetting)
            return;

        this.resetting = true;
        String root = getRoot();
        String title = MessageManager.getMessage(root + ".died.title");
        String sub = MessageManager.getMessageF(root + ".died.subtitle");
        Title deathTitle = Title.title(Component.text(title), Component.text(sub));

        Tools.getPlayers().showTitle(deathTitle);
        Bukkit
                .getScheduler()
                .runTaskLater(Main.plugin, () -> WorldManager.resetWorld(player), 20L * 10);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Bukkit.getLogger().info(String.format("Player death event %s", e.isCancelled()));
        if(e.isCancelled())
            return;

        Player player = e.getEntity();
        handleReset(player);
    }
}
