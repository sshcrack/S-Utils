package me.sshcrack.sutils.tools.items;

import dev.dbassett.skullcreator.SkullCreator;
import fr.minuskube.inv.ClickableItem;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.ui.SettingsProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Skulls {
    private final static  String worldBase = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkYWExZTNlZDk0ZmYzZTMzZTFkNGM2ZTQzZjAyNGM0N2Q3OGE1N2JhNGQzOGU3NWU3YzkyNjQxMDYifX19";

    public final static String previousPageBase= "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTJmMDQyNWQ2NGZkYzg5OTI5MjhkNjA4MTA5ODEwYzEyNTFmZTI0M2Q2MGQxNzViZWQ0MjdjNjUxY2JlIn19fQ==";
    public final static String nextPageBase = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ4NjVhYWUyNzQ2YTliOGU5YTRmZTYyOWZiMDhkMThkMGE5MjUxZTVjY2JlNWZhNzA1MWY1M2VhYjliOTQifX19";
    private final static String backPageBase = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZlMTQ1ZTcxMjk1YmNjMDQ4OGU5YmI3ZTZkNjg5NWI3Zjk2OWEzYjViYjdlYjM0YTUyZTkzMmJjODRkZjViIn19fQ==";

    private final static String timerResetBase = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTlhNWExZTY5YjRmODEwNTYyNTc1MmJjZWUyNTM0MDY2NGIwODlmYTFiMmY1MjdmYTkxNDNkOTA2NmE3YWFkMiJ9fX0=";
    private final static String timerPausedBase = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWRjNTYzNTVmMTFjZTUzZTE0ZDM3NGVjZjJhMGIyNTUzMDFiNzM0ZDk5YzY3NDI0MGFmYWNjNzNlMjE0NWMifX19";
    private final static String timerResumedBase = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU0ODRmNGI2MzY3Yjk1YmIxNjI4ODM5OGYxYzhkZDZjNjFkZTk4OGYzYTgzNTZkNGMzYWU3M2VhMzhhNDIifX19";

    private final static String resetWorldBase = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE4OTc4Y2NiZjU3NmY0NDZlMjFjNTFkM2U4MGZjN2Y4NTY2ZWI3MjY1Y2M0M2M0YWQ3MWNmYjc4YzE2NTI1NyJ9fX0=";
    private final static String villagerEnableBase = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFiODMwZWI0MDgyYWNlYzgzNmJjODM1ZTQwYTExMjgyYmI1MTE5MzMxNWY5MTE4NDMzN2U4ZDM1NTU1ODMifX19";
    private final static String villagerDisableBase = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2E0MTA2MWVkODU0MTUxZmRkYTEzZjY4M2RiZTI5OTdhMjczNWNhYTVhMmE1OWE1Njk5MzE0NjAyYTE0ZjkifX19";

    public final static ItemStack WORLD_OPTIONS = SkullCreator.itemFromBase64(worldBase);

    public final static ItemStack NEXT_PAGE = getNextPageItem();
    public final static ItemStack PREVIOUS_PAGE = getPreviousPageItem();
    public final static ItemStack BACK = SkullCreator.itemFromBase64(backPageBase);

    public final static ItemStack TIMER_RESET = SkullCreator.itemFromBase64(timerResetBase);
    public final static ItemStack TIMER_PAUSE = SkullCreator.itemFromBase64(timerPausedBase);
    public final static ItemStack TIMER_RESUMED = SkullCreator.itemFromBase64(timerResumedBase);

    public final static ItemStack RESET_WORLD = SkullCreator.itemFromBase64(resetWorldBase);
    public final static ItemStack VILLAGER_DISABLED = SkullCreator.itemFromBase64(villagerEnableBase);
    public final static ItemStack VILLAGER_ENABLED = SkullCreator.itemFromBase64(villagerDisableBase);

    public final static ClickableItem CLICKABLE_BACK = ClickableItem.of(Skulls.getBackItem(), (inv) -> {
        HumanEntity entity = inv.getWhoClicked();
        if(!(entity instanceof Player))
            return;

        SettingsProvider.INVENTORY.open((Player) entity);
    });

    public static ItemStack getBackItem() {
        String backDisplay = MessageManager.getMessage("settings.items.back");

        ItemStack item  = BACK.clone();
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(backDisplay));
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack getNextPageItem() {
        ItemStack skull = SkullCreator.itemFromBase64(nextPageBase);

        skull.editMeta(meta -> {
            String nextPage = MessageManager.getMessage("settings.items.next");
            meta.displayName(Component.text((nextPage)));
        });

        return skull;
    }

    public static ItemStack getPreviousPageItem() {
        ItemStack skull = SkullCreator.itemFromBase64(previousPageBase);

        skull.editMeta(meta -> {
            String previousPage = MessageManager.getMessage("settings.items.previous");
            meta.displayName(Component.text((previousPage)));
        });

        return skull;
    }
}
