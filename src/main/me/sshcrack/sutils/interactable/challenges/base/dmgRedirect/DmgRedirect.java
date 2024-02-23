package me.sshcrack.sutils.interactable.challenges.base.dmgRedirect;

import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import me.sshcrack.sutils.events.damage.DamageLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class DmgRedirect extends Challenge {
    private HashMap<UUID, UUID> mappings = new HashMap<>();
    private final HashMap<UUID, EntityDamageEvent> ignore = new HashMap<>();

    //This is a hashmap to determine weither the death event of the player should be canceled or not. If the player exists there,
    // The death is getting canceled and the death message is being put in deathMSGs with the uuid of the target
    private final HashMap<UUID, EntityDamageEvent> gettingDeathMSG = new HashMap<>();

    //The health to set the source player to again
    private final HashMap<UUID, PreviousInfo> previousInfo = new HashMap<>();

    private final HashMap<UUID, String> deathMSGs = new HashMap<>();

    public DmgRedirect() {
        super(
                "damage_redirect",
                new Properties()
                        .item(new ItemStack(Material.REDSTONE))
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;

        Player player = (Player) e.getEntity();
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();

        double damage = e.getDamage();

        UUID uuid = player.getUniqueId();

        if(ignore.containsKey(uuid) && ignore.get(uuid) == lastDamageCause) {
            ignore.remove(uuid);
            return;
        }

        Player target = getTarget(uuid);
        UUID targetUUID = target.getUniqueId();

        double targetHealth = target.getHealth();

        // Getting death message here
        if(damage >= targetHealth) {
            player.setLastDamageCause(e);
            ignore.put(uuid, e);

            e.setCancelled(false);
            e.setDamage(DamageLogger.noDetectDamage);

            gettingDeathMSG.put(uuid, e);

            PlayerInventory inv = player.getInventory();
            double health = player.getHealth();

            ItemStack[] armor = inv.getArmorContents();
            ItemStack[] extr = inv.getExtraContents();
            ItemStack[] contents = inv.getContents();
            previousInfo.put(uuid, new PreviousInfo(health, extr, armor, contents));
            return;
        }

        ignore.put(targetUUID, e);
        target.setLastDamageCause(e);
        target.damage(damage);
        e.setDamage(0);
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
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        UUID uuid = player.getUniqueId();

        String storedDeathMSG = deathMSGs.get(uuid);
        if (storedDeathMSG != null) {
            e.deathMessage(Component.text(storedDeathMSG));
            deathMSGs.remove(uuid);
            return;
        }

        EntityDamageEvent event = player.getLastDamageCause();
        EntityDamageEvent storedEvent = gettingDeathMSG.get(uuid);

        boolean hasHealth = previousInfo.containsKey(uuid);
        if(!hasHealth)
            return;

        PreviousInfo info = previousInfo.get(uuid);
        double health = info.getHealth();

        ItemStack[] armor = info.getArmor();
        ItemStack[] extra = info.getExtraInv();
        ItemStack[] contents = info.getContents();

        PlayerInventory inv = player.getInventory();
        inv.setExtraContents(extra);
        inv.setArmorContents(armor);
        inv.setContents(contents);

        player.setHealth(health);
        if(storedEvent != event) {
            gettingDeathMSG.remove(uuid);
            return;
        }

        Player target = getTarget(uuid);
        String deathMSG = e.getDeathMessage();
        if(deathMSG == null) {
            gettingDeathMSG.remove(uuid);
            return;
        }

        String targetName = target.getDisplayName();
        String sourceName = player.getDisplayName();
        String swapped = deathMSG
                .replace(sourceName, "source")
                .replace(targetName, "target")
                .replace("source", targetName)
                .replace("target", sourceName);

        deathMSGs.put(target.getUniqueId(), swapped);
        gettingDeathMSG.remove(uuid);
        previousInfo.remove(uuid);
        ignore.remove(uuid);

        target.setHealth(0);

        player.setHealth(health);
        e.setCancelled(true);
    }


    public void rollPlayers() {
        List<? extends Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        HashMap<UUID, UUID> playerList = new HashMap<>();
        if (players.size() == 1) {
            this.mappings.clear();
            this.disable();
            Bukkit.getServer().broadcast(Component.text("Cannot have odd number of players").color(NamedTextColor.RED));
            return;
        }

        Random random = new Random();

        int size = players.size();
        for (Player key : players) {
            Player value = key;

            //You cant have so much unluck alright its illegal
            for (int i = 0; i < size * 3; i++) {
                if (value.getUniqueId().compareTo(key.getUniqueId()) != 0)
                    break;

                int randomIndex = random.nextInt(size);
                value = players.get(randomIndex);
            }

            playerList.put(
                    key.getUniqueId(),
                    value.getUniqueId()
            );
        }

        this.mappings = playerList;
    }

    public Player getTarget(UUID uuid) {
        Player target = null;
        while(target == null) {
            UUID targetUUID = mappings.get(uuid);
            if(targetUUID == null)
                rollPlayers();
            else
                target = Bukkit.getPlayer(targetUUID);
        }

        return target;
    }
}
