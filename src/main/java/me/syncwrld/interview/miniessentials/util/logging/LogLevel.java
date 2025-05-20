package me.syncwrld.interview.miniessentials.util.logging;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum LogLevel {
	DEBUG("&d[DEBUG]", ChatColor.LIGHT_PURPLE),
	INFO("&f[INFO]", ChatColor.AQUA),
	WARNING("&e[WARNING]", ChatColor.WHITE),
	ERROR("&c[ERROR]", ChatColor.WHITE),
	SECURITY("&4[SECURITY]", ChatColor.RED);
	
	private final String value;
	private final ChatColor messageColor;
	
	LogLevel(String value, ChatColor messageColor) {
		this.value = value;
		this.messageColor = messageColor;
	}
}
