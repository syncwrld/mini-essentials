package me.syncwrld.interview.miniessentials.util.logging;

import com.google.common.base.Strings;
import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.util.config.YAML;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

public class PluginLogger {
	
	private final MiniEssentialsPlugin plugin;
	private final String pluginName;
	
	private String MESSAGE_FORMAT = "[${pluginName} v${pluginVersion}] ${coloredLevel} ${message}";
	
	public PluginLogger(MiniEssentialsPlugin plugin) {
		this.plugin = plugin;
		this.pluginName = plugin.getDescription().getName();
	}
	
	public void log(LogLevel level, String message) {
		log(level, null, message);
	}
	
	public void info(String... messages) {
		log(LogLevel.INFO, null, messages);
	}
	
	public void warn(String... messages) {
		log(LogLevel.WARNING, null, messages);
	}
	
	public void error(Exception exception, String... messages) {
		log(LogLevel.ERROR, exception, messages);
	}
	
	public void error(String... messages) {
		log(LogLevel.ERROR, null, messages);
	}
	
	public void debug(String... messages) {
		final YAML configuration = plugin.configuration();
		if (configuration != null && configuration.getBoolean("options.debug")) {
			printFormattedCollection(LogLevel.DEBUG, messages);
		}
	}
	
	public void format(String format) {
		MESSAGE_FORMAT = format;
	}
	
	public String currentFormat() {
		return MESSAGE_FORMAT;
	}
	
	private void log(LogLevel level, Throwable throwable, String... messages) {
		if (level == LogLevel.DEBUG) {
			debug(messages);
			return;
		}
		
		printFormattedCollection(level, messages);
		
		if (throwable != null) {
			throw new RuntimeException("An error had occurred (" + level.name() + "):", throwable);
		}
	}
	
	private void printFormatted(LogLevel level, String message) {
		Bukkit.getConsoleSender()
		  .sendMessage(
			formatMessage(level, message)
		  );
	}
	
	private void printFormattedCollection(LogLevel level, String... messages) {
		if (messages.length == 1) {
			printFormatted(level, messages[0]);
			return;
		}
		
		String[] formattedMessages = new String[messages.length];
		for (int i = 0; i < messages.length; i++) {
			final String message = messages[i];
			formattedMessages[i] = Strings.isNullOrEmpty(message) ? "" : formatMessage(level, message);
		}
		
		Bukkit.getConsoleSender().sendMessage(formattedMessages);
	}
	
	private @NotNull String formatMessage(LogLevel level, String message) {
		final PluginDescriptionFile pluginDescription = plugin.getDescription();
		final String apiVersion = getApiVersion(pluginDescription);
		final String authors = String.join(", ", pluginDescription.getAuthors());
		
		return (
		  currentFormat()
			.replace("${pluginName}", pluginName)
			.replace("${pluginMain}", pluginDescription.getMain())
			.replace("${pluginAuthors}", Strings.isNullOrEmpty(authors) ? "unknown" : authors)
			.replace("${pluginApiVersion}", Strings.isNullOrEmpty(apiVersion) ? "unknown" : apiVersion)
			.replace("${pluginVersion}", pluginDescription.getVersion())
			.replace("${pluginFullName}", pluginDescription.getFullName())
			.replace("${coloredLevel}", level.getValue())
			.replace("${message}", level.getMessageColor() + message)
		)
		  .replace("&", "§");
	}
	
	private String getApiVersion(PluginDescriptionFile pluginDescription) {
		try {
			return pluginDescription.getAPIVersion();
		} catch (NoSuchMethodError e) {
			return "legacy (apiVersion < 1.13.2)";
		}
	}
	
}
