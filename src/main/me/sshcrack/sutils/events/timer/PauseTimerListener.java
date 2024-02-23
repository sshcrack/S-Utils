package me.sshcrack.sutils.events.timer;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.timer.TimerState;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class PauseTimerListener implements Listener {
    Main plugin = Main.plugin;

    boolean enabled = false;
    PluginManager manager = this.plugin.getServer().getPluginManager();

    public void enable() {
        if(enabled)
            return;

        manager.registerEvents(this, plugin);
        enabled = true;
    }

    public void disable() {
        if(!enabled)
            return;

        HandlerList.unregisterAll(this);
        enabled = false;
    }

    public void register() {
        UtilTimer.instance.onStateChange.add(state -> {
            if (TimerState.RUNNING == state)
                disable();
            else
                enable();
        });

        UtilTimer.instance.onTimerUpdate.add((state, format) -> {
            Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
            boolean paused = state == TimerState.PAUSED;

            String actionBar = paused ?
                    MessageManager.getMessage("timer.state.paused") :
                    ChatColor.GOLD + format;

            for (Player player : players) {
                player.sendActionBar(Component.text(actionBar));
            }
        });
    }

    public PauseTimerListener() {
        if(UtilTimer.isPaused())
            enable();

        register();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemInteractEvent(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    public static HashMap<Entity, ArrayList<Entity>> changedEntities = new HashMap<>();

    @EventHandler
    public void onEntityTargetEvent(EntityTargetEvent event) {
        ArrayList<Entity> entities = changedEntities.computeIfAbsent(event.getEntity(), k -> new ArrayList<>());

        if (event.getTarget() != null) {
            entities.add(event.getTarget());
            event.setCancelled(true);
        }

        changedEntities.put(event.getEntity(), entities);
    }

    @EventHandler
    public void onPlayerItemPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        addNewPlayer(event.getPlayer());
    }

    @EventHandler
    public void onDamageEvent(EntityDamageEvent damageEvent) {
        if(damageEvent.getEntity() instanceof Player)
            damageEvent.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteractEvent(EntityInteractEvent event) {
        if (event.getEntity() instanceof Player) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropEvent(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityHurtByPlayer(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if(damager instanceof Player)
            e.setCancelled(true);
    }

    public static void addNewPlayer(Player player) {
        List<Entity> nearby = player.getNearbyEntities(20, 20, 20);

        nearby.forEach(entity -> {
            if (entity instanceof Creature) {
                Creature creature = (Creature) entity;
                if (creature.getTarget() != null) {
                    if (creature.getTarget().getUniqueId().toString().equals(player.getUniqueId().toString())) return;
                }
                if (creature.getTarget() == null) return;

                creature.setTarget(null);

                ArrayList<Entity> entities = PauseTimerListener.changedEntities.get(entity);

                if (entities == null) {
                    entities = new ArrayList<>();
                }

                entities.add(player);

                PauseTimerListener.changedEntities.put(entity, entities);
            }
        });
    }
}
