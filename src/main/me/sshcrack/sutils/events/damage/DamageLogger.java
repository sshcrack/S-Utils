package me.sshcrack.sutils.events.damage;

import me.sshcrack.sutils.interactable.toggable.Toggleable;
import me.sshcrack.sutils.interactable.toggable.ToggleableListener;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.Tools;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class DamageLogger extends ToggleableListener {
    public final static double noDetectDamage = 1000;

    public DamageLogger() {
        super("damage_logger", new Properties()
                .item(new ItemStack(Material.NAME_TAG))
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        double damage = e.getDamage();
        if(!(e.getEntity() instanceof Player) || e.isCancelled() || damage == 0 || damage == noDetectDamage)
            return;

        Player player = (Player) e.getEntity();
        if(UtilTimer.isPaused())
            return;

        EntityDamageEvent savedEvent = player.getLastDamageCause();
        if(savedEvent == null)
            return;

        EntityDamageEvent.DamageCause cause = savedEvent.getCause();
        String causeLoc = String.format("causes.%s", cause.toString().toUpperCase());

        String playerName = player.getDisplayName();
        String strCause = MessageManager.getMessage(causeLoc);

        String message = MessageManager.getPrefix() + MessageManager.getMessageF("causes.format",
                playerName,
                strCause,
                String.format("%.2f", damage)
        );

        Tools.getPlayers().sendMessage(Component.text(message));
    }
}
