package me.syncwrld.interview.miniessentials.command.impl;

import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class GamemodeCommand extends AbstractCommand {
	
	public GamemodeCommand(MiniEssentialsPlugin plugin) {
		super("gamemode", false, plugin);
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (args.length == 0) {
			message("messages", "gamemode-incorrect-usage").send(sender);
			return;
		}
		
		Optional<GameMode> gamemodeOpt = findGamemode(args[0]);
		if (gamemodeOpt.isEmpty()) {
			message("messages", "gamemode-invalid-mode")
			  .addReplacement("$mode", args[0])
			  .send(sender);
			return;
		}
		
		GameMode gamemode = gamemodeOpt.get();
		
		// /gamemode <mode>
		if (args.length == 1) {
			if (!isPlayer(sender)) {
				message("messages", "gamemode-invalid-self-target").send(sender);
				return;
			}
			
			Player player = (Player) sender;
			player.setGameMode(gamemode);
			
			message("messages", "gamemode-changed-self")
			  .addReplacement("$mode", "$gamemode." + gamemode.name().toLowerCase())
			  .send(player);
			return;
		}
		
		// /gamemode <mode> <player>
		String targetName = args[1];
		Player targetPlayer = Bukkit.getPlayerExact(targetName);
		
		if (targetPlayer == null || !targetPlayer.isOnline()) {
			message("messages", "invalid-player")
			  .addReplacement("$player", targetName)
			  .send(sender);
			return;
		}
		
		targetPlayer.setGameMode(gamemode);
		
		message("messages", "gamemode-changed-self")
		  .addReplacement("$mode", "$gamemode." + gamemode.name().toLowerCase())
		  .send(targetPlayer);
		
		message("messages", "gamemode-changed-other")
		  .addReplacement("$player", targetName)
		  .addReplacement("$mode", "$gamemode." + gamemode.name().toLowerCase())
		  .send(sender);
	}
	
	private Optional<GameMode> findGamemode(String input) {
		return switch (input.toLowerCase()) {
			case "s", "0" -> Optional.of(GameMode.SURVIVAL);
			case "c", "1" -> Optional.of(GameMode.CREATIVE);
			case "a", "2" -> Optional.of(GameMode.ADVENTURE);
			case "sc", "3" -> Optional.of(GameMode.SPECTATOR);
			default -> {
				try {
					yield Optional.of(GameMode.valueOf(input.toUpperCase()));
				} catch (IllegalArgumentException e) {
					yield Optional.empty();
				}
			}
		};
	}
	
}
