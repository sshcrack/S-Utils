package me.sshcrack.sutils.commands.utils;

import me.sshcrack.sutils.CommandResponse;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.commands.SubCommand;
import me.sshcrack.sutils.message.MessageManager;
import me.sshcrack.sutils.tools.string.StringFormatter;
import me.sshcrack.sutils.tools.string.colors.Gradient;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;

public class TestCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        return new CommandResponse("gradient");
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
