package me.sshcrack.sutils.interactable.challenges.module;

import me.sshcrack.sutils.events.damage.DamageLogger;
import me.sshcrack.sutils.events.death.SpeedrunListener;
import me.sshcrack.sutils.interactable.challenges.base.*;
import me.sshcrack.sutils.interactable.challenges.base.borderItem.BorderItem;
import me.sshcrack.sutils.interactable.challenges.base.dmgRedirect.DmgRedirect;
import me.sshcrack.sutils.interactable.challenges.base.force_item.ForceItem;
import me.sshcrack.sutils.interactable.challenges.base.randomRecipes.RandomRecipes;
import me.sshcrack.sutils.interactable.toggable.Toggleable;
import org.bukkit.Bukkit;

public class ToggleableList {
    public static ToggleableList instance;

    public ToggleableList() {
        Bukkit.getLogger().info("Enabling challenges...");
        instance = this;
    }

    public Challenge[] CHALLENGES = new Challenge[] {
            new UltraHardcore(),
            new DmgRedirect(),
            new HurtSpawn(),
            new BorderItem(),
            new ChunkDestroy(),
            new ChunkEffect(),
            new HalfHeart(),
            new HurtPotion(),
            new TNTRun(),
            new InterCircle(),
            new ForceItem()
            //new RandomRecipes(),
    };

    public Toggleable[] TOGGLEABLES = new Toggleable[] {
            new SpeedrunListener(),
            new DamageLogger(),
    };

    public void disable() {
        for (Challenge challenge : CHALLENGES) {
            if(challenge.isEnabled())
                challenge.onDisable();
            challenge.saveConfig();
        }

        for (Toggleable toggleable : TOGGLEABLES) {
            if(toggleable.isEnabled())
                toggleable.onDisable();
            toggleable.saveConfig();
        }
    }
}
