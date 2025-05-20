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

public class TpDenyCommand extends AbstractCommand {
	
	private final TpaService tpaService;
	
	public TpDenyCommand(MiniEssentialsPlugin plugin, TpaService tpaService) {
		super("tpdeny", true, plugin);
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
			message("messages", "tp-deny-incorrect-usage").send(sender);
			return;
		}
		
		String providedSender = args[0].toLowerCase();
		
		if (providedSender.equals("all")) {
			Set<TpaRequest> validRequests = requests.stream()
			  .filter(request ->
				request.sender() != null && request.sender().isOnline() &&
				!request.isExpired(tpaService.expiration()))
			  .collect(Collectors.toSet());
			
			validRequests.forEach(request -> {
				Player senderPlayer = request.sender();
				if (senderPlayer != null && senderPlayer.isOnline()) {
					message("messages", "tpa-request-denied-sender")
					  .addReplacement("$target", target.getName())
					  .send(senderPlayer);
				}
				tpaService.removeRequest(target, request);
			});
			
			message("messages", "tpa-request-denied-all")
			  .addReplacement("$amount", String.valueOf(validRequests.size()))
			  .send(target);
			
			return;
		}
		
		TpaRequest foundRequest = requests.stream()
		  .filter(req -> req.sender() != null && req.sender().getName().equalsIgnoreCase(providedSender))
		  .findFirst()
		  .orElse(null);
		
		if (foundRequest == null) {
			message("messages", "tpa-request-not-found")
			  .addReplacement("$requester", providedSender)
			  .send(target);
			return;
		}
		
		if (foundRequest.isExpired(tpaService.expiration())) {
			message("messages", "tpa-request-already-expired")
			  .addReplacement("$requester", providedSender)
			  .send(target);
			tpaService.removeRequest(target, foundRequest);
			return;
		}
		
		Player senderPlayer = foundRequest.sender();
		if (senderPlayer != null && senderPlayer.isOnline()) {
			message("messages", "tpa-request-denied-sender")
			  .addReplacement("$target", target.getName())
			  .send(senderPlayer);
		}
		
		message("messages", "tpa-request-denied-target")
		  .addReplacement("$requester", providedSender)
		  .send(target);
		
		tpaService.removeRequest(target, foundRequest);
	}
}
