package me.sshcrack.sutils;

import fr.minuskube.inv.InventoryManager;
import me.sshcrack.sutils.interactable.challenges.module.ChallengeList;
import me.sshcrack.sutils.commands.ParentCommand;
import me.sshcrack.sutils.commands.utils.*;
import me.sshcrack.sutils.events.damage.DamageLogger;
import me.sshcrack.sutils.events.timer.LeaveTimerListener;
import me.sshcrack.sutils.events.timer.PauseTimerListener;
import me.sshcrack.sutils.events.UtilListener;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import me.sshcrack.sutils.tools.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    public static Main plugin;
    public static String name = "Utils";

    public InventoryManager invManager;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        plugin = this;

        MessageManager.lang = getConfig().getString("language");
        assert MessageManager.lang != null;
        if (MessageManager.lang.equals("en") || MessageManager.lang.equals("")) {
            MessageManager.lang = "messages";
        }

        loadCustomConfigs();
        WorldManager.load();
    }

    @Override
    public void onEnable() {
        WorldManager.enable();

        setupListeners();
        setupCommands();

        //Enable challenges
        new ChallengeList();

        setupInv();
    }

    @Override
    public void onDisable() {
        ChallengeList.disable();
        save();
    }

    public void reload() {
        onDisable();
        reloadConfig();
        onEnable();
    }

    public void save() {
        UtilTimer.disable();
        WorldManager.save();

        FileConfiguration config  = this.getConfig();
        config.set(UtilTimer.currLoc, UtilTimer.getMillis());
        this.saveConfig();
    }


    public void setupInv() {
        this.invManager = new InventoryManager(this);
        this.invManager.init();
    }

    public void setupCommands() {
        ParentCommand utilsParent = new ParentCommand("utils");

        utilsParent.addSubCommand(new ReloadCommand());


        new CustomCommand("utils", utilsParent, "template.standard", "All commands for utils",
                getConfig().getStringList("command.utils"));
        new CustomCommand("settings", new SettingsCommand(), "template.manage", "Open Utils settings",
                getConfig().getStringList("command.settings"));
        new CustomCommand("timer", new TimerCommand(), "template.standard", "Manage the timer",
                getConfig().getStringList("command.timer"));
        new CustomCommand("reset", new ResetCommand(), "template.manage", "Reset world",
                getConfig().getStringList("command.reset"));

        new CustomCommand("position", new PosCommand(), "template.standard", "Get, reset and remove positions",
                getConfig().getStringList("command.position"));
    }

    public void setupListeners() {
        Bukkit.getLogger().info("Setting up listeners...");
        getServer().getPluginManager().registerEvents(new UtilListener(this), this);

        new PauseTimerListener();
        new LeaveTimerListener();
        new DamageLogger();
    }
    /**
     * This method is used to add any config values which are required post 3.0
     * @param messages The yml of messages
     */
    private void addDefaults(YamlConfiguration messages) {
        File f = MessageManager.getFile();

        // if something has been changed, saving the new config
        if (!f.exists()) {
            Bukkit.getLogger().info("Saving new messages to messages.yml");

            try {
                messages.save(f);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
            }
        }
    }

    public void loadCustomConfigs() {
        File f = MessageManager.getFile();

        try {
            if (!f.exists()) {
                saveResource(MessageManager.lang + ".yml", false);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Could not load selected language: " + MessageManager.lang);
            MessageManager.lang = "messages";
            loadCustomConfigs();
            return;
        }

        YamlConfiguration messages = YamlConfiguration.loadConfiguration(f);
        addDefaults(messages);

        MessageManager.addMessages(messages);
    }
}

