package me.sshcrack.sutils.ui.categories;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.TwoConsumer;
import me.sshcrack.sutils.tools.items.ReactiveItems;
import me.sshcrack.sutils.tools.items.Skulls;
import me.sshcrack.sutils.tools.world.WorldManager;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class WorldOptionsInventory implements InventoryProvider {
    public static SmartInventory INVENTORY = SmartInventory.builder()
            .id("timer-util-options")
            .provider(new WorldOptionsInventory())
            .manager(Main.plugin.invManager)
            .size(3, 9)
            .title(ChatColor.GOLD + "Options")
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        ClickableItem border = ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        contents.fillBorders(border);

        ClickableItem resetWorldItem = getResetWorldItem();

        contents.set(1, 2, getVillagerSpawnItem(contents));
        contents.set(1, 4, resetWorldItem);
        contents.set(1, 6, getSeedResetItem(contents));

        contents.set(2, 0, Skulls.CLICKABLE_BACK);
    }

    private ClickableItem getSeedResetItem(InventoryContents contents) {
        ItemStack stack = new ItemStack(Material.WHEAT_SEEDS);

        String displayName = "settings.options.seed_reset";
        Supplier<Boolean> isEnabled = WorldManager::isSeedReset;
        TwoConsumer<@Nullable InventoryClickEvent, Supplier<ClickableItem>> onChange = (inv, getClickable) -> {
            WorldManager.toggleSeedReset();

            contents.set(1, 6, getClickable.get());
        };

        return ReactiveItems.getItem(stack, displayName, isEnabled, onChange);
    }

    private ClickableItem getVillagerSpawnItem(InventoryContents contents) {
        ItemStack enabledItem = Skulls.VILLAGER_ENABLED;
        ItemStack disabledItem = Skulls.VILLAGER_DISABLED;

        String displayName = "settings.options.villager_spawn";
        Supplier<Boolean> isEnabled = WorldManager::isVillagerSpawnEnabled;
        TwoConsumer<@Nullable InventoryClickEvent, Supplier<ClickableItem>> onChange = (inv, getClickable) -> {
            WorldManager.toggleVillagerSpawn();

            contents.set(1, 2, getClickable.get());
        };

        return ReactiveItems.getItem(enabledItem, disabledItem, displayName, isEnabled, onChange);
    }


    private ClickableItem getResetWorldItem() {
        ItemStack resetItem = Skulls.RESET_WORLD;
        ItemMeta meta = resetItem.getItemMeta();

        String resetWorldText = MessageManager.getMessage("settings.options.reset_world");
        meta.displayName(Component.text(resetWorldText));

        resetItem.setItemMeta(meta);
        return ClickableItem.of(resetItem, inv -> {
            HumanEntity entity = inv.getWhoClicked();

            if(!(entity instanceof Player))
                return;

            Player player = (Player) entity;
            WorldManager.resetWorld(player);
        });
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
