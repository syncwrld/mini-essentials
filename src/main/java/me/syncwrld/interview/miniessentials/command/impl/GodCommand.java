package me.syncwrld.interview.miniessentials.command.impl;

import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.command.AbstractCommand;
import me.syncwrld.interview.miniessentials.service.GodService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand extends AbstractCommand {
	
	private final GodService godService;
	
	public GodCommand(MiniEssentialsPlugin plugin) {
		super("god", false, plugin);
		this.godService = plugin.godService();
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (!isPlayer(sender)) {
			message("messages", "god-invalid-self-target").send(sender);
			return;
		}
		
		Player player = (Player) sender;
		godService.toggleGod(player);
		
		boolean godState = godService.hasGod(player);
		message("messages", godState ? "god-enabled" : "god-disabled")
		  .send(player);
	}
}
