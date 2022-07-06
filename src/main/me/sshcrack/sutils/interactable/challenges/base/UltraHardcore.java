package me.sshcrack.sutils.interactable.challenges.base;

import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import me.sshcrack.sutils.interactable.toggable.ToggleableListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;

public class UltraHardcore extends Challenge {

    public UltraHardcore() {
        super(
                "ultra_hardcore",
                new Properties()
                        .alwaysEnabled()
                        .item(new ItemStack(Material.GOLDEN_APPLE))
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHealthRegenerated(EntityRegainHealthEvent e) {
        if(!(e.getEntity() instanceof Player))
            return;

        EntityRegainHealthEvent.RegainReason reason = e.getRegainReason();
        boolean shouldCancel = reason != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN;
        if(shouldCancel) {
            e.setCancelled(true);
            e.setAmount(0);
        }
    }
}
