package me.sshcrack.sutils.commands.utils;

import me.sshcrack.sutils.CommandResponse;
import me.sshcrack.sutils.commands.SubCommand;
import me.sshcrack.sutils.tools.timer.UtilTimer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TimerCommand extends SubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return new CommandResponse("onlyplayer");

        boolean paused = UtilTimer.isPaused();
        if(paused) {
            UtilTimer.resume();
            return new CommandResponse("utils.timer.resumed");
        }

        UtilTimer.pause();
        return new CommandResponse("utils.timer.paused");
    }

    @Override
    public String getCommand() {
        return "timer";
    }

    @Override
    public String getNode() {
        return "utils.standard";
    }

    @Override
    public String getHelp() {
        return "Manages the timer";
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
