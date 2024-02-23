package me.sshcrack.sutils.interactable.challenges.base;

import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.Tools;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HurtPotion extends Challenge {
    Random random = new Random();
    HashMap<Player, List<PotionEffectType>> givenEffects = new HashMap<>();
    public final static double noDetectDamage = 1000;

    int maxAmplifier = 5;

    public HurtPotion() {
        super("hurt_potion",
                new Properties()
                    .item(new ItemStack(Material.BREWING_STAND))
                    .timerEnabled()
        );
    }


    @Override
    public void onUnregister() {
        givenEffects.forEach((Player p, List<PotionEffectType> effect) -> effect.forEach(p::removePotionEffect));
    }

    @EventHandler()
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        double damage = e.getFinalDamage();
        if(!(entity instanceof Player) || damage <= 0.25 || e.isCancelled() || damage == noDetectDamage)
            return;

        Player player = (Player) entity;
        if( e.getCause() == EntityDamageEvent.DamageCause.MAGIC || e.getCause() == EntityDamageEvent.DamageCause.WITHER ||e.getCause() == EntityDamageEvent.DamageCause.POISON)
                return;

        String playerName = player.getDisplayName();

        PotionEffectType[] types = PotionEffectType.values();
        int size = types.length;

        PotionEffectType type = types[random.nextInt(size)];
        int amplifier = random.nextInt(maxAmplifier) + 1; //Prevent 0
        int timeLength = 60 * 20 * 3;

        if(type == PotionEffectType.HARM) {
            amplifier = 1;
            timeLength = 1;
        }

        PotionEffect pot = new PotionEffect(type, timeLength, amplifier, true, false);

        player.addPotionEffect(pot);
        List<PotionEffectType> effList = new ArrayList<>();
        if(givenEffects.containsKey(player))
            effList = givenEffects.get(player);

        effList.add(pot.getType());
        givenEffects.put(player, effList);

        String potionName = pot.getType().getName().toLowerCase() + " " + amplifier;
        String giveMSG = MessageManager.getPrefix() + MessageManager.getMessageF("challenges.hurt_potion.effect", playerName, potionName);

        Tools.getPlayers().sendMessage(Component.text(giveMSG));
    }
}
