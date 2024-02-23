package me.sshcrack.sutils.interactable.challenges.base;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TNTRun extends Challenge {
    private final HashMap<TNTPrimed, Location> originalLocations = new HashMap<>();

    private final Main plugin;
    private final String name = "TNTRUN_TNT";
    @Nullable BukkitTask task;

    public TNTRun() {
        super(
                "tnt_run",
                new Properties()
                        .item(new ItemStack(Material.TNT))
                        .timerEnabled()
        );
        this.plugin = Main.plugin;
    }

    public void onTick() {
        List<TNTPrimed> toRemove = new ArrayList<>();
        originalLocations.forEach((k, v) -> {
            if(k.isDead()) {
                toRemove.add(k);
                return;
            }

            k.teleportAsync(v);
            k.setVelocity(new Vector(0,0,0));
        });

        for (TNTPrimed primed : toRemove) {
            originalLocations.remove(primed);
        }
    }


    @Override
    public void onRegister() {
        if (task != null && !task.isCancelled())
            task.cancel();

        if(plugin == null) {
            Bukkit.getLogger().info("Plugin is null");
            Bukkit.getLogger().info(Main.plugin.toString());
            return;
        }

        task = Bukkit.getScheduler().runTaskTimer(plugin, this::onTick, 0L, 10L);
    }

    @Override
    public void onUnregister() {
        if (task == null)
            return;

        task.cancel();
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if(!(e.getEntity() instanceof TNTPrimed))
            return;
        String customName = e.getEntity().getCustomName();
        if(customName == null || !customName.equals(name))
            return;

        e.setCancelled(true);
        Entity en = e.getEntity();
        for (Player player : Bukkit.getOnlinePlayers()) {
            double distance = player.getLocation().distance(en.getLocation());
            if(distance > 20)
                return;
            player.playSound(en.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.BLOCKS, 5, 1);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Block to = e.getTo().getBlock();
        Block from = e.getFrom().getBlock();
        Player p = e.getPlayer();

        if(p.getGameMode() != GameMode.SURVIVAL)
                return;

        if(to.getX() == from.getX() && to.getZ() == from.getZ())
                return;

        Location spawnPoint = from.getLocation().toCenterLocation();
        spawnPoint.setY(from.getY() -1.5);
        TNTPrimed tnt = from.getWorld().spawn(spawnPoint, TNTPrimed.class);
        tnt.setFuseTicks(8 * 20);
        tnt.setGravity(false);
        tnt.setGlowing(true);

        tnt.setCustomName(name);
        tnt.setCustomNameVisible(false);

        originalLocations.put(tnt, spawnPoint);
    }
}
