package me.sshcrack.sutils.interactable.challenges.base;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class InterCircle extends Challenge {
    private final String cPrefix = "challenges.inter_circle.";
    private final double DIAMETER_RANGE = 5;
    private final float RESOLUTION = 1;
    private BukkitTask task;
    private final Main plugin;
    private boolean invinciblePhase = false;
    FileConfiguration config = Main.plugin.getConfig();
    /**
     * Key: Player on Overworld
     * Value: Player on Nether
     */
    private HashMap<String, String> mappings = new HashMap<>();

    public InterCircle() {
        super(
                "inter_circle",
                new Properties()
                        .item(new ItemStack(Material.POWDER_SNOW_BUCKET))
                        .timerEnabled()
        );

        this.plugin = Main.plugin;
    }

    private void onTick() {
        this.mappings.forEach((k, v) -> {
            Player worldPlayer = Bukkit.getPlayer(UUID.fromString(k));
            Player netherPlayer = Bukkit.getPlayer(UUID.fromString(v));

            if(worldPlayer == null || netherPlayer == null) {
                Bukkit.getLogger().info("Some player null");
                return;
            }

            if(!netherPlayer.getWorld().getName().contains("nether")) {
                return;
            }

            if(!worldPlayer.getWorld().getName().equalsIgnoreCase("world")) {
                return;
            }

            Location overworld = toOverworldPos(netherPlayer.getLocation(), worldPlayer);
            Location nether = toNetherPos(worldPlayer.getLocation(), netherPlayer);

            spawnCircle(overworld);
            spawnCircle(nether);
        });
    }

    public boolean shouldActivate() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            if(player.getWorld().getName().contains("end"))
                return false;
        }

        return Bukkit.getOnlinePlayers().size() % 2 == 0 && !invinciblePhase;
    }

    public void rollPlayers() {
        List<? extends Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        HashMap<String, String> playerList = new HashMap<>();
        if (!shouldActivate()) {
            this.mappings.clear();
            Bukkit.getServer().broadcast(Component.text("Cannot have odd number of players").color(NamedTextColor.RED));
            return;
        }

        int size = (int) Math.floor((double)players.size() / 2);

        Bukkit.getLogger().info("Rerolling");
        Collections.shuffle(players);
        for (int i = 0; i < size; i += 2) {
            UUID source = players.get(i).getUniqueId();
            UUID dest = players.get(i +1).getUniqueId();

            playerList.put(source.toString(), dest.toString());
        }

        this.mappings = playerList;

        this.mappings.forEach((k,v)->{
            UUID kU = UUID.fromString(k);
            UUID vU = UUID.fromString(v);

            Player kP = Bukkit.getPlayer(kU);
            Player vP = Bukkit.getPlayer(vU);

            if(kP == null || vP == null)
                return;

            Location overworldLoc = toOverworldPos(vP.getLocation(), kP);
            Location netherLoc = toNetherPos(kP.getLocation(), vP);

            kP.teleport(overworldLoc);
            vP.teleport(netherLoc);
        });

        save();
        invinciblePhase = true;
        Bukkit.getScheduler().runTaskLater(Main.plugin, () -> invinciblePhase = false, 20 * 2);
    }

    public Location toNetherPos(Location worldPlayerLoc, Player netherPlayer) {
        Location nether = worldToNether(worldPlayerLoc);
        nether.setY(netherPlayer.getLocation().getY());

        return nether;
    }

    public Location toOverworldPos(Location netherPlayerLoc, Player overworldPlayer) {
        Location overworld = netherToWorld(netherPlayerLoc);
        overworld.setY(overworldPlayer.getLocation().getY());

        return overworld;
    }

    public void loadConfigLocal() {
        Object rawMats = config.get(cPrefix +"mappings");
        if(!(rawMats instanceof HashMap<?,?> map))
            return;


        this.mappings = new HashMap<>();
        map.forEach((k, v) -> {
            if(!(k instanceof String) || !(v instanceof String))
                return;

            this.mappings.put((String) k, (String) v);
        });
    }

    @Override
    public void onDisable() {
        save();
    }


    public void save() {
        config.set(cPrefix +"mappings", this.mappings);
    }

    @Override
    public void onReset() {
        super.onReset();
        config.set(cPrefix + "mappings", new HashMap<>());
    }

    @Override
    public void onRegister() {
        loadConfigLocal();
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
    public void onPlayerJoin(PlayerJoinEvent e) {
        rollPlayers();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        rollPlayers();
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        if(!e.message().toString().contains("reroll"))
            return;

        rollPlayers();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if(!shouldActivate())
            return;

        Player eventPlayer = e.getPlayer();
        Player isNetherPlayer = getNetherPlayer(eventPlayer.getUniqueId());
        Player isOverworldPlayer = getOverworldPlayer(eventPlayer.getUniqueId());

        Player overworldPlayer = eventPlayer;
        Player netherPlayer = isNetherPlayer;

        if(isNetherPlayer == null && isOverworldPlayer == null) {
            Bukkit.getLogger().info("Rerolling");
            rollPlayers();
            isNetherPlayer = getNetherPlayer(eventPlayer.getUniqueId());
            isOverworldPlayer = getOverworldPlayer(eventPlayer.getUniqueId());
        }

        if(isOverworldPlayer != null) {
            overworldPlayer = isOverworldPlayer;
            netherPlayer = eventPlayer;
        }

        if(netherPlayer == null) {
            Bukkit.getLogger().info("Target is null");
            return;
        }


        String playerUuid = eventPlayer.getUniqueId().toString();
        if(netherPlayer.getUniqueId().toString().equals(playerUuid)) {
            Location loc = toNetherPos(overworldPlayer.getLocation(), netherPlayer);
            e.setRespawnLocation(loc);
        }

        if(overworldPlayer.getUniqueId().toString().equals(playerUuid)) {
            e.setRespawnLocation(toOverworldPos(netherPlayer.getLocation(), overworldPlayer));
        }
    }

    @EventHandler
    public void onAfterRespawnEvent(PlayerPostRespawnEvent e) {
        if(!shouldActivate())
            return;

        Player eventPlayer = e.getPlayer();
        Player isNetherPlayer = getNetherPlayer(eventPlayer.getUniqueId());
        Player isOverworldPlayer = getOverworldPlayer(eventPlayer.getUniqueId());

        Player overworldPlayer = eventPlayer;
        Player netherPlayer = isNetherPlayer;

        if(isNetherPlayer == null && isOverworldPlayer == null) {
            Bukkit.getLogger().info("Rerolling");
            rollPlayers();
            isNetherPlayer = getNetherPlayer(eventPlayer.getUniqueId());
            isOverworldPlayer = getOverworldPlayer(eventPlayer.getUniqueId());
        }

        if(isOverworldPlayer != null) {
            overworldPlayer = isOverworldPlayer;
            netherPlayer = eventPlayer;
        }

        if(netherPlayer == null) {
            Bukkit.getLogger().info("Target is null");
            return;
        }


        String playerUuid = eventPlayer.getUniqueId().toString();
        if(netherPlayer.getUniqueId().toString().equals(playerUuid)) {
            netherPlayer.teleport(toNetherPos(overworldPlayer.getLocation(), netherPlayer));

            invinciblePhase = true;
            Bukkit.getScheduler().runTaskLater(Main.plugin, () -> invinciblePhase = false, 20 * 2);
        }

        if(overworldPlayer.getUniqueId().toString().equals(playerUuid)) {
            Location loc = toOverworldPos(netherPlayer.getLocation(), overworldPlayer);
            Block b = overworldPlayer.getWorld().getHighestBlockAt(loc);
            loc.setY(b.getY() +1);

            overworldPlayer.teleport(loc);
            invinciblePhase = true;
            Bukkit.getScheduler().runTaskLater(Main.plugin, () -> invinciblePhase = false, 20 * 2);
        }
    }

    @EventHandler
    public void onPlayerPortalEnter(EntityPortalEnterEvent e) {
        if(!shouldActivate())
            return;

        Entity entity = e.getEntity();
        if(!(entity instanceof Player p))
            return;

        Location loc = e.getLocation();

        Player target = getNetherPlayer(p.getUniqueId());
        if(target == null || !loc.getWorld().getName().contains("end"))
            return;

        target.teleport(p);

        invinciblePhase = true;
        Bukkit.getScheduler().runTaskLater(Main.plugin, () -> invinciblePhase = false, 20 * 2);
    }

    @EventHandler
    public void onPlayerPortalEvent(EntityPortalEvent e) {
        if(!shouldActivate())
            return;

        Entity entity = e.getEntity();
        if(!(entity instanceof Player))
            return;

        if(e.getPortalType() != PortalType.NETHER)
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(!shouldActivate())
            return;

        Player eventPlayer = e.getPlayer();
        Player isNetherPlayer = getNetherPlayer(eventPlayer.getUniqueId());
        Player isOverworldPlayer = getOverworldPlayer(eventPlayer.getUniqueId());

        Player overworldPlayer = eventPlayer;
        Player netherPlayer = isNetherPlayer;

        if(isNetherPlayer == null && isOverworldPlayer == null) {
            Bukkit.getLogger().info("Rerolling");
            rollPlayers();
            isNetherPlayer = getNetherPlayer(eventPlayer.getUniqueId());
            isOverworldPlayer = getOverworldPlayer(eventPlayer.getUniqueId());
        }

        if(isOverworldPlayer != null) {
            overworldPlayer = isOverworldPlayer;
            netherPlayer = eventPlayer;
        }

        if(netherPlayer == null) {
            Bukkit.getLogger().info("Target is null");
            return;
        }

        boolean shouldTeleportRespawn = !netherPlayer.getWorld().getName().contains("nether") && !overworldPlayer.getWorld().getName().contains("end");
        if(shouldTeleportRespawn) {
            World nether = Bukkit.getWorld("world_nether");
            Location pLoc = overworldPlayer.getLocation();

            Location l = pLoc.clone().multiply(1f / RESOLUTION).clone();
            Location spawnLoc = new Location(nether, l.getX(), l.getY(), l.getZ());
            int startX = -5;
            int startZ = -5;

            int increment = 5;
            boolean hasFound = false;
            Bukkit.getLogger().info("Finding location...");
            while (!hasFound) {
                for(int x = startX; x < startX + 5 && !hasFound; x++) {
                    for(int z = startZ; z < startZ + 5 && !hasFound; z++) {
                        for(int y = 5; y < 118; y++) {
                            Location relLoc = spawnLoc.clone().add(x, 0, z);
                            relLoc.setY(y);

                            Block block = relLoc.getBlock();
                            Block above = relLoc.clone().add(0,1,0).getBlock();
                            Block aboveAbove = above.getLocation().clone().add(0, 1, 0).getBlock();

                            if(block.isSolid() && !block.isLiquid() && above.getType() == Material.AIR && aboveAbove.getType() == Material.AIR) {
                                spawnLoc = above.getLocation().toCenterLocation();
                                hasFound = true;
                                break;
                            }
                        }
                    }
                    startZ += increment;
                }
                startX += increment;
            }



            Location overworldLoc = toOverworldPos(spawnLoc, overworldPlayer);
            netherPlayer.teleport(spawnLoc);
            overworldPlayer.teleport(overworldLoc);

            invinciblePhase = true;
            Bukkit.getScheduler().runTaskLater(Main.plugin, () -> invinciblePhase = false, 20 * 2);
        }

        Location overworld = toOverworldPos(netherPlayer.getLocation(), overworldPlayer);
        Location nether = toNetherPos(overworldPlayer.getLocation(), netherPlayer);

        double distanceOverworld = overworld.distanceSquared(overworldPlayer.getLocation());
        double distanceNether = nether.distanceSquared(netherPlayer.getLocation());

        if(!netherPlayer.getWorld().getName().contains("nether"))
            return;

        if(!overworld.getWorld().getName().equalsIgnoreCase("world"))
            return;

        double radiusSrd = Math.pow(DIAMETER_RANGE, 2);
        if(distanceOverworld > radiusSrd && !netherPlayer.isDead()) {
            overworldPlayer.damage(1000000);
        }

        if(distanceNether > radiusSrd && !overworldPlayer.isDead()) {
            netherPlayer.damage(1000000);
        }
    }


    @Nullable
    public Player getOverworldPlayer(UUID netherPlayer) {
        AtomicReference<Player> source = new AtomicReference<>();

        mappings.forEach((k, v) -> {
            if(v == null)
                return;

            if(v.equals(netherPlayer.toString()))
                source.set(Bukkit.getPlayer(UUID.fromString(k)));
        });

        return source.get();
    }

    @Nullable
    public Player getNetherPlayer(UUID overworldPlayer) {
        String netherPlayer = this.mappings.get(overworldPlayer.toString());
        if(netherPlayer == null)
            return null;

        return Bukkit.getPlayer(UUID.fromString(netherPlayer));
    }

    public boolean isOverworldPlayer(UUID player) {
        return this.mappings.containsKey(player.toString());
    }

    public boolean isNetherPlayer(UUID player) {
        return this.mappings.containsValue(player.toString());
    }

    public void spawnCircle(Location center) {
        double resolution = 2;

        //center.subtract(.5 * DIAMETER_RANGE, 0, .5 * DIAMETER_RANGE);


        double fullAngle = 360;
        double single = 1 / resolution;
        for (double i = 0; i < fullAngle; i += single) {
            double xAdd = (DIAMETER_RANGE * Math.cos(i * (Math.PI / 180F)));
            double yAdd = (DIAMETER_RANGE * Math.sin(i * (Math.PI / 180F)));

            Location circle = center.clone();

            circle.add(xAdd, 0, yAdd);
            new ParticleBuilder(Particle.REDSTONE)
                    .location(circle)
                    .color(Color.RED)
                    .allPlayers()
                    .spawn();
        }
    }



    public Location worldToNether(Location loc) {
        World nether = Bukkit.getWorld("world_nether");

        Location l = loc.clone().multiply(1f / RESOLUTION);
        return new Location(nether, l.getX(), loc.getY(), l.getZ());
    }

    public Location netherToWorld(Location loc) {
        World overworld = Bukkit.getWorld("world");

        Location l = loc.clone().multiply(RESOLUTION);
        return new Location(overworld, l.getX(), loc.getY(), l.getZ());
    }
}
