package me.sshcrack.sutils.commands.utils;

import me.sshcrack.sutils.CommandResponse;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.commands.SubCommand;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.Tools;
import me.sshcrack.sutils.tools.location.Position;
import me.sshcrack.sutils.tools.location.PositionManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PosCommand extends SubCommand {
    public HashMap<Player, String> lastAccesses = new HashMap<>();

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        Player player = (Player) sender;
        if(args.length == 0) {
            String delimiter = MessageManager.getMessage("utils.position.delimiter");
            String joined = String.join(delimiter, PositionManager.listNames());

            MessageManager.sendMessageF(player, "utils.position.list", joined);
            return null;
        }

        String name = args[0];
        if(name.equalsIgnoreCase("reset")) {
            PositionManager.reset();
            MessageManager.sendMessageF(player, "utils.position.reset");

            return null;
        }

        if(name.equalsIgnoreCase("compass")) {
            String last = args.length >= 2 ? args[1] : lastAccesses.get(player);
            PlayerInventory inv = player.getInventory();

            if(last == null)
                return new CommandResponse("utils.position.invoke");

            Location pos = PositionManager.get(last);
            if(pos == null)
                return new CommandResponse("utils.position.not_existent");

            ItemStack compass = new ItemStack(Material.COMPASS);
            compass.editMeta(e -> {
                CompassMeta meta = (CompassMeta) e;
                meta.setLodestoneTracked(false);
                meta.setLodestone(pos);
            });

            inv.addItem(compass);
            MessageManager.sendMessageF(player, "utils.position.compass", last);
            return null;
        }

        if(name.equalsIgnoreCase("remove")) {
            if(args.length < 2)
                return new CommandResponse("utils.position.name_needed");

            String toDelete = args[1];
            boolean result = PositionManager.remove(toDelete);
            if(!result) {
                MessageManager.sendMessageF(player, "utils.position.cant_remove", toDelete);
                return null;
            }

            MessageManager.sendMessageF(player, "utils.position.removed",toDelete);
            return null;
        }

        boolean isSafe = Position.checkValidName(name);
        if(!isSafe)
            return new CommandResponse("utils.position.invalid");

        Location existant = PositionManager.get(name);
        if(existant != null) {
            int x = existant.getBlockX();
            int y = existant.getBlockY();
            int z = existant.getBlockZ();

            String sX = String.valueOf(x);
            String sY = String.valueOf(y);
            String sZ = String.valueOf(z);

            MessageManager.sendMessageF(player, "utils.position.get", name, sX, sY, sZ);
            lastAccesses.put(player, name);
            return null;
        }

        String playerName = player.getDisplayName();
        Location loc = player.getLocation();

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        Location blockLoc = new Location(loc.getWorld(), x,y,z);

        Position pos = new Position(name, blockLoc);
        boolean couldAdd = PositionManager.add(pos);
        if(!couldAdd)
            return new CommandResponse("utils.position.unknown_error");

        String createMSG = MessageManager.getMessageF(
                "utils.position.create",
                name,
                playerName,
                String.valueOf(x),
                String.valueOf(y),
                String.valueOf(z));

        Tools.getPlayers().sendMessage(Component.text(createMSG));
        return null;
    }

    @Override
    public String getCommand() {
        return "reload";
    }

    @Override
    public String getNode() {
        return "utils.manage";
    }

    @Override
    public String getHelp() {
        return "Reloads this config";
    }

    @Override
    public String getArguments() {
        return "<mode|pos> <position>";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
        if(args.length == 0 || args.length == 1) {
            options.add("reset");
            options.add("remove");
            options.add("compass");
        }

        options.addAll(PositionManager.listNames());
    }

    @Override
    public int getMaximumArguments() {
        return 2;
    }

}
