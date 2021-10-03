package me.sshcrack.sutils.commands.utils;

import me.sshcrack.sutils.CommandResponse;
import me.sshcrack.sutils.commands.SubCommand;
import me.sshcrack.sutils.ui.SettingsProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SettingsCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");
        Player player = (Player) sender;


        SettingsProvider.INVENTORY.open(player);
        return new CommandResponse("utils.settings");
    }

    @Override
    public String getCommand() {
        return "settings";
    }

    @Override
    public String getNode() {
        return "utils.manage";
    }

    @Override
    public String getHelp() {
        return "Opens challenge settings gui";
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
    public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) { }

    @Override
    public int getMaximumArguments() {
        return 0;
    }

}
