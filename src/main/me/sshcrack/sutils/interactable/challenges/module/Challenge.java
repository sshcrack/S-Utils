package me.sshcrack.sutils.interactable.challenges.module;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.challenges.module.settings.Setting;
import me.sshcrack.sutils.interactable.toggable.ToggableListener;
import me.sshcrack.sutils.interactable.toggable.Toggleable;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.Tools;
import me.sshcrack.sutils.tools.string.colors.ColorUtil;
import me.sshcrack.sutils.tools.timer.TimerState;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class Challenge extends ToggableListener {
    //If the challenge is listening for events
    private boolean eventsRegistered = false;

    //If events should always be enabled, for example for ultra hardcore4
    private final boolean alwaysRegistered;

    //If the challenge is enabled in config
    private boolean enabled;

    final PluginManager manager = Bukkit.getPluginManager();
    final Main plugin = Main.plugin;

    String enabledTitle;
    String disabledTitle;
    String configLoc;

    public Challenge(String id, Challenge.Properties properties) {
        super(
                id,
                new Toggleable.Properties()
                        .item(properties.marker)
                        .clickToggle()
                        .enchant()
                        .statusDescription()
                        .showTitles()
        );

        this.alwaysRegistered = properties.alwaysEnabled;


        loadChallengeConfig();

        this.enabledTitle = String.format(MessageManager.getMessage("title.enabled"), getItem());
        this.disabledTitle = String.format(MessageManager.getMessage("title.disabled"), name);
        if(alwaysRegistered && this.enabled)
            registerEvents();

        UtilTimer.onStateChange.add(state -> {
            if(alwaysRegistered)
                return;

            if(state == TimerState.PAUSED)
                unregisterEvents();

            if(!enabled)
                return;

            if(state == TimerState.RUNNING)
                registerEvents();
        });
    }


    //------------------------------------
    //
    //
    //Enable & Disable
    //
    //
    //------------------------------------



    public final void enable() {
        enable(false);
    }

    public final void enable(boolean silent) {
        plugin.getLogger().info(String.format("Enabling challenge %s", this.name));
        if (enabled && eventsRegistered) {
            plugin.getLogger().info(String.format("Challenge %s is already enabled.", this.name));
            return;
        }

        Title.Times times = Title.Times.of(
                Duration.ofMillis(500),
                Duration.ofMillis(2000),
                Duration.ofMillis(500)
        );

        Title title = Title.title(Component.text(""), Component.text(this.enabledTitle), times);

        if(!silent)
            Tools.getPlayers().showTitle(title);

        enabled = true;

        if(!UtilTimer.isPaused() || alwaysRegistered)
            registerEvents();
    }

    public final void disable() {
        disable(false);
    }

    public final void disable(boolean silent) {
        plugin.getLogger().info(String.format("Disabling challenge %s", this.name));
        if (!enabled) {
            plugin.getLogger().info(String.format("Challenge %s is already disabled", this.name));
            return;
        }

        Title.Times times = Title.Times.of(
                Duration.ofMillis(500),
                Duration.ofMillis(2000),
                Duration.ofMillis(500)
        );

        Title title = Title.title(Component.text(""), Component.text(this.disabledTitle), times);

        if(!silent)
            Tools.getPlayers().showTitle(title);


        enabled = false;

        onDisable();
        unregisterEvents();
    }



    //------------------------------------
    //
    //
    //Events
    //
    //
    //------------------------------------

    public final void registerEvents() {
        if(eventsRegistered)
            return;

        eventsRegistered = true;
        manager.registerEvents(this, plugin);

        onRegister();
        if(enabled)
            onEnable();
    }

    public final void unregisterEvents() {
        if(!eventsRegistered)
            return;

        eventsRegistered = false;
        HandlerList.unregisterAll(this);
        onUnregister();
        if(!enabled)
            onDisable();
    }

    public boolean isEnabled() {
        return this.enabled;
    }




    //------------------------------------
    //
    //
    //Config
    //
    //
    //------------------------------------

    public final void saveChallengeConfig() {
        String enabledLoc = String.format("%s.enabled", configLoc);
        plugin.getConfig().set(enabledLoc, this.enabled);

        onSave();
    }

    public final void loadChallengeConfig() {
        FileConfiguration config = plugin.getConfig();

        this.configLoc = String.format("challenges.%s", id);
        String nameLoc = String.format("%s.name", configLoc);
        String enabledLoc = String.format("%s.enabled", configLoc);
        String descLoc = String.format("%s.description", configLoc);

        Main.plugin.getLogger().info(String.format("%s description is %s", this, descLoc));
        boolean exists = config.isBoolean(enabledLoc);
        if (!exists)
            config.set(enabledLoc, false);

        enabled = config.getBoolean(enabledLoc, false);


        this.name = MessageManager.getMessage(nameLoc);
        List<String> descList = MessageManager.getMessages().getStringList(descLoc);

        this.description = descList
                .stream()
                .map(ColorUtil::translate)
                .collect(Collectors.toList());

        onLoad();
    }



    //------------------------------------
    //
    //
    // Events
    //
    //
    //------------------------------------

    /**
     * Whenever the config is loaded
     */
    public void onLoad() {

    }

    /**
     * whenever the config is saved
     */
    public void onSave() {

    }

    public void onRegister() {

    }

    public void onUnregister() {

    }




    //------------------------------------
    //
    //
    // Getters
    //
    //
    //------------------------------------


    public List<String> getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getId() {
        return id;
    }

    /**
     * This is the properties class used to condense the huge needed super in a class
     */
    public static class Properties {
        ItemStack marker;
        boolean alwaysEnabled;
        List<Setting> settings;

        public Properties item(ItemStack item) {
            this.marker = item;
            return this;
        }

        public Properties item(Supplier<ItemStack> itemSupplier) {
            this.marker = itemSupplier.get();

            return this;
        }

        public Properties alwaysEnabled() {
            this.alwaysEnabled = true;

            return this;
        }

        public Properties addSetting(Setting settings) {
            this.settings.add(settings);

            return this;
        }
    }
}
