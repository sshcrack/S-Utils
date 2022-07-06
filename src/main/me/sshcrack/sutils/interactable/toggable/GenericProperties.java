package me.sshcrack.sutils.interactable.toggable;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class GenericProperties<T extends GenericProperties<?>> {
    public boolean itemEnchanted = false;
    public boolean clickToggle = false;
    public boolean statusDescription = false;
    public Supplier<Boolean> showTitles = () -> false;

    public @Nullable String enableTitle = null;
    public @Nullable String disableTitle = null;


    public @Nullable String enabled = null;
    public @Nullable String disabled = null;
    public @Nullable ItemStack item;

    public T showTitles() {
        showTitles = () -> true;

        return (T) this;
    }

    public T showTitles(Supplier<Boolean> showTitle) {
        this.showTitles = showTitle;

        return (T) this;
    }

    public T showTitles(String enable, String disable) {
        enableTitle = enable;
        disableTitle = disable;

        return (T) this;
    }

    public T showTitles(String enable, String disable, Supplier<Boolean> showTitle) {
        showTitles(enable, disable);
        this.showTitles = showTitle;

        return (T) this;
    }

    public T enchant() {
        itemEnchanted = true;

        return (T) this;
    }

    public T clickToggle() {
        this.clickToggle = true;

        return (T) this;
    }

    public T statusDescription() {
        statusDescription = true;

        return (T) this;
    }

    public T status(String enabled, String disabled) {
        this.enabled = enabled;
        this.disabled = disabled;

        return (T) this;
    }

    public T item(ItemStack item) {
        this.item = item;

        return (T) this;
    }
}