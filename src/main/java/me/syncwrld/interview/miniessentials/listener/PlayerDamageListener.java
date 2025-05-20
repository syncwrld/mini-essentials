package me.syncwrld.interview.miniessentials.listener;

import lombok.RequiredArgsConstructor;
import me.syncwrld.interview.miniessentials.service.GodService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

@RequiredArgsConstructor
public class PlayerDamageListener implements Listener {
	
	private final GodService godService;
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (godService.hasGod(player)) {
			event.setCancelled(true);
			event.setDamage(0);
		}
	}
	
	@EventHandler
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (godService.hasGod(player)) {
			event.setFoodLevel(20);
		}
	}
	
}
