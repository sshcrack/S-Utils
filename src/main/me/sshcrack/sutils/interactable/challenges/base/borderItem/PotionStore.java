package me.sshcrack.sutils.interactable.challenges.base.borderItem;

import org.bukkit.Material;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class PotionStore implements Serializable {
    private final Material material;
    private final PotionType potion;

    public PotionStore(Material mat, PotionType pot) {
        this.material = mat;
        this.potion = pot;
    }

    public Material getMaterial() {
        return material;
    }

    public PotionType getPotion() {
        return potion;
    }

    @Override
    public String toString() {
        return material.toString() + "," + potion.toString();
    }

    public static @Nullable PotionStore fromString(String str) {
        String[] split = str.split(",");
        if(split.length != 2)
            return null;

        Material mat = Material.valueOf(split[0]);
        PotionType type = PotionType.valueOf(split[0]);

        return new PotionStore(mat, type);
    }
}
