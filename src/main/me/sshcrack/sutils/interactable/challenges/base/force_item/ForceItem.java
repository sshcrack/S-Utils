package me.sshcrack.sutils.interactable.challenges.base.force_item;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.timer.GeneralTimer;
import me.sshcrack.sutils.tools.timer.TimeFormatter;
import me.sshcrack.sutils.tools.timer.TimerState;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ForceItem extends Challenge {
    public final String DURATION_MILLIS_LOC = String.format("%s.duration", getRoot());
    public final int DURATION_MILLIS;
    public final String ITEM_CONFIG_LOC = String.format("%s.item_list", getRoot());
    private final GeneralTimer timer;
    private BossBar bar;
    private final List<Material> itemList = new ArrayList<>();
    final Main plugin;
    final Server server;

    public ForceItem() {
        super("force_item", new Properties()
                .item(new ItemStack(Material.CHEST))
                .timerEnabled()
                .dontInitialize()
        );

        timer = new GeneralTimer(String.format("%s.timer", getRoot()));
        plugin = Main.plugin;
        server = plugin.getServer();
        DURATION_MILLIS = plugin.getConfig().getInt(DURATION_MILLIS_LOC, 1000 * 60 * 13);
        initialize();
    }

    @Nullable
    private Material getCurrentItem() {
        if (itemList.size() == 0)
            return null;
        return itemList.get(itemList.size() - 1);
    }

    private String getCurrTitle(boolean paused, long currTime) {
        var currItem = getCurrentItem();
        if (currItem == null)
            return "Loading...";

        String itemName = currItem.name().replaceAll("_", " ").toLowerCase();
        long timeLeft = DURATION_MILLIS - currTime;
        String humanReadable = TimeFormatter.formatTime(timeLeft);

        String out = String.format("%s (%s)", itemName, humanReadable);
        if (paused) {
            out += " - paused";
        }
        return out;
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        FileConfiguration config = plugin.getConfig();
        List<String> configList = config.getStringList(ITEM_CONFIG_LOC);

        itemList.addAll(configList
                .stream()
                .map(Material::getMaterial)
                .toList()
        );

        if (itemList.isEmpty()) {
            System.out.println("Adding random");
            itemList.add(getRandomMaterial());
        }
    }

    public void checkForNextItem() {
        Material currItem = getCurrentItem();
        timer.reset();
        boolean hasItem = false;
        for (Player p : plugin.getServer().getOnlinePlayers()) {

            for (ItemStack item : p.getInventory()) {
                if (item != null && item.getType() == currItem) {
                    hasItem = true;
                    break;
                }
            }

            if(hasItem)
                break;
        }

        if(!hasItem) {
            server.getScheduler().runTask(plugin, () -> {
                server.getOnlinePlayers().stream().findFirst().ifPresent(p -> p.setHealth(0));
            });
            timer.pause();
            UtilTimer.pause();
            this.itemList.clear();
        } else {
            timer.resume();
        }
        itemList.add(getRandomMaterial());
    }

    public Material getRandomMaterial() {
        Material nextMaterial = null;
        Material[] obtainable = SurvivalObtainable.OBTAINABLE;

        while (nextMaterial == null || itemList.contains(nextMaterial)) {
            int rnd = (int) (Math.random() * obtainable.length);
            nextMaterial = obtainable[rnd];
        }

        return nextMaterial;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        bar = server
                .createBossBar(
                        MessageManager.getMessage("challenges.force_item.start_timer"),
                        BarColor.GREEN,
                        BarStyle.SOLID
                );

        bar.setVisible(true);
        timer.onTimerUpdate.add((state, humanReadable) -> {
            long curr = timer.getMillis();

            for (Player player : plugin
                    .getServer()
                    .getOnlinePlayers()
                    .stream()
                    .filter(e -> bar.getPlayers().stream().noneMatch(p -> p.getUniqueId().equals(e.getUniqueId())))
                    .toList()
            ) {
                bar.addPlayer(player);
            }

            if (curr >= DURATION_MILLIS) {
                checkForNextItem();
                return;
            }

            double progress = ((double) curr) / DURATION_MILLIS;
            bar.setProgress(1 - progress);

            boolean paused = state == TimerState.PAUSED;
            if (paused) {
                bar.setColor(BarColor.RED);
            } else {
                bar.setColor(BarColor.BLUE);
            }

            bar.setTitle(getCurrTitle(paused, curr));
        });

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            bar.addPlayer(player);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        timer.clearEvents();
        bar.removeAll();
    }

    @Override
    public void onRegister() {
        timer.resume();
    }

    @Override
    public void onUnregister() {
        timer.pause();
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        timer.save();

        FileConfiguration config = plugin.getConfig();
        config.set(ITEM_CONFIG_LOC, itemList.stream().map(Enum::toString));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        bar.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void handleSkip(AsyncPlayerChatEvent e) {
        if (e.getMessage().equalsIgnoreCase("skip")){

            e.getPlayer().sendMessage(Component.text("Skipped item.").color(TextColor.color(0, 255, 0)));
            itemList.add(getRandomMaterial());
            timer.setMillis(0);
        }

        if(e.getMessage().equalsIgnoreCase("showall")) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                bar.addPlayer(player);
            }
        }
    }
}
