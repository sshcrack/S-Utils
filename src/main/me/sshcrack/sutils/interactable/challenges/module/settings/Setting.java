package me.sshcrack.sutils.interactable.challenges.module.settings;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.Interactable;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Function;

public class Setting<T> {
    public final Main plugin = Main.plugin;
    public final FileConfiguration config = plugin.getConfig();
    public final Interactable challenge;
    public final Function<InventoryClickEvent, T> onClick;

    String description;
    String name;

    String id;


    public Setting(Interactable challenge, String id, Function<InventoryClickEvent, T> onClick) {
        this.challenge = challenge;
        this.id = id;
        this.onClick = onClick;
    }

    public final void loadSettingConfig() {

    }

    public String getRoot() {
        return "interactable";
    }
}
