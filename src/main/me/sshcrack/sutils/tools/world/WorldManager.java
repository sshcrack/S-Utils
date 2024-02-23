package me.sshcrack.sutils.tools.world;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import me.sshcrack.sutils.interactable.challenges.module.ToggleableList;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.Tools;
import me.sshcrack.sutils.tools.location.PositionManager;
import me.sshcrack.sutils.tools.system.Directory;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WorldManager {
    private static boolean villagerSpawn = false;
    private static boolean seedReset = false;
    private static boolean isResetting = false;

    private static final Main plugin = Main.plugin;
    private static final Logger logger = plugin.getLogger();
    private static final BukkitScheduler scheduler = Bukkit.getScheduler();
    private static final FileConfiguration config = plugin.getConfig();

    private static final String OVERWORLD = "world";
    private static final String NETHER = "world_nether";
    private static final String END = "world_the_end";



    public static void load() {
        isResetting = config.getBoolean("world.reset", false);
        seedReset = config.getBoolean("world.seed", false);
        villagerSpawn = config.getBoolean("world.villager_spawn", false);

        if(!isResetting)
            return;


        Directory.deleteFolder(OVERWORLD);
        Directory.deleteFolder(NETHER);
        Directory.deleteFolder(END);

        Directory.createPlayerData(OVERWORLD);

        if(seedReset) {
            String[] targets = new String[] {
                OVERWORLD,
                NETHER,
                END
            };
            List<String> sources = Arrays
                    .stream(targets)
                    .map(e -> String.format("%s_copy", e))
                    .collect(Collectors.toList());

            for (int x = 0; x < sources.size(); x++) {
                String source = sources.get(x);
                String target = targets[x];

                Directory.copyDir(source, target);
            }
        }

    }

    public static void enable() {
        if(!isResetting)
            return;

        isResetting = false;
        if(!villagerSpawn)
            return;

        World world = Bukkit.getWorld("world");
        if(world == null) {
            logger.warning("Could not teleport players to village: World not found");
            return;
        }

        Location origin = world.getSpawnLocation();
        Location nearest = world.locateNearestStructure(origin, StructureType.VILLAGE, 1000, false);
        if(nearest == null) {
            logger.warning("No village could be found within a range of 1000");
            return;
        }

        Location highestBlock = world.getHighestBlockAt(nearest).getLocation();
        Location spawnLoc = highestBlock.add(new Vector(0, 1, 0));

        world.setSpawnLocation(spawnLoc);
    }

    private static void performSeedReset(Runnable callback) {
        String resetMSG = MessageManager.getMessage("reset.seed_reset");

        Title title = Title.title(
                Component.text(""),
                Component.text(resetMSG)
        );
        Tools.getPlayers().showTitle(title);


        World overworld = Bukkit.getWorld(OVERWORLD);
        World nether = Bukkit.getWorld(NETHER);
        World end = Bukkit.getWorld(END);

        if(overworld == null || nether == null || end == null) {
            logger.warning(ChatColor.DARK_RED + "Could not recreate world by seed, not all worlds are available");
            callback.run();
            return;
        }

        WorldCreator[] creators = new WorldCreator[] {
                getCreator(overworld),
                getCreator(nether),
                getCreator(end)
        };

        scheduler.runTask(plugin, () -> {
            for (WorldCreator creator : creators) {
                creator.createWorld();
            }
            callback.run();
        });
    }

    private static WorldCreator getCreator(World world) {
        WorldCreator creator = new WorldCreator(String.format("%s_copy", world.getName()));
        creator.environment(world.getEnvironment());

        creator.seed(world.getSeed());

        return creator;
    }



    public static void resetWorld(Player issuer) {
        if(isResetting) {
            issuer.sendMessage("Already resetting");
            return;
        }
        isResetting = true;
        UtilTimer.reset();
        for (Challenge challenge : ToggleableList.instance.CHALLENGES) {
            challenge.onReset();
        }

        Runnable toRun = () -> {
            PositionManager.reset();
            Bukkit.spigot().restart();
        };

        if(seedReset)
            performSeedReset(toRun);
        else
            toRun.run();
    }

    public static boolean isVillagerSpawnEnabled() {
        return villagerSpawn;
    }

    public static boolean isSeedReset() {
        return seedReset;
    }

    public static void toggleVillagerSpawn() {
        villagerSpawn = !villagerSpawn;
    }

    public static void toggleSeedReset() {
        seedReset = !seedReset;
    }


    public static void save() {
        config.set("world.reset", isResetting);
        config.set("world.seed", seedReset);
        config.set("world.villager_spawn", villagerSpawn);
    }
}
