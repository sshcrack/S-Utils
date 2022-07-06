package me.sshcrack.sutils.interactable.challenges.base;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.function.Supplier;

public class ChunkEffect extends Challenge {
    Random random = new Random();
    HashMap<Player, PotionEffectType> givenEffects = new HashMap<>();

    //Long = Chunk Key
    HashMap<Long, PotionEffect> saved = new HashMap<>();

    int unlimitedEffect = 1000000;
    int maxAmplifier = 5;

    public ChunkEffect() {
        super(
                "chunk_effect",
                new Properties()
                        .item(new ItemStack(Material.POTION))
        );
    }

    @Override
    public void onUnregister() {
        givenEffects.forEach(LivingEntity::removePotionEffect);
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        FileConfiguration config = Main.plugin.getConfig();
        HashMap<Long, PotionEffect> mapped = new HashMap<>();

        List<String> inConfig = config.getStringList("chunk_effect");
        for (String conf : inConfig) {
            String[] splits = conf.split("=");
            if (splits.length != 3)
                continue;

            try {
                long chunkKey = Long.parseLong(splits[0]);
                int amplifier = Integer.parseInt(splits[2]);

                PotionEffectType type = PotionEffectType.getByName(splits[1]);

                if (type == null)
                    continue;

                mapped.put(chunkKey, new PotionEffect(type, unlimitedEffect, amplifier, true, false));
            } catch (NullPointerException | NumberFormatException e) {
                Main.plugin.getLogger().info("Invalid config skipping");
            }
        }

        this.saved = mapped;
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        List<String> list = new ArrayList<>();
        saved.forEach((k, v) -> {
            list.add(k + "=" + v.getType().getName() + "=" + v.getAmplifier());
        });

        Main.plugin.getConfig().set("chunk_effect", list);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        Location toLoc = e.getTo();
        Location fromLoc = e.getFrom();

        Chunk from = fromLoc.getChunk();
        Chunk to = toLoc.getChunk();

        long chunkKey = to.getChunkKey();
        if (from.getChunkKey() == chunkKey)
            return;

        PotionEffectType[] types = PotionEffectType.values();
        int size = types.length;

        Supplier<PotionEffect> getRandomPotion = () -> {
            PotionEffectType type = PotionEffectType.HARM;
            while (type == PotionEffectType.HARM) {
                type = types[random.nextInt(size)];
            }
            int amplifier = random.nextInt(maxAmplifier) + 1; //Prevent 0

            return new PotionEffect(type, unlimitedEffect, amplifier, true, false);
        };

        PotionEffect pot =
                saved.containsKey(chunkKey) && saved.get(chunkKey).getType() != PotionEffectType.HARM ?
                        saved.get(chunkKey) :
                        getRandomPotion.get();

        PotionEffectType previous = givenEffects.get(player);
        if (previous != null)
            player.removePotionEffect(previous);

        player.addPotionEffect(pot);
        givenEffects.put(player, pot.getType());
        saved.put(chunkKey, pot);
    }
}
