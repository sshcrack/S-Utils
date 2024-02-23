package me.sshcrack.sutils.ui.categories;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import me.sshcrack.sutils.interactable.challenges.module.ToggleableList;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.items.Skulls;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ChallengeInventory implements InventoryProvider {
    public static SmartInventory INVENTORY = SmartInventory.builder()
            .id("challenge-util-settings")
            .provider(new ChallengeInventory())
            .manager(Main.plugin.invManager)
            .size(6, 9)
            .title(MessageManager.getMessage("settings.titles.challenges"))
            .build();

    public static String nameEnable = MessageManager.getMessage("settings.name.enable");
    public static String nameDisable = MessageManager.getMessage("settings.name.disable");

    public static String descEnabled = MessageManager.getMessage("settings.description.enabled");
    public static String descDisabled = MessageManager.getMessage("settings.description.disabled");

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        ClickableItem border = ClickableItem.empty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        int challengeSize = ToggleableList.instance.CHALLENGES.length;
        int width = 7;

        ArrayList<ClickableItem> items = new ArrayList<>();
        for(int i = 0; i < challengeSize; i++) {
            Challenge challenge = ToggleableList.instance.CHALLENGES[i];
            Main.plugin.getLogger().info(String.format("Loading challenge %s", challenge.toString()));

            Supplier<ItemStack> updateItem = () -> {
                ItemStack item = challenge.getItem();
                boolean enabled = challenge.isEnabled();

                String name = challenge.getName();
                String displayName = String.format(enabled ? nameEnable : nameDisable, name);

                Component descStatus = Component.text(enabled ? descEnabled : descDisabled);

                List<Component> description = challenge.getDescription()
                                                .stream()
                                                .map(Component::text)
                                                .collect(Collectors.toList());

                description.add(0, descStatus);


                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);


                meta.displayName(Component.text(displayName));
                meta.lore(description);

                item.setItemMeta(meta);

                if(enabled)
                    item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

                return item;
            };



            ItemStack toUse = updateItem.get();
            if(i % 7 == 0) {
                if(i != 0)
                    items.add(border);
                items.add(border);
            }
            items.add(
                ClickableItem.of(toUse, (inv) -> {
                    Main.plugin.getLogger().info(String.format("Is enabled %s name %s", challenge.isEnabled(), challenge.getName()));
                    if(challenge.isEnabled())
                        challenge.disable();
                    else
                        challenge.enable();

                    ItemStack newItem = updateItem.get();
                    inv.setCurrentItem(newItem);
                })
            );
        }

        pagination.setItems(items.toArray(new ClickableItem[0]));
        pagination.setItemsPerPage(width * 3);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

        contents.fillBorders(border);
        contents.set(5, 0, Skulls.CLICKABLE_BACK);
        contents.set(5, 2, ClickableItem.of(
                Skulls.PREVIOUS_PAGE,
                e -> INVENTORY.open(player, pagination.previous().getPage()))
        );

        contents.set(5, 6, ClickableItem.of(
                Skulls.NEXT_PAGE,
                e -> INVENTORY.open(player, pagination.next().getPage()))
        );

    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
