package me.syncwrld.interview.miniessentials.command.impl;

import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class FixToolCommand extends AbstractCommand {
	
	public FixToolCommand(MiniEssentialsPlugin plugin) {
		super("fix", true, plugin);
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] arguments) {
		final Player player = (Player) sender;
		final ItemStack item = player.getInventory().getItemInMainHand();
		final Material itemType = item.getType();
		
		if (itemType == Material.AIR) {
			message("messages", "fix-tool-requirement").send(player);
			return;
		}
		
		Damageable damageable = (Damageable) item.getItemMeta();
		final short maxDurability = itemType.getMaxDurability();
		
		assert damageable != null;
		if (maxDurability - damageable.getDamage() <= 0) {
			message("messages", "fix-tool-not-damaged").send(player);
			return;
		}
		
		damageable.setDamage(0);
		item.setItemMeta(damageable);
		
		message("messages", "fix-tool-repaired").send(player);
	}
	
}
