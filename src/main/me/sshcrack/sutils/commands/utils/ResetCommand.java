package me.sshcrack.sutils.commands.utils;

import me.sshcrack.sutils.CommandResponse;
import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.commands.SubCommand;
import me.sshcrack.sutils.tools.world.WorldManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.List;

public class ResetCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        WorldManager.resetWorld((Player) sender);
        return new CommandResponse("utils.reset");
    }

    @Override
    public String getCommand() {
        return "reset";
    }

    @Override
    public String getNode() {
        return "utils.manage";
    }

    @Override
    public String getHelp() {
        return "Resets the world";
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
