package me.syncwrld.interview.miniessentials.service;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GodService {
	
	private final Set<UUID> godPlayers = new HashSet<>();
	
	public void toggleGod(Player player) {
		if (hasGod(player)) {
			godPlayers.remove(player.getUniqueId());
			return;
		}
		godPlayers.add(player.getUniqueId());
	}
	
	public boolean hasGod(Player player) {
		return godPlayers.contains(player.getUniqueId());
	}
	
	public void clear() {
		godPlayers.clear();
	}
	
}
