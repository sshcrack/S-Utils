package me.sshcrack.sutils.interactable;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Interactable {
    public String getName();
    public List<String> getDescription();
    public ItemStack getItem();

}
