package me.syncwrld.interview.miniessentials.command.impl;

import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderchestCommand extends AbstractCommand {
	
	public EnderchestCommand(MiniEssentialsPlugin plugin) {
		super("enderchest", true, plugin);
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] arguments) {
		final Player senderPlayer = (Player) sender;
		
		if (arguments.length == 0) {
			senderPlayer.openInventory(senderPlayer.getEnderChest());
			return;
		}
		
		final String targetName = arguments[0];
		final Player targetPlayer = Bukkit.getPlayerExact(targetName);
		
		if (targetPlayer == null || !targetPlayer.isOnline()) {
			message("messages", "invalid-player")
			  .addReplacement("$player", targetName)
			  .send(sender);
			return;
		}
		
		senderPlayer.closeInventory();
		senderPlayer.openInventory(targetPlayer.getEnderChest());
	}
	
}
