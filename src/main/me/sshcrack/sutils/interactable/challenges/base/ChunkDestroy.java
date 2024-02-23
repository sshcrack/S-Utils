package me.sshcrack.sutils.interactable.challenges.base;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChunkDestroy extends Challenge {
    private final long DESTROY_INTERVAL = 20L * 6;
    HashMap<Player, Chunk> currChunks = new HashMap<>();


    private final Main plugin;
    @Nullable BukkitTask task;

    public ChunkDestroy() {
        super(
                "chunk_destroy",
                new Properties()
                        .item(new ItemStack(Material.BEDROCK))
                        .timerEnabled()
        );
        this.plugin = Main.plugin;
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

        task = Bukkit.getScheduler().runTaskTimer(plugin, this::onTick, 0L, DESTROY_INTERVAL);
    }

    @Override
    public void onUnregister() {
        if (task == null)
            return;

        task.cancel();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Location to = e.getTo();
        Player player = e.getPlayer();

        if(!currChunks.containsKey(player))
            plugin.getLogger().info(String.format("Registering player %s", player.getDisplayName()));

        currChunks.put(player, to.getChunk());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        currChunks.remove(e.getPlayer());
    }

    public void onTick() {
        if(this.plugin == null)
                return;
        List<Chunk> chunks = new ArrayList<>(currChunks.values());

        for (int i = 0; i < chunks.size(); i++) {
            Chunk chunk = chunks.get(i);

            ArrayList<Block> nonAir = new ArrayList<>();
            for (int x = 0; x < 16; x++) {
                int finalX = x;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {

                    for (int z = 0; z < 16; z++) {
                        for (int y = 150; y >= -67; y--) {
                            Block block = chunk.getBlock(finalX, y, z);
                            Material type = block.getType();

                            if (type == Material.AIR || type == Material.LAVA || type == Material.WATER)
                                continue;

                            nonAir.add(block);
                            break;
                        }
                    }

                    for (Block block : nonAir) {
                        block.setType(Material.AIR);
                    }
                }, i * 6L * (x / 2));
            }
        }
    }
}
