package me.sshcrack.sutils.interactable.challenges.module;

import me.sshcrack.sutils.interactable.challenges.base.ChunkDestroy;
import me.sshcrack.sutils.interactable.challenges.base.ChunkEffect;
import me.sshcrack.sutils.interactable.challenges.base.HurtSpawn;
import me.sshcrack.sutils.interactable.challenges.base.UltraHardcore;
import me.sshcrack.sutils.interactable.challenges.base.borderItem.BorderItem;
import me.sshcrack.sutils.interactable.challenges.base.dmgRedirect.DmgRedirect;
import org.bukkit.Bukkit;

public class ChallengeList {
    public ChallengeList() {
        Bukkit.getLogger().info("Enabling challenges...");
    }

    public static Challenge[] CHALLENGES = new Challenge[] {
            new UltraHardcore(),
            new DmgRedirect(),
            new HurtSpawn(),
            new BorderItem(),
            new ChunkDestroy(),
            new ChunkEffect()
    };

    public static void disable() {
        for (Challenge challenge : CHALLENGES) {
            if(challenge.isEnabled())
                challenge.onDisable();
            challenge.saveChallengeConfig();
        }
    }
}
