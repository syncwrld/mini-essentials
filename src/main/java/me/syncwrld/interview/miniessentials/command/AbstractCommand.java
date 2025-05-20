package me.syncwrld.interview.miniessentials.command;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.experimental.Accessors;
import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.lang.LanguageMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Data
@Accessors(fluent = true)
public abstract class AbstractCommand implements CommandExecutor {
	
	private final String commandKey;
	private final boolean onlyPlayers;
	private final MiniEssentialsPlugin plugin;
	
	private String permissionNode = null;
	private boolean enabled = true;
	
	public void permissionNode(String permissionNode) {
		this.permissionNode = permissionNode;
	}
	
	public String permissionNode() {
		return permissionNode;
	}
	
	public void enabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}
	
	public LanguageMessage message(String namespace, String key) {
		return plugin.languages().find(namespace, key);
	}
	
	private boolean hasPermission(CommandSender sender) {
		return Strings.isNullOrEmpty(permissionNode()) || sender.hasPermission(permissionNode());
	}
	
	public boolean checkForPermission(CommandSender sender) {
		if (!hasPermission(sender)) {
			message("messages", "insufficient-permissions").send(sender);
			return false;
		}
		return true;
	}
	
	public boolean checkForPlayer(CommandSender sender) {
		if (!isPlayer(sender)) {
			message("messages", "only-players-execution").send(sender);
			return false;
		}
		return true;
	}
	
	public abstract void onCommand(CommandSender sender, String[] arguments);
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!enabled) {
			message("messages", "command-disabled").send(sender);
			return false;
		}
		
		if (!checkForPermission(sender)) return false;
		if (onlyPlayers && !checkForPlayer(sender)) return false;
		
		onCommand(sender, args);
		return true;
	}
	
}
