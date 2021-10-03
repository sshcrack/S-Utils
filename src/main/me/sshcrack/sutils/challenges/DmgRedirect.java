package me.sshcrack.sutils.challenges;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.management.BufferPoolMXBean;
import java.util.*;

public class DmgRedirect extends Challenge {
    HashMap<UUID, UUID> mappings = new HashMap<>();


    public DmgRedirect() {
        super(
                new ItemStack(Material.REDSTONE),
                "damage_redirect"
        );
    }

    @EventHandler
    public void onHealthRegenerated(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player))
            return;
        double damage = e.getFinalDamage();

        Player curr = (Player) e.getEntity();
        UUID uuid = curr.getUniqueId();

        UUID targetUUID = mappings.getOrDefault(uuid, uuid);
        Player target = Bukkit.getPlayer(targetUUID);
        if(target == null) {
            rerollPlayers();
            return;
        }

        target.damage(damage);
        target.setLastDamageCause(e);

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        rerollPlayers();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        rerollPlayers();
    }

    public void rerollPlayers() {
        List<? extends Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        HashMap<UUID, UUID> playerList = new HashMap<>();
        if(players.size() == 1) {
            Player onlyPlayer = players.get(0);
            UUID uuid = onlyPlayer.getUniqueId();

            playerList.put(uuid, uuid);
            this.mappings = playerList;
            return;
        }

        Random random = new Random();

        int size = players.size();
        for (Player key : players) {
            Player value = key;

            //You cant have so much unluck alright its illegal
            for(int i = 0; i < size *3; i++) {
                if(value.getUniqueId() != key.getUniqueId())
                    break;

                int randomIndex = random.nextInt(size);
                value = players.get(randomIndex);
            }

            playerList.put(
                    key.getUniqueId(),
                    value.getUniqueId()
            );
        }
    }

    @Override
    public void enable() {
        super.enable();

        rerollPlayers();
    }
}
