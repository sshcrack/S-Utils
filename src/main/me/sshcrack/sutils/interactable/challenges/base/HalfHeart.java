package me.sshcrack.sutils.interactable.challenges.base;

import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class HalfHeart extends Challenge {
    public HalfHeart() {
        super(
                "half_heart",
                new Properties()
                        .item(new ItemStack(Material.REDSTONE_BLOCK))
                        .timerEnabled()
        );
    }

    private void setHealth(Player p, double health) {
        AttributeInstance attribute = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert attribute != null;
        attribute.setBaseValue(health);
    }

    @Override
    public void onRegister() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            setHealth(player, 1);
        }
    }

    @Override
    public void onUnregister() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            setHealth(player, 20);
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        setHealth(e.getPlayer(), 1);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        setHealth(e.getPlayer(), 20);
    }
}
