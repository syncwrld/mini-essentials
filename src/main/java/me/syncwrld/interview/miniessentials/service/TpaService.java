package me.syncwrld.interview.miniessentials.service;

import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.model.tpa.TpaRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;


public class TpaService {
	
	private final MiniEssentialsPlugin plugin;
	private final Map<UUID, List<TpaRequest>> requestsByTarget = new HashMap<>();
	private long expirationSeconds = 60L;
	
	public TpaService(MiniEssentialsPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void expiration(long expirationSeconds) {
		this.expirationSeconds = expirationSeconds;
	}
	
	public void load() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(
		  plugin,
		  this::cleanupExpiredRequests,
		  20L, 20L
		);
	}
	
	public boolean hasPendingRequestBetween(Player sender, Player target) {
		return getRequests(target).stream()
		  .anyMatch(
			request -> request.sender().getUniqueId().equals(sender.getUniqueId())
		  );
	}
	
	public boolean createRequest(Player sender, Player target) {
		if (hasPendingRequestBetween(sender, target)) return false;
		
		TpaRequest request = new TpaRequest(sender, target);
		requestsByTarget
		  .computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>())
		  .add(request);
		return true;
	}
	
	public List<TpaRequest> getRequests(Player target) {
		return requestsByTarget.getOrDefault(target.getUniqueId(), Collections.emptyList());
	}
	
	public void removeRequest(Player target, TpaRequest request) {
		List<TpaRequest> list = requestsByTarget.get(target.getUniqueId());
		if (list != null) {
			list.remove(request);
			if (list.isEmpty()) {
				requestsByTarget.remove(target.getUniqueId());
			}
		}
	}
	
	private void cleanupExpiredRequests() {
		for (UUID targetId : new HashSet<>(requestsByTarget.keySet())) {
			List<TpaRequest> list = requestsByTarget.get(targetId);
			if (list == null) continue;
			
			Iterator<TpaRequest> it = list.iterator();
			while (it.hasNext()) {
				TpaRequest req = it.next();
				if (req.isExpired(expirationSeconds)) {
					Player sender = req.sender();
					if (sender.isOnline()) {
						plugin.languages().find("messages", "tpa-request-expired")
						  .addReplacement("$target", req.target().getName())
						  .send(sender);
					}
					it.remove();
				}
			}
			
			if (list.isEmpty()) {
				requestsByTarget.remove(targetId);
			}
		}
	}
	
	public void acceptTpaRequest(CommandSender sender, String providedSender, TpaRequest accepted, Player target, boolean all) {
		Player requestSender = Bukkit.getPlayerExact(providedSender);
		if (requestSender == null || !requestSender.isOnline()) {
			plugin.languages().find("messages", "invalid-player")
			  .addReplacement("$player", providedSender)
			  .send(sender);
			return;
		}
		
		if (accepted == null) {
			plugin.languages().find("messages", "tpa-request-not-found")
			  .addReplacement("$requester", providedSender)
			  .send(target);
			return;
		}
		
		if (accepted.isExpired(60)) {
			plugin.languages().find("messages", "tpa-request-already-expired")
			  .addReplacement("$requester", providedSender)
			  .send(target);
			this.removeRequest(target, accepted);
			return;
		}
		
		
		// requester/sender message
		plugin.languages().find("messages", "tpa-request-accepted-sender")
		  .addReplacement("$target", target.getName())
		  .send(requestSender);
		
		// target message
		if (!all) {
			plugin.languages().find("messages", "tpa-request-accepted-target")
			  .addReplacement("$requester", requestSender.getName())
			  .send(target);
		}
		
		requestSender.teleport(target);
		this.removeRequest(target, accepted);
	}
	
	public long expiration() {
		return expirationSeconds;
	}
	
}
