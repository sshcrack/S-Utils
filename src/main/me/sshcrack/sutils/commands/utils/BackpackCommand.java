package me.sshcrack.sutils.commands.utils;

import me.sshcrack.sutils.CommandResponse;
import me.sshcrack.sutils.commands.SubCommand;
import me.sshcrack.sutils.tools.string.colors.Gradient;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.awt.*;
import java.util.List;

public class BackpackCommand extends SubCommand {
    public static Inventory shared = Bukkit.createInventory(null, InventoryType.FURNACE, Component.text("Backpack"));

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player p))
            return new CommandResponse("onlyplayer");

        p.openInventory(shared);
        return new CommandResponse("utils.backpack");
    }

    @Override
    public String getCommand() {
        return "bp";
    }

    @Override
    public String getNode() {
        return "utils.standard";
    }

    @Override
    public String getHelp() {
        return "Opens the backpack";
    }

    @Override
    public String getArguments() {
        return "";
    }

    @Override
    public int getMinimumArguments() {
        return 0;
    }

    @Override
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {}

    @Override
    public int getMaximumArguments() {
        return 0;
    }

}
