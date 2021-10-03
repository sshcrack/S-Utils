package me.sshcrack.sutils.commands;

import me.sshcrack.sutils.CommandResponse;
import me.sshcrack.sutils.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * This is used for any parent commands across the system
 * 
 * @author booksaw
 *
 */
public class ParentCommand extends SubCommand {

	/**
	 * Used to store all applicable sub commands
	 */
	private HashMap<String, SubCommand> subCommands = new HashMap<>();

	/**
	 * Used to store what the parent command reference is
	 */
	private String command;

	/**
	 * Creates a new parent command with a set of sub commands
	 * 
	 * @param command the command which will be defaulted to if the user enters an
	 *                incorrect command
	 */
	public ParentCommand(String command) {
		this.command = command;
	}

	/**
	 * this method adds another command to the parent command
	 * 
	 * @param command the command to add
	 */
	public void addSubCommand(SubCommand command) {
		subCommands.put(getReference(command), command);
	}

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		return onCommand(sender, label, args, false);
	}

	public CommandResponse onCommand(CommandSender sender, String label, String[] args, boolean first) {
		// checking length
		if (args.length == 0) {
			// help command is not expected to return anything
			displayHelp(sender, label, args);
			return null;
		}

		if (!first) {
			label = label + " " + getCommand();
		}

		SubCommand command = subCommands.get(args[0].toLowerCase());
		if (command == null) {
			displayHelp(sender, label, args);
			return null;
		}

		if (!sender.hasPermission(command.getNode()) && !command.getNode().equals("")) {
			MessageManager.sendMessage(sender, "noPerm");
			return null;
		}

		String[] newArgs = removeFirstElement(args);
		// checking enough arguments have been entered
		if (command.getMinimumArguments() > newArgs.length) {
			MessageManager.sendMessage(sender, "invalidArgs");
			displayHelp(sender, label, args);
			return null;
		} else if (command.needPlayer() && !(sender instanceof Player)) {
			return new CommandResponse("needPlayer");
		}

		CommandResponse result = command.onCommand(sender, label, newArgs);

		if (result == null) {
			return null;
		}

		return result;
	}

	/**
	 * Used to display the help information to the user
	 * 
	 * @param sender the user which called the command
	 * @param label  the label of the command
	 * @param args   the arguments that the user entered
	 */
	private void displayHelp(CommandSender sender, String label, String[] args) {
		return;
	}

	/**
	 * Used to remove the first element, this is used when sending commands into sub
	 * commands
	 */
	private String[] removeFirstElement(String[] args) {
		String[] toReturn = new String[args.length - 1];

		for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = args[i + 1];
		}

		return toReturn;

	}

	public HashMap<String, SubCommand> getSubCommands() {
		return subCommands;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public String getNode() {
		return "";
	}

	@Override
	public String getHelp() {
		return "";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length <= 1 || args[0].equals("")) {
			for (Entry<String, SubCommand> subCommand : subCommands.entrySet()) {
				if ((args.length == 0 || subCommand.getKey().startsWith(args[0]))
						&& (sender.hasPermission(subCommand.getValue().getNode())  || subCommand.getValue().getClass() == ParentCommand.class))
					options.add(subCommand.getKey());
			}
			return;
		}

		SubCommand command = subCommands.get(args[0]);
		if (command == null) {
			return;
		}

		if ((args.length - 1 > command.getMaximumArguments() && command.getMaximumArguments() >= 0)
				|| (command.needPlayer() && !(sender instanceof Player))) {
			return;
		}

		command.onTabComplete(options, sender, label, removeFirstElement(args));
		return;
	}

	@Override
	public int getMaximumArguments() {
		return -1;
	}

	public String getReference(SubCommand subCommand) {

		String toReturn = MessageManager.getMessage("command." + subCommand.getCommand());
		if (toReturn != null && !toReturn.equals("")) {
			return toReturn;
		}

		Bukkit.getLogger().info(ChatColor.GREEN + "setting " + "command." + subCommand.getCommand());
		Bukkit.getLogger().info("Messages are " + MessageManager.getMessages());
		MessageManager.getMessages().set("command." + subCommand.getCommand(), subCommand.getCommand());

		File f = MessageManager.getFile();
		try {
			MessageManager.getMessages().save(f);
		} catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
		}

		return subCommand.getCommand();

	}

}
