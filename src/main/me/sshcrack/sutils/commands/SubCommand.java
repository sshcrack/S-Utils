package me.sshcrack.sutils.commands;

import me.sshcrack.sutils.CommandResponse;
import me.sshcrack.sutils.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class is used by any commands which are included within a command tree
 * (for example /command subcommand)
 * 
 * @author booksaw
 *
 */
public abstract class SubCommand {

	/**
	 * This method is used to load the help message from the file, or if there is
	 * not one, it will get the default message
	 * 
	 * @return the help message for the subcommand
	 */
	public String getHelpMessage() {
		String message = MessageManager.getMessages().getString("help." + getCommand());
		if (message == null || message.equals("")) {
			message = getHelp();
			MessageManager.getMessages().set("help." + getCommand(), getHelp());

			File f = MessageManager.getFile();
			try {
				MessageManager.getMessages().save(f);
			} catch (IOException ex) {
				Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
			}
		}
		return message;
	}

	/**
	 * <p>
	 * This method is called whenever the sub command is run, return the message (+
	 * chat color if it should not be the default chat color)
	 * </p>
	 * <p>
	 * The return value should be the value of the message to be sent to the user,
	 * for more complicated messaging systems return null
	 * </p>
	 * 
	 * @param sender the person who called the command
	 * @param label  the label of the initial command (useful for help files)
	 * @param args   the arguments of the sub command (starting at args[0], as the
	 *               sub command itself will be removed)
	 * @return the message
	 */
	public abstract CommandResponse onCommand(CommandSender sender, String label, String[] args);

	/**
	 * @return the sub-command which this class handles
	 */
	public abstract String getCommand();

	/**
	 * @return the permission node for that sub command
	 */
	public abstract String getNode();

	/**
	 * @return the help information for that sub command (this does not include the
	 *         arguments)
	 */
	public abstract String getHelp();

	/**
	 * @return the arguments for that sub command ie '[name]'
	 */
	public abstract String getArguments();

	/**
	 * Used to get the minimum number of arguments which need to be parsed into this
	 * command
	 * 
	 * @return the number of minimum arguments
	 */
	public abstract int getMinimumArguments();

	/**
	 * return -1 if there is no cap
	 * 
	 * @return the maximum number of arguments for the command
	 */
	public abstract int getMaximumArguments();

	/**
	 * Used to check if the commandSender needs to be a player, defaults to false
	 * 
	 * @return if the commandSender needs to be a player
	 */
	public boolean needPlayer() {
		return false;
	}

	public abstract void onTabComplete(List<String> options, CommandSender sender, String label, String[] args);
}
