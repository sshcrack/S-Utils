package me.sshcrack.sutils.message;

import me.sshcrack.sutils.Main;
import me.sshcrack.sutils.tools.string.colors.ColorUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.MissingFormatArgumentException;
import java.util.logging.Level;

/**
 * Used to control all communications to the user
 * 
 * @author booksaw
 *
 */
public class MessageManager {

	/**
	 * This is used to store the configuration file which has all the messages
	 * within
	 */
	private static FileConfiguration messages;

	/**
	 * This is the prefix which goes before all messages related to this plugin
	 */
	private static String prefix;

	/**
	 * This is the language reference for the selected language
	 */
	public static String lang;

	/**
	 * This method is used to provide the configuration file in which all the
	 * message references are stored, this method also loads the default prefix
	 * 
	 * @param file the configuration file
	 */
	public static void addMessages(FileConfiguration file) {
		messages = file;

		String colored = ChatColor.translateAlternateColorCodes('&', Main.plugin.getConfig().getString("prefixFormat"));
		prefix =ColorUtil.translateColors(colored);
	}

	/**
	 * Used to send a message to the specified user
	 * 
	 * @param sender    the commandSender which the message should be sent to
	 * @param reference the reference for the message
	 */
	public static void sendMessage(CommandSender sender, String reference) {
		try {
			String message = getMessage(sender, reference);
			if (message == null) {
				Bukkit.getLogger().log(Level.WARNING, "Message with the reference " + reference + " does not exist");
				return;
			} else if (message.equals("")) {
				return;
			}

			sender.sendMessage(prefix + message);

		} catch (NullPointerException e) {
			Bukkit.getLogger().warning("Could not find the message with the reference " + reference);
			sender.sendMessage(prefix + "Something went wrong with the message, alert your server admins");
		}

	}

	/**
	 * Used to send a formatted message
	 * 
	 * @param sender      the commandSender which the message should be sent to
	 * @param reference   the reference for the message
	 * @param replacement the value that the placeholder should be replaced with
	 */
	public static void sendMessageF(CommandSender sender, String reference, String... replacement) {
		try {
			String message = getMessage(sender, reference);
			if (message == null) {
				Bukkit.getLogger().log(Level.WARNING, "Message with the reference " + reference + " does not exist");
				return;
			} else if (message.equals("")) {
				return;
			}

			try {
				message = String.format(prefix + message, (Object[]) replacement);
			} catch (MissingFormatArgumentException e) {
				// expected error if the message does not contain %s
			}

			sender.sendMessage(message);
		} catch (NullPointerException e) {
			Bukkit.getLogger().warning("Could not find the message with the reference " + reference);
			sender.sendMessage(prefix + "Something went wrong with the message, alert your server admins");
		}
	}

	/**
	 * Used to send a formatted message
	 * 
	 * @param sender      the commandSender which the message should be sent to
	 * @param reference   the reference for the message
	 * @param replacement the value that the placeholder should be replaced with
	 */
	public static void sendMessageF(CommandSender sender, String reference, Object[] replacement) {
		try {
			String message = getMessage(sender, reference);
			if (message == null) {
				Bukkit.getLogger().log(Level.WARNING, "Message with the reference " + reference + " does not exist");
				return;
			} else if (message.equals("")) {
				return;
			}

			try {
				message = String.format(prefix + message, (Object[]) replacement);
			} catch (MissingFormatArgumentException e) {
				// expected error if the message does not contain %s
			}

			sender.sendMessage(message);
		} catch (NullPointerException e) {
			Bukkit.getLogger().warning("Could not find the message with the reference " + reference);
			sender.sendMessage(prefix + "Something went wrong with the message, alert your server admins");
		}
	}

	/**
	 * This is used to get the message from the provided location in the
	 * Configuration file, this does not add a prefix to the message
	 * 
	 * @param reference the reference for the message
	 * @return the message (without prefix)
	 */
	public static String getMessage(String reference) {

		try {
			String msg = messages.getString(reference);
			String colored = ChatColor.translateAlternateColorCodes('&', msg);
			return ColorUtil.translateColors(colored);
		} catch (NullPointerException e) {
			Bukkit.getLogger().warning("Could not find the message with the reference " + reference);
			return "";
		}
	}

	public static String getMessageF(String reference, String... replacement) {
		String message = getMessage(reference);

		return String.format(message, (Object[]) replacement);
	}

	public static String getMessage(CommandSender sender, String reference) {
		try {
			String msg = getMessage(reference);
			String colored = ChatColor.translateAlternateColorCodes('&', msg);
			return ColorUtil.translateColors(colored);
		} catch (NullPointerException e) {
			Bukkit.getLogger().warning("Could not find the message with the reference " + reference);
			return "";
		}
	}

	public static FileConfiguration getMessages() {
		return messages;
	}

	/**
	 * 
	 * @return the prefix for all messages Defaults to [BetterTeams] unless it is
	 *         changed by end user
	 */
	public static String getPrefix() {
		return prefix;
	}

	/**
	 * Used when you are sending a user a message instead of a message loaded from a
	 * file
	 * 
	 * @param sender  the player who sent the command
	 * @param message the message to send to that user
	 */
	public static void sendFullMessage(CommandSender sender, String message) {

		sender.sendMessage(prefix + message);
	}

	public static File getFile() {
		return new File("plugins/" + Main.name + "/" + lang + ".yml");
	}

}
