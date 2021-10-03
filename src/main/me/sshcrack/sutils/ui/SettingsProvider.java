package me.sshcrack.sutils.ui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.items.Skulls;
import me.sshcrack.sutils.ui.categories.ChallengeInventory;
import me.sshcrack.sutils.ui.categories.WorldOptionsInventory;
import me.sshcrack.sutils.ui.categories.TimerInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class SettingsProvider implements InventoryProvider {
    public static SmartInventory INVENTORY = SmartInventory.builder()
            .id("main-util-settings")
            .provider(new SettingsProvider())
            .manager(Main.plugin.invManager)
            .size(3, 9)
            .title(MessageManager.getMessage("settings.titles.main"))
            .build();


    @Override
    public void init(Player player, InventoryContents contents) {
        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        contents.fillBorders(ClickableItem.empty(border));

        contents.set(1, 2, getChallengeItem());
        contents.set(1, 4, getTimerItem());
        contents.set(1, 6, getWorldOptions());
    }

    public ClickableItem getChallengeItem() {
        ItemStack item = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta meta = item.getItemMeta();

        String name = MessageManager.getMessage("settings.items.challenges.name");
        String desc = MessageManager.getMessage("settings.items.challenges.description");

        List<Component> lores = Collections.singletonList(Component.text(desc));

        meta.lore(lores);
        meta.displayName(Component.text(name));
        item.setItemMeta(meta);
        return ClickableItem.of(item, inv -> {
            HumanEntity entity = inv.getWhoClicked();
            if(!(entity instanceof Player))
                return;

            ChallengeInventory.INVENTORY.open((Player) entity);
        });
    }

    public ClickableItem getTimerItem() {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();

        String name = MessageManager.getMessage("settings.items.timer.name");
        String desc = MessageManager.getMessage("settings.items.timer.description");

        List<Component> lores = Collections.singletonList(Component.text(desc));

        meta.lore(lores);
        meta.displayName(Component.text(name));
        item.setItemMeta(meta);
        return ClickableItem.of(item, inv -> {
            HumanEntity entity = inv.getWhoClicked();
            if(!(entity instanceof Player))
                return;

            TimerInventory.INVENTORY.open((Player) entity);
        });
    }

    public  ClickableItem getWorldOptions() {
        ItemStack item = Skulls.WORLD_OPTIONS;
        ItemMeta meta = item.getItemMeta();

        String name = MessageManager.getMessage("settings.items.world_options.name");
        String desc = MessageManager.getMessage("settings.items.world_options.description");

        List<Component> lores = Collections.singletonList(Component.text(desc));

        meta.lore(lores);
        meta.displayName(Component.text(name));
        item.setItemMeta(meta);
        return ClickableItem.of(item, inv -> {
            HumanEntity entity = inv.getWhoClicked();
            if(!(entity instanceof Player))
                return;

            WorldOptionsInventory.INVENTORY.open((Player) entity);
        });
    }

    @Override
    public void update(Player player, InventoryContents contents) {}
}
