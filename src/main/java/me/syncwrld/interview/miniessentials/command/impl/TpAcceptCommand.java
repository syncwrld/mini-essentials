package me.syncwrld.interview.miniessentials.command.impl;

import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.command.AbstractCommand;
import me.syncwrld.interview.miniessentials.model.tpa.TpaRequest;
import me.syncwrld.interview.miniessentials.service.TpaService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TpAcceptCommand extends AbstractCommand {
	
	private final TpaService tpaService;
	
	public TpAcceptCommand(MiniEssentialsPlugin plugin, TpaService tpaService) {
		super("tpaccept", true, plugin);
		this.tpaService = tpaService;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		Player target = (Player) sender;
		List<TpaRequest> requests = tpaService.getRequests(target);
		
		if (requests.isEmpty()) {
			message("messages", "tpa-requests-no-pending").send(sender);
			return;
		}
		
		if (args.length == 0) {
			message("messages", "tp-accept-incorrect-usage").send(sender);
			return;
		}
		
		String providedSender = args[0].toLowerCase();
		
		if (providedSender.equals("all")) {
			final Set<TpaRequest> verifiedRequests = requests.stream().filter(tpaRequest ->
				tpaRequest.sender() != null && tpaRequest.sender().isOnline() &&
				tpaRequest.target() != null && tpaRequest.target().isOnline() &&
				!tpaRequest.isExpired(tpaService.expiration()))
			  .collect(Collectors.toSet());
			int count = verifiedRequests.size();
			
			verifiedRequests.forEach(tpaRequest -> {
				tpaService.acceptTpaRequest(sender, providedSender, tpaRequest, target, true);
			});
			message("messages", "tpa-request-accepted-all").addReplacement("$amount", "" + count);
			
			return;
		}
		
		TpaRequest senderRequest = requests.stream()
		  .filter(tpaRequest ->
			tpaRequest.sender() != null &&
			tpaRequest.sender().getName().equalsIgnoreCase(providedSender)
		  )
		  .findFirst()
		  .orElse(null);
		tpaService.acceptTpaRequest(sender, providedSender, senderRequest, target, false);
	}
	
	
}
