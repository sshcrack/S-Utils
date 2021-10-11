package me.sshcrack.sutils.tools.items;

import fr.minuskube.inv.ClickableItem;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.TwoConsumer;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.ItemTag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReactiveItems {
    private static final String enabledText = MessageManager.getMessage("settings.description.enabled");
    private static final String disabledText = MessageManager.getMessage("settings.description.disabled");

    public static ClickableItem getItem(ItemStack stack, String displayNameLoc, Supplier<Boolean> isEnabled, TwoConsumer<@Nullable InventoryClickEvent, Supplier<ClickableItem>> onUpdate) {
        return ReactiveItems.getItem(stack, stack, displayNameLoc, isEnabled, onUpdate);
    }

    public static ClickableItem getItem(ItemStack enabledItem, ItemStack disabledItem, String displayNameLoc, Supplier<Boolean> isEnabled, TwoConsumer<@Nullable InventoryClickEvent, Supplier<ClickableItem>> onUpdate) {
        String displayName = MessageManager.getMessage(displayNameLoc);

        Function<@Nullable InventoryClickEvent, ClickableItem> updateItem = new Function<>() {
            @Override
            public ClickableItem apply(@Nullable InventoryClickEvent clickEvent) {
                Supplier<ClickableItem> getClickable = () -> {
                    boolean enabled = isEnabled.get();

                    ItemStack toUse = enabled ? getEnabled(enabledItem, displayName) : getDisabled(disabledItem, displayName);

                    return ClickableItem.of(toUse, this::apply);
                };

                if(clickEvent != null) {
                    onUpdate.accept(clickEvent, getClickable);
                    return null;
                }

                return getClickable.get();
            }
        };

        return updateItem.apply(null);
    }

    private static ItemStack getEnabled(ItemStack stack, String displayName) {
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(displayName));

        List<Component> lores = Collections.singletonList(Component.text(enabledText));
        meta.lore(lores);

        stack.setItemMeta(meta);

        stack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
        stack.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        return stack;
    }

    private static ItemStack getDisabled(ItemStack stack, String displayName) {
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(displayName));

        List<Component> lores = Collections.singletonList(Component.text(disabledText));
        meta.lore(lores);

        stack.setItemMeta(meta);

        return stack;
    }
}
