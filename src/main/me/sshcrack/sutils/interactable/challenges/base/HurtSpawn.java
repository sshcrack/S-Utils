package me.sshcrack.sutils.interactable.challenges.base;

import dev.dbassett.skullcreator.SkullCreator;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.Tools;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HurtSpawn extends Challenge {
    private static final String itemBase = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGM5YjlkY2JhMmE3ODA5ZWQ3Y2E1MmU2MzU3M2Q4NmUzMWY4OTU2NTdkOTk2MTU1NWVkMTgzMDMzZmIxZmU5OSJ9fX0=";
    private static final ItemStack skull = SkullCreator.itemFromBase64(itemBase);
    private static final Random random = new Random();
    public final static double noDetectDamage = 1000;

    public HurtSpawn() {
        super("hurt_spawn", new Properties().item(skull)
                .timerEnabled());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        double damage = e.getFinalDamage();
        if(!(entity instanceof Player player) || damage <= 0.25 || e.isCancelled() || damage == noDetectDamage)
            return;

        Location loc = player.getLocation();
        World world = loc.getWorld();

        player.sendMessage(String.format("Damage: %s", damage));

        Location around = Tools.getAirAround(loc, 2, 5);
        if(around == null) {
            System.out.println("No blocks around");
            return;
        }

        EntityType[] entityTypes = EntityType.values();
        Class<? extends Entity> randClass = null;
        for(int i = 0; i < entityTypes.length; i++) {
            int randomEntity = random.nextInt(entityTypes.length);

            EntityType type = entityTypes[randomEntity];
            Class<? extends Entity> toTest = type.getEntityClass();
            if(toTest == null)
                continue;

            if(!Tools.implementsClass(toTest, LivingEntity.class))
                continue;

            String playerPackage = Player.class.getCanonicalName();
            String name = toTest.getCanonicalName();
            if(Tools.implementsClass(toTest, Player.class) || name.equals(playerPackage))
                return;

            randClass = toTest;
            break;
        }

        if(randClass == null)
            return;

        world.spawn(around, randClass);
        String playerName = player.getDisplayName();

        String className = randClass.getSimpleName();
        List<String> words = findWordsInMixedCase(className);

        String readable = String.join(" ", words);
        String spawnMSG = MessageManager.getPrefix() + MessageManager.getMessageF("challenges.hurt_spawn.spawn", playerName, readable);

        Tools.getPlayers().sendMessage(Component.text(spawnMSG));
    }

    public List<String> findWordsInMixedCase(String text) {
        Pattern WORD_FINDER = Pattern.compile("(([A-Z]?[a-z]+)|([A-Z]))");

        Matcher matcher = WORD_FINDER.matcher(text);
        List<String> words = new ArrayList<>();
        while (matcher.find()) {
            words.add(matcher.group(0));
        }
        return words;
    }
}
