package me.syncwrld.interview.miniessentials.command.impl;

import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.command.AbstractCommand;
import me.syncwrld.interview.miniessentials.service.TpaService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpRequestCommand extends AbstractCommand {
	
	private final TpaService tpaService;
	
	public TpRequestCommand(MiniEssentialsPlugin plugin, TpaService tpaService) {
		super("tpa", true, plugin);
		this.tpaService = tpaService;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (args.length < 1) {
			message("messages", "tpa-incorrect-usage").send(sender);
			return;
		}
		
		Player player = (Player) sender;
		final String targetName = args[0];
		Player target = Bukkit.getPlayerExact(targetName);
		
		if (target == null || !target.isOnline()) {
			message("messages", "invalid-player")
			  .addReplacement("$player", targetName)
			  .send(sender);
			return;
		}
		
		if (target.getName().equals(player.getName())) {
			message("messages", "tpa-request-self").send(sender);
			return;
		}
		
		if (!tpaService.createRequest(player, target)) {
			message("messages", "tpa-request-delay")
			  .addReplacement("$target", targetName)
			  .send(sender);
			return;
		}
		
		// sender message
		message("messages", "tpa-request-sent")
		  .addReplacement("$target", targetName)
		  .send(sender);
		
		// target message
		message("messages", "tpa-request-received")
		  .addReplacement("$requester", player.getName())
		  .send(target);
	}
}
