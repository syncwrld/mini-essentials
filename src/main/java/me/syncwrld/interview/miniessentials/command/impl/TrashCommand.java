package me.syncwrld.interview.miniessentials.command.impl;

import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrashCommand extends AbstractCommand {
	
	public TrashCommand(MiniEssentialsPlugin plugin) {
		super("trash", true, plugin);
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] arguments) {
		final Player player = (Player) sender;
		player.openInventory(
		  Bukkit.createInventory(null, 36, "Trash can")
		);
		player.playSound(player, Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
	}
	
}
