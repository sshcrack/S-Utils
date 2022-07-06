package me.sshcrack.sutils.ui.categories;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.events.damage.DamageLogger;
import me.sshcrack.sutils.events.death.SpeedrunListener;
import me.sshcrack.sutils.interactable.toggable.ToggleableListener;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.items.Skulls;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OptionsInventory implements InventoryProvider {
    ToggleableListener damageLogger = new DamageLogger();
    ToggleableListener listener = new SpeedrunListener();

    public static SmartInventory INVENTORY = SmartInventory.builder()
            .id("world-util-options")
            .provider(new OptionsInventory())
            .manager(Main.plugin.invManager)
            .size(3, 9)
            .title(MessageManager.getMessage("settings.titles.options"))
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        ClickableItem border = ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        contents.fillBorders(border);

        damageLogger.addClickable(contents, 1, 3);
        listener.addClickable(contents, 1, 5);

        contents.set(2, 0, Skulls.CLICKABLE_BACK);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
