package me.sshcrack.sutils.interactable.challenges.base.dmgRedirect;

import org.bukkit.inventory.ItemStack;

public class PreviousInfo {
    private final ItemStack[] extraInv;
    private final ItemStack[] armor;
    private final ItemStack[] inv;
    private final double health;

    public PreviousInfo(double health, ItemStack[] extraInv, ItemStack[] armor, ItemStack[] inv) {
        this.extraInv = extraInv;
        this.armor = armor;
        this.inv = inv;
        this.health = health;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack[] getExtraInv() {
        return extraInv;
    }

    public ItemStack[] getContents() {
        return inv;
    }

    public double getHealth() {
        return health;
    }
}
