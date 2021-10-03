package me.sshcrack.sutils.ui.categories;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.items.Skulls;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TimerInventory implements InventoryProvider {
    public static SmartInventory INVENTORY = SmartInventory.builder()
            .id("timer-util-settings")
            .provider(new TimerInventory())
            .manager(Main.plugin.invManager)
            .size(3, 9)
            .title(MessageManager.getMessage("settings.titles.timer"))
            .build();

    public static String timerReset = MessageManager.getMessage("settings.timer.reset");
    public static String timerResume = MessageManager.getMessage("settings.timer.resume");
    public static String timerPause = MessageManager.getMessage("settings.timer.pause");

    @Override
    public void init(Player player, InventoryContents contents) {
        ClickableItem border = ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        contents.fillBorders(border);

        Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                contents.set(1, 5, getTimerActionItem(this));
            }
        };

        ClickableItem resetItem = getResetItem(player);
        ClickableItem timerAction = getTimerActionItem(updateRunnable);

        contents.set(1, 3, resetItem);
        contents.set(1, 5, timerAction);
        contents.set(2, 0, Skulls.CLICKABLE_BACK);
    }



    private ClickableItem getResetItem(Player player) {
        ItemStack resetItem = Skulls.TIMER_RESET;
        ItemMeta meta = resetItem.getItemMeta();

        meta.displayName(Component.text(timerReset));

        resetItem.setItemMeta(meta);
        return ClickableItem.of(resetItem, inv -> {
            player.closeInventory();
            UtilTimer.reset();
        });
    }

    private ClickableItem getTimerActionItem(Runnable runnable) {
        if(UtilTimer.isPaused())
            return getResumeItem(runnable);

        return getPauseItem(runnable);
    }

    private ClickableItem getPauseItem(Runnable runnable) {
        ItemStack pausedItem = Skulls.TIMER_PAUSE;
        ItemMeta meta = pausedItem.getItemMeta();

        meta.displayName(Component.text(timerPause));
        pausedItem.setItemMeta(meta);

        return ClickableItem.of(pausedItem, inv -> {
            UtilTimer.pause();
            runnable.run();
        });
    }

    private ClickableItem getResumeItem(Runnable runnable) {
        ItemStack resumedItem = Skulls.TIMER_RESUMED;
        ItemMeta meta = resumedItem.getItemMeta();

        meta.displayName(Component.text(timerResume));
        resumedItem.setItemMeta(meta);

        return ClickableItem.of(resumedItem, inv -> {
            UtilTimer.resume();
            runnable.run();
        });
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
