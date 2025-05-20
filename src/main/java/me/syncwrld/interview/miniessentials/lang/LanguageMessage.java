package me.syncwrld.interview.miniessentials.lang;

import lombok.Getter;
import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class LanguageMessage {
	
	private static final String DEFAULT_NOT_FOUND = "§cMessage not found. §7(${langKey})";
	
	private final MiniEssentialsPlugin plugin;
	private final String key;
	private final Object messageValue;
	private final boolean isString;
	private final Map<String, String> replacements = new HashMap<>();
	private final String fallbackMessage = DEFAULT_NOT_FOUND;
	
	public LanguageMessage(MiniEssentialsPlugin plugin, String key, Object messageValue) {
		this.plugin = plugin;
		this.key = key;
		this.messageValue = messageValue;
		this.isString = messageValue instanceof String;
	}
	
	public LanguageMessage addReplacement(String placeholder, String replacement) {
		if (messageValue == null) return new LanguageMessage(plugin, key, "not_found_message");
		
		var newMessage = new LanguageMessage(plugin, key, messageValue);
		newMessage.replacements.putAll(this.replacements);
		newMessage.replacements.put(placeholder, replacement);
		return newMessage;
	}
	
	public void send(CommandSender sender) {
		send(sender, true);
	}
	
	public void send(CommandSender sender, boolean applyPlaceholders) {
		if (messageValue == null || "not_found_message".equals(messageValue)) {
			sender.sendMessage(fallbackMessage.replace("${langKey}", key));
			return;
		}
		
		Object genericMessage = applyPlaceholders ? processMessage() : applyReplacements(messageValue);
		
		if (genericMessage instanceof String messageString) {
			sender.sendMessage(messageString);
			return;
		}
		
		if (genericMessage instanceof List<?> messageList) {
			messageList.stream()
			  .filter(String.class::isInstance)
			  .map(String.class::cast)
			  .forEach(sender::sendMessage);
		}
	}
	
	private Object processMessage() {
		Object replaced = applyReplacements(messageValue);
		
		if (replaced instanceof String replacedString) {
			return color(replacedString);
		}
		
		if (replaced instanceof List<?> replacedStringList) {
			return replacedStringList.stream()
			  .filter(String.class::isInstance)
			  .map(String.class::cast)
			  .map(this::color)
			  .toList();
		}
		
		return replaced;
	}
	
	private Object applyReplacements(Object message) {
		if (message == null || replacements.isEmpty()) return message;
		
		if (message instanceof String messageString) {
			for (var entry : replacements.entrySet()) {
				messageString = plugin.languages().applyPlaceholders(messageString.replace(entry.getKey(), entry.getValue()));
			}
			return messageString;
		}
		
		if (message instanceof List<?> messageList) {
			return messageList.stream()
			  .filter(String.class::isInstance)
			  .map(String.class::cast)
			  .map(line -> {
				  for (var entry : replacements.entrySet()) {
					  line = plugin.languages().applyPlaceholders(line.replace(entry.getKey(), entry.getValue()));
				  }
				  return line;
			  })
			  .toList();
		}
		
		return message;
	}
	
	private String color(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
}
