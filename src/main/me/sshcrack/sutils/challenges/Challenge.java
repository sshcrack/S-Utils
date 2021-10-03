package me.sshcrack.sutils.challenges;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.message.MessageManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Challenge implements Listener {
    public final List<String> description;
    public final ItemStack marker;
    public final String name;
    public boolean enabled = false;

    final PluginManager manager = Bukkit.getPluginManager();
    final Main plugin = Main.plugin;
    final String enabledTitle;
    final String disabledTitle;
    final String configLoc;

    final String id;

    public Challenge(ItemStack item, String id) {
        FileConfiguration config = plugin.getConfig();
        this.id = id;

        this.configLoc = String.format("challenges.%s", id);
        String nameLoc = String.format("%s.name", configLoc);
        String enabledLoc = String.format("%s.enabled", configLoc);
        String descLoc = String.format("%s.description", configLoc);

        boolean exists = config.isBoolean(enabledLoc);
        if (!exists)
            config.set(enabledLoc, false);

        boolean isEnabled = config.getBoolean(enabledLoc);
        if (isEnabled)
            enable();

        this.name = MessageManager.getMessage(nameLoc);
        List<String> descList = MessageManager.getMessages().getStringList(descLoc);

        this.description = descList
                .stream()
                .map(e -> ChatColor.translateAlternateColorCodes('&', e))
                .collect(Collectors.toList());
        this.marker = item;

        this.enabledTitle = String.format(MessageManager.getMessage("title.enabled"), name);
        this.disabledTitle = String.format(MessageManager.getMessage("title.disabled"), name);
    }

    public void enable() {
        if (enabled) {
            plugin.getLogger().info(String.format("Tried to enable challenge %s, but it is already enabled.", this.name));
            return;
        }

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        String formattedTitle = String.format(this.enabledTitle, this.name);
        Title.Times times = Title.Times.of(
                Duration.ofMillis(500),
                Duration.ofMillis(2000),
                Duration.ofMillis(500)
        );

        Title title = Title.title(Component.text(""), Component.text(formattedTitle), times);
        Audience audience = Audience.audience(players);

        audience.showTitle(title);

        enabled = true;
        saveToConfig();

        manager.registerEvents(this, plugin);
    }

    public void disable() {
        if (!enabled) {
            plugin.getLogger().info(String.format("Tried to enable challenge %s, but it is not enabled.", this.name));
            return;
        }

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Title.Times times = Title.Times.of(
                Duration.ofMillis(500),
                Duration.ofMillis(2000),
                Duration.ofMillis(500)
        );

        Title title = Title.title(Component.text(""), Component.text(this.disabledTitle), times);
        Audience audience = Audience.audience(players);

        audience.showTitle(title);


        enabled = false;
        saveToConfig();

        HandlerList.unregisterAll(this);
    }

    public void saveToConfig() {
        String enabledLoc = String.format("%s.enabled", configLoc);
        plugin.getConfig().set(enabledLoc, this.enabled);
    }
}
