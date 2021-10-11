package me.sshcrack.sutils.interactable.challenges.base.borderItem;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.interactable.challenges.module.Challenge;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.Tools;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BorderItem extends Challenge {
    private final String cPrefix = "challenges.border_item.";
    private int border_size = 3 *2;
    private final int addition = 3 *2;

    FileConfiguration config = Main.plugin.getConfig();

    World world = Bukkit.getWorld("world");
    Location spawn = world.getSpawnLocation();
    WorldBorder border = world.getWorldBorder();


    public ArrayList<Material> materials = new ArrayList<>();
    public ArrayList<PotionStore> potions = new ArrayList<>();

    public BorderItem() {
        super(
                new ItemStack(Material.BEACON),
                "border_item",
                false
        );
    }

    @Override
    public void onRegister() {
        Location spawn = world.getSpawnLocation();

        loadBorderConfig();

        Main.plugin.getLogger().info(String.format("Border size is %s", border_size));
        border.setSize(border_size);
        border.setCenter(spawn);
    }

    public void loadBorderConfig() {
        long currSeed = world.getSeed();
        long seed = config.getLong(cPrefix + "seed", currSeed);

        if(currSeed != seed) {
            Main.plugin.getLogger().warning("Seed change detected, resetting border!");
            config.set(cPrefix + "seed", currSeed);

            border.setCenter(spawn);
            border.setSize(border_size);
            return;
        }

        List<String> rawMats = config.getStringList(cPrefix +"collected");
        List<String> rawPots = config.getStringList(cPrefix +"potions");

        Main.plugin.getLogger().info(String.format("new Border size is %s", border_size));
        border_size = config.getInt(cPrefix + "size");
        potions.addAll(
                rawPots
                        .stream()
                        .map(PotionStore::fromString)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
        materials.addAll(
                rawMats
                        .stream()
                        .map(Material::valueOf)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void onDisable() {
        save();
    }

    @EventHandler()
    public void onItemCraft(CraftItemEvent e) {
        if(!(e.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if(item == null)
            return;

        registerItem(player, item);
    }

    @EventHandler
    public void onItemPickUp(EntityPickupItemEvent e) {
        if(!(e.getEntity() instanceof Player))
            return;
        Player player = (Player) e.getEntity();
        ItemStack item = e.getItem().getItemStack();

        registerItem(player, item);
    }


    public void registerItem(Player player, ItemStack item) {
        Material mat = item.getType();
        ItemMeta meta = item.getItemMeta();

        if(meta instanceof PotionMeta) {
            registerPotion(player, (PotionMeta) meta, mat);
            return;
        }
        boolean alreadyCollected = materials.contains(mat);
        if(alreadyCollected)
            return;

        materials.add(mat);
        String name = screamingToNormal(mat.name());
        showSuccess(player, name);
    }

    public void registerPotion(Player player, PotionMeta meta, Material mat) {
        PotionType type = meta.getBasePotionData().getType();

        boolean contains = containsPotion(type, mat);
        if(contains)
            return;

        PotionStore store = new PotionStore(mat, type);
        potions.add(store);

        String name = screamingToNormal(mat.name()) + " of " + screamingToNormal(type.name());
        showSuccess(player, name);
    }



    public boolean containsPotion(PotionType type, Material mat) {
        List<PotionStore> contains = potions
                .stream()
                .filter(e -> e.getPotion() == type && e.getMaterial() == mat)
                .collect(Collectors.toList());

        return contains.size() > 0;
    }



    public void showSuccess(Player player, String itemName) {
        Audience audience = Tools.getPlayers();
        String playerName = player.getDisplayName();

        Sound sound = Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, .5f, 0f);
        audience.playSound(sound);


        String rawMSG = MessageManager.getMessageF("challenges.border_item.increased.msg", itemName, playerName);
        String prefix = MessageManager.getPrefix();

        audience.sendMessage(Component.text(prefix + rawMSG));

        String titleStr = MessageManager.getMessage("challenges.border_item.increased.title");
        String sub = MessageManager.getMessage("challenges.border_item.increased.substring");

        Duration inOut = Duration.ofMillis(200);
        Duration stay = Duration.ofMillis(500);

        Title.Times times = Title.Times.of(inOut, stay, inOut);
        Title title = Title.title(Component.text(titleStr), Component.text(sub), times);

        audience.showTitle(title);

        border_size += addition;

        border.setCenter(spawn);
        border.setSize(border_size);
    }

    public void save() {
        config.set(cPrefix + "size", border_size);
        config.set(
                cPrefix + "collected",
                materials
                        .stream()
                        .map(Enum::toString)
                        .collect(Collectors.toList())
        );
        config.set(
                cPrefix + "potions",
                potions
                        .stream()
                        .map(PotionStore::toString)
                        .collect(Collectors.toList())
        );
        config.set(cPrefix + "seed", world.getSeed());
    }

    public String screamingToNormal(String screaming) {
        Pattern pattern = Pattern.compile("[A-Z]{1,}");
        Matcher matcher = pattern.matcher(screaming);

        ArrayList<String> groups = new ArrayList<>();
        while(matcher.find()) {
            String curr = matcher.group().toLowerCase();

            char[] charArr = curr.toCharArray();
            if(charArr.length == 0)
                continue;

            char firstChar = charArr[0];
            char capital = Character.toUpperCase(firstChar);

            charArr[0] = capital;
            groups.add(new String(charArr));
        }

        return String.join(" ", groups);
    }
}
