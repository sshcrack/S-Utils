package me.sshcrack.sutils.interactable.toggable;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.Interactable;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.Tools;
import me.sshcrack.sutils.tools.string.colors.ColorUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Toggleable implements Interactable {
    //<editor-fold desc="Variables">
    private final ItemStack item;
    private final FileConfiguration config = Main.plugin.getConfig();

    private String name;
    private List<String> description;

    private String enabledDesc;
    private String disabledDesc;

    private boolean enabled = false;
    private Supplier<Boolean> showTitles;

    private String enabledTitle;
    private String disabledTitle;

    private final Title.Times times = Title.Times.of(
            Duration.ofMillis(500),
            Duration.ofMillis(2000),
            Duration.ofMillis(500)
    );


    private final String id;
    private final Properties properties;
    //</editor-fold>

    public Toggleable(String id, Properties properties) {
        this.id = id;
        this.properties = properties;

        loadMessages();
        loadConfig();

        this.showTitles = properties.showTitles;
        this.item = Tools.firstDefault(properties.item, getDefaultItem());

        this.enabledTitle = Tools.firstDefault(properties.enableTitle, this.enabledTitle);

        this.enabledDesc = Tools.firstDefault(properties.enabled, this.enabledDesc);
        this.disabledDesc = Tools.firstDefault(properties.disabled, this.disabledDesc);
    }

    //<editor-fold desc="Clickables">
    public void addClickable(InventoryContents contents, int row, int column) {
        this.addClickable(contents, row, column, e -> {
        });
    }


    public void addClickable(InventoryContents contents, int row, int column, Consumer<InventoryClickEvent> onClick) {
        Consumer<InventoryClickEvent> onAction = new Consumer<>() {
            @Override
            public void accept(InventoryClickEvent event) {
                if (properties.clickToggle) {
                    if (isEnabled())
                        disable();
                    else
                        enable();
                }

                onClick.accept(event);
                contents.set(row, column, ClickableItem.of(getItem(), this));
            }
        };

        contents.set(row, column, ClickableItem.of(getItem(), onAction));
    }
    //</editor-fold>

    //<editor-fold desc="Enable / Disable">
    public void enable() {
        if (isEnabled())
            return;

        this.enabled = true;
        saveConfig();

        if (showTitles.get())
            showEnabledTitle();

        onEnable();
    }

    public void disable() {
        if (!isEnabled())
            return;

        this.enabled = false;
        saveConfig();

        if (showTitles.get())
            showDisabledTitle();

        onDisable();
    }

    public void showEnabledTitle() {
        Audience allPlayers = Tools.getPlayers();


        Title title = Title.title(Component.text(""), Component.text(this.disabledTitle), times);
        allPlayers.showTitle(title);
    }


    public void showDisabledTitle() {
        Audience allPlayers = Tools.getPlayers();


        Title title = Title.title(Component.text(""), Component.text(this.disabledTitle), times);
        allPlayers.showTitle(title);
    }

    //</editor-fold>

    //<editor-fold desc="Configuration">

    /**
     * Configs are handled here
     */
    public void loadConfig() {
        String enabledLoc = String.format("%s.enabled", getRoot());

        boolean enabledInConf = config.getBoolean(enabledLoc, false);
        if (enabledInConf)
            enable();
    }

    public void saveConfig() {
        String enabledLoc = String.format("%s.enabled", getRoot());

        config.set(enabledLoc, enabled);
    }

    public void loadMessages() {
        this.enabledDesc = MessageManager.getMessage("settings.description.enabled");
        this.disabledDesc = MessageManager.getMessage("settings.description.disabled");

        this.enabledTitle = MessageManager.getMessage("settings.toggle_titles.enabled");
        this.disabledTitle = MessageManager.getMessage("settings.toggle_titles.disabled");

        FileConfiguration rawFile = MessageManager.getMessages();

        String root = getRoot();
        String nameLoc = String.format("%s.name", root);
        String descLoc = String.format("%s.description", root);

        this.name = MessageManager.getMessage(nameLoc);

        List<String> descList = rawFile.getStringList(descLoc);
        this.description = descList
                .stream()
                .map(ColorUtil::translate)
                .collect(Collectors.toList());
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    public ItemStack getDefaultItem() {
        ItemStack item = new ItemStack(Material.BEDROCK);
        item.editMeta(e ->
                e.displayName(Component.text("No item given."))
        );

        return item;
    }

    public ItemStack getItem() {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = Tools.stringListToComponent(getDescription());

        boolean enabled = isEnabled();

        if (properties.statusDescription)
            lore.add(0, Component.text(
                    enabled ? enabledDesc : disabledDesc
            ));

        meta.lore(lore);
        meta.displayName(Component.text(this.name));

        item.setItemMeta(meta);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        if (enabled)
            item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
        return item;
    }

    public List<String> getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getRoot() {
        return String.format("toggleable.%s", id);
    }
    //</editor-fold>

    //<editor-fold desc="Events">
    public void onEnable() {

    }

    public void onDisable() {

    }
    //</editor-fold>

    //<editor-fold desc="Properties">
    public static class Properties {
        public boolean itemEnchanted = false;
        public boolean clickToggle = false;
        public boolean statusDescription = false;
        public Supplier<Boolean> showTitles = () -> false;

        public @Nullable String enableTitle = null;
        public @Nullable String disableTitle = null;


        public @Nullable String enabled = null;
        public @Nullable String disabled = null;
        public @Nullable ItemStack item;

        public Properties showTitles() {
            showTitles = () -> true;

            return this;
        }

        public Properties showTitles(Supplier<Boolean> showTitle) {
            this.showTitles = showTitle;

            return this;
        }

        public Properties showTitles(String enable, String disable) {
            enableTitle = enable;
            disableTitle = disable;

            return this;
        }

        public Properties showTitles(String enable, String disable, Supplier<Boolean> showTitle) {
            showTitles(enable, disable);
            this.showTitles = showTitle;

            return this;
        }

        public Properties enchant() {
            itemEnchanted = true;

            return this;
        }

        public Properties clickToggle() {
            this.clickToggle = true;

            return this;
        }

        public Properties statusDescription() {
            statusDescription = true;

            return this;
        }

        public Properties status(String enabled, String disabled) {
            this.enabled = enabled;
            this.disabled = disabled;

            return this;
        }

        public Properties item(ItemStack item) {
            this.item = item;

            return this;
        }
    }
    //</editor-fold>
}
